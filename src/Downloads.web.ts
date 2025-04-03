import { registerWebModule, NativeModule } from "expo";

import { SaveFileResponse, IDownloadsModule, SaveFileOptions } from "./Downloads.types";

class DownloadsModule extends NativeModule implements IDownloadsModule {
  async saveFile({ fileName, mimeType, base64Data }: SaveFileOptions): Promise<SaveFileResponse> {
    const byteCharacters = atob(base64Data);
    const byteArray = new Uint8Array(byteCharacters.length);
    for (let i = 0; i < byteCharacters.length; i++) {
      byteArray[i] = byteCharacters.charCodeAt(i);
    }
    const blob = new Blob([byteArray], { type: mimeType });

    const objectUrl = URL.createObjectURL(blob);

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
