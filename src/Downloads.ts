import { NativeModule, requireNativeModule } from "expo";
import { PermissionResponse } from "expo-modules-core";

import { DownloadResponse, IDownloadsModule } from "./Downloads.types";

declare class DownloadsModule extends NativeModule implements IDownloadsModule {
  saveToDownloads(fileName: string, mimeType: string, base64Data: string): Promise<DownloadResponse>;
  openDownloadFile(uri: string, mimeType: string): Promise<void>;
  getPermissionsAsync?: () => Promise<PermissionResponse>;
  requestPermissionsAsync?: () => Promise<PermissionResponse>;
}

export default requireNativeModule<DownloadsModule>("Downloads");
