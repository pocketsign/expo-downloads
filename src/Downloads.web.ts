import { registerWebModule, NativeModule } from "expo";

import { DownloadResponse, IDownloadsModule } from "./Downloads.types";

class DownloadsModule extends NativeModule implements IDownloadsModule {
  async saveToDownloads(
    fileName: string,
    mimeType: string,
    base64Data: string
  ): Promise<DownloadResponse> {
    // Base64 デコードしてバイナリデータ取得
    const byteCharacters = atob(base64Data);
    const byteNumbers = new Array(byteCharacters.length);
    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], { type: mimeType });

    // Blob から Object URL を生成
    const objectUrl = URL.createObjectURL(blob);

    // 自動ダウンロードをトリガーするためのリンクを作成してクリック
    const link = document.createElement("a");
    link.href = objectUrl;
    link.setAttribute("download", fileName);
    link.innerHTML = "downloading...";
    link.style.display = "none";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    return { uri: objectUrl, cancelled: false };
  }
}

export default registerWebModule(DownloadsModule);
