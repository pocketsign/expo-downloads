import ExpoModulesCore

internal final class InvalidArgumentsException: Exception {
    private let detail: String

    init(_ detail: String) {
        self.detail = detail
        super.init()
    }

    override var reason: String {
        "Invalid arguments: \(detail)"
    }
}

internal final class DownloadInProgressException: Exception {
    override var reason: String {
        "Different download is in progress. Await other download to complete"
    }
}

internal final class MissingViewControllerException: Exception {
    override var reason: String {
        "Cannot determine currently presented view controller"
    }
}

internal final class FileOpenException: Exception {
    override var reason: String {
        "Failed to open file"
    }
}
