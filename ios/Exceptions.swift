import ExpoModulesCore

internal final class InvalidArgumentsException: Exception {
    private let message: String

    init(_ message: String) {
        self.message = message
        super.init()
    }

    override var reason: String {
        "Invalid arguments: \(message)"
    }
}

internal class DownloadInProgressException: Exception {
    override var reason: String {
        "Different download is in progress. Await other download to complete"
    }
}

internal final class MissingCurrentViewControllerException: Exception {
    override var reason: String {
        "Cannot determine currently presented view controller"
    }
}

internal final class UserCancelledException: Exception {
    override var reason: String {
        "User cancelled"
    }
}
