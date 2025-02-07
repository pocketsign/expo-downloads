import { NativeModule, requireNativeModule } from "expo";

import { DownloadResponse, IDownloadsModule } from "./Downloads.types";

declare class DownloadsModule extends NativeModule implements IDownloadsModule {
  saveToDownloads(
    fileName: string,
    mimeType: string,
    base64Data: string
  ): Promise<DownloadResponse>;
}

export default requireNativeModule<DownloadsModule>("Downloads");
