import ExpoModulesCore

internal class InvalidArgumentsException: Exception {
  private let message: String

  init(_ message: String) {
    self.message = message
  }

  override var reason: String {
    "Invalid arguments: \(message)"
  }
}

internal class MissingCurrentViewControllerException: Exception {
  override var reason: String {
    "Cannot determine currently presented view controller"
  }
}

internal class UserCancelledException: Exception {
  override var reason: String {
    "User cancelled"
  }
}
