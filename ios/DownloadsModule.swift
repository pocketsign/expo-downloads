import ExpoModulesCore

struct DownloadingContext {
    let promise: Promise
    let delegate: DownloadingDelegate
}

public final class DownloadsModule: Module, DownloadResultHandler {
    private var downloadingContext: DownloadingContext?

    public func definition() -> ModuleDefinition {
        Name("Downloads")

        AsyncFunction("saveToDownloads") {
            (fileName: String, mimeType: String, base64Data: String, promise: Promise) in
            if downloadingContext != nil {
                throw DownloadInProgressException()
            }
            if fileName.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                throw InvalidArgumentsException("fileName cannot be blank")
            }
            if mimeType.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty || !mimeType.contains("/") {
                throw InvalidArgumentsException("mimeType format is invalid")
            }
            if base64Data.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                throw InvalidArgumentsException("base64Data cannot be blank")
            }
            let nonWhitespace = base64Data.filter { !$0.isWhitespace }
            if nonWhitespace.count % 4 != 0 {
                throw InvalidArgumentsException("base64Data length (ignoring whitespace) must be a multiple of 4")
            }

            guard let data = Data(base64Encoded: base64Data) else {
                throw InvalidArgumentsException("base64Data is invalid")
            }

            guard let viewController = appContext?.utilities?.currentViewController() else {
                throw MissingViewControllerException()
            }

            let tempDir = FileManager.default.temporaryDirectory
            let tempFileURL = tempDir.appendingPathComponent(fileName)
            try data.write(to: tempFileURL)

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
}
