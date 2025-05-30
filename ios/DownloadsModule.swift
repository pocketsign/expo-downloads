import ExpoModulesCore
import UIKit

internal struct DownloadingContext {
    let promise: Promise
    let delegate: DownloadingDelegate
}

internal struct OpeningFileContext {
    let promise: Promise
    let delegate: OpeningFileDelegate
}

public final class DownloadsModule: Module, DownloadResultHandler, OpeningFileResultHandler {
    private var downloadingContext: DownloadingContext?
    private var openingFileContext: OpeningFileContext?

    public func definition() -> ModuleDefinition {
        Name("Downloads")

        AsyncFunction("saveFile") { (options: SaveFileOptions, promise: Promise) in
            if downloadingContext != nil {
                throw DownloadInProgressException()
            }

            let name = options.name.trimmingCharacters(in: .whitespacesAndNewlines)
            let data = options.data.trimmingCharacters(in: .whitespacesAndNewlines)
            let encoding = options.encoding ?? .utf8

            if name.isEmpty {
                throw InvalidArgumentsException("name cannot be blank")
            }
            if data.isEmpty {
                throw InvalidArgumentsException("data cannot be blank")
            }
            guard let encodedData = (encoding == .base64 ? Data(base64Encoded: data) : data.data(using: .utf8)) else {
                throw InvalidArgumentsException("data is invalid")
            }

            guard let viewController = appContext?.utilities?.currentViewController() else {
                throw MissingViewControllerException()
            }

            let tempDir = FileManager.default.temporaryDirectory
            let tempFileURL = tempDir.appendingPathComponent(name)
            try encodedData.write(to: tempFileURL)

            Task.detached { @MainActor in
                let picker: UIDocumentPickerViewController
                if #available(iOS 15.0, *) {
                    picker = UIDocumentPickerViewController(forExporting: [tempFileURL], asCopy: true)
                } else {
                    picker = UIDocumentPickerViewController(url: tempFileURL, in: .exportToService)
                }
                let delegate = DownloadingDelegate(handler: self)

                self.downloadingContext = DownloadingContext(promise: promise, delegate: delegate)
                picker.delegate = delegate

                viewController.present(picker, animated: true)
            }
        }

        AsyncFunction("openFile") { (options: OpenFileOptions, promise: Promise) in
            guard let url = URL(string: options.uri) else {
                throw InvalidArgumentsException("Invalid URL: \(options.uri)")
            }
            if url.scheme?.lowercased() != "file" {
                throw InvalidArgumentsException("Invalid URL scheme: \(options.uri)")
            }

            guard let viewController = self.appContext?.utilities?.currentViewController() else {
                throw MissingViewControllerException()
            }

            Task.detached { @MainActor in
                let controller = UIDocumentInteractionController(url: url)
                let delegate = OpeningFileDelegate(handler: self, viewController: viewController)
                controller.delegate = delegate
                self.openingFileContext = OpeningFileContext(promise: promise, delegate: delegate)
                if !controller.presentPreview(animated: true) {
                    promise.reject(FileOpenException())
                    self.openingFileContext = nil
                }
            }
        }
    }

    func didDownloadAt(urls: [URL]) {
        guard let promise = self.downloadingContext?.promise else {
            log.error("downloadingContext has been lost.")
            return
        }
        downloadingContext = nil
        promise.resolve(DownloadResponse(uri: urls.first?.absoluteString ?? ""))
    }

    func didCancel() {
        guard let promise = self.downloadingContext?.promise else {
            log.error("downloadingContext has been lost.")
            return
        }
        downloadingContext = nil
        promise.resolve(DownloadResponse(cancelled: true))
    }

    func didEndPreview() {
        guard let promise = self.openingFileContext?.promise else {
            log.error("openingFileContext has been lost.")
            return
        }
        openingFileContext = nil
        promise.resolve(nil)
    }
}
