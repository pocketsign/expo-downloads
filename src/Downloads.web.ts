import { registerWebModule, NativeModule } from "expo";

import { SaveFileResponse, IDownloadsModule, SaveFileOptions } from "./Downloads.types";

class DownloadsModule extends NativeModule implements IDownloadsModule {
  async saveFile({ name, type, data, encoding }: SaveFileOptions): Promise<SaveFileResponse> {
    const blob = (() => {
      if (encoding === "base64") {
        const byteCharacters = atob(data);
        const byteArray = new Uint8Array(byteCharacters.length);
        for (let i = 0; i < byteCharacters.length; i++) {
          byteArray[i] = byteCharacters.charCodeAt(i);
        }
        return new Blob([byteArray], { type });
      }
      return new Blob([data], { type });
    })();

    const objectUrl = URL.createObjectURL(blob);

    const link = document.createElement("a");
    link.href = objectUrl;
    link.setAttribute("download", name);
    link.innerHTML = "downloading...";
    link.style.display = "none";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    return { uri: objectUrl, cancelled: false };
  }
}

export default registerWebModule(DownloadsModule);
