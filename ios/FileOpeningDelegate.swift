import UIKit
import ExpoModulesCore

public final class FileOpeningDelegate: NSObject, UIDocumentInteractionControllerDelegate {
    let promise: Promise
    let viewController: UIViewController
    
    public init(promise: Promise, viewController: UIViewController) {
        self.promise = promise
        self.viewController = viewController
    }
    
    public func documentInteractionControllerViewControllerForPreview(_ controller: UIDocumentInteractionController) -> UIViewController {
        return viewController
    }
    
    public func documentInteractionControllerDidEndPreview(_ controller: UIDocumentInteractionController) {
        promise.resolve(nil)
    }
} 
