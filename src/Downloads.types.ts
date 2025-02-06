export interface DownloadsModuleInterface {
  /**
   * Downloads フォルダにファイルを保存します
   * @param fileName 保存するファイル名
   * @param mimeType ファイルの MIME タイプ
   * @param base64Data ファイルデータの Base64 エンコード文字列
   * @returns 保存されたファイルの URL を返す Promise
   */
  saveToDownloads(
    fileName: string,
    mimeType: string,
    base64Data: string
  ): Promise<string>;
}
