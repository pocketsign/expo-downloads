import ExpoModulesCore

public class DownloadsModule: Module, UIDocumentPickerDelegate {
  public func definition() -> ModuleDefinition {
    Name("Downloads")

    AsyncFunction("saveToDownloads") { (fileName: String, mimeType: String, base64Data: String) async throws -> String in
      // 引数の検証（Android実装と同様のチェック）
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
      
      // Base64 デコードしてデータ取得
      guard let data = Data(base64Encoded: base64Data) else {
        throw InvalidArgumentsException("base64Data is invalid")
      }
      
      // 一時ファイルのパス作成
      let tempDir = FileManager.default.temporaryDirectory
      let tempFileURL = tempDir.appendingPathComponent(fileName)
      try data.write(to: tempFileURL)
      
      // UIDocumentPicker を用いてエクスポート（ファイル保存）させる
      return try await withCheckedThrowingContinuation { continuation in
        DispatchQueue.main.async {
          guard let viewController = self.appContext.reactViewController else {
            continuation.resume(throwing: MissingCurrentViewControllerException())
            return
          }
          let picker: UIDocumentPickerViewController
          if #available(iOS 15.0, *) {
            picker = UIDocumentPickerViewController(forExporting: [tempFileURL], asCopy: true)
          } else {
            picker = UIDocumentPickerViewController(url: tempFileURL, in: .exportToService)
          }
          picker.delegate = self
          self.saveToDownloadsContinuation = continuation
          viewController.present(picker, animated: true, completion: nil)
        }
      }
    }
  }

  // saveToDownloads の Promise 解決用 continuation を保持します（シンプルな実装のため、並列呼び出しへの対応は省略）
  private var saveToDownloadsContinuation: CheckedContinuation<String, Error>?
}

// MARK: - UIDocumentPickerDelegate 実装
extension DownloadsModule: UIDocumentPickerDelegate {
  public func documentPickerWasCancelled(_ controller: UIDocumentPickerViewController) {
    if let continuation = self.saveToDownloadsContinuation {
      continuation.resume(throwing: UserCancelledException())
      self.saveToDownloadsContinuation = nil
    }
  }
  
  public func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
    if let continuation = self.saveToDownloadsContinuation {
      // 解決時は選択された最初の URL の文字列を返します
      continuation.resume(returning: urls.first?.absoluteString ?? "")
      self.saveToDownloadsContinuation = nil
    }
  }
}
