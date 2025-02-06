import { registerWebModule, NativeModule } from "expo";

import { DownloadsModuleInterface } from "./Downloads.types";

class DownloadsModule extends NativeModule implements DownloadsModuleInterface {
  async saveToDownloads(
    fileName: string,
    mimeType: string,
    base64Data: string
  ): Promise<string> {
    try {
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

      return objectUrl;
    } catch (error) {
      return Promise.reject(error);
    }
  }
}

export default registerWebModule(DownloadsModule);
