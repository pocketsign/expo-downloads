import UIKit
import ExpoModulesCore

internal protocol OpeningFileResultHandler {
    func didEndPreview()
}

internal final class OpeningFileDelegate: NSObject, UIDocumentInteractionControllerDelegate {
    private let handler: OpeningFileResultHandler
    private let viewController: UIViewController

    init(handler: OpeningFileResultHandler, viewController: UIViewController) {
        self.handler = handler
        self.viewController = viewController
    }
    
    func documentInteractionControllerViewControllerForPreview(_ controller: UIDocumentInteractionController) -> UIViewController {
        return viewController
    }
    
    func documentInteractionControllerDidEndPreview(_ controller: UIDocumentInteractionController) {
        handler.didEndPreview()
    }
} 
