import UIKit

protocol DownloadResultHandler {
    func didDownloadAt(urls: [URL])
    func didCancel()
}

internal class DownloadingDelegate: NSObject, UIDocumentPickerDelegate {
    private let handler: DownloadResultHandler

    init(handler: DownloadResultHandler) {
        self.handler = handler
    }

    func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
        self.handler.didDownloadAt(urls: urls)
    }

    func documentPickerWasCancelled(_ controller: UIDocumentPickerViewController) {
        self.handler.didCancel()
    }

    func presentationControllerDidDismiss(_ presentationController: UIPresentationController) {
        self.handler.didCancel()
    }
}
