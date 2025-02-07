import { PermissionResponse } from "expo-modules-core";

export interface IDownloadsModule {
  saveToDownloads(fileName: string, mimeType: string, base64Data: string): Promise<DownloadResponse>;
  getPermissionsAsync?: () => Promise<PermissionResponse>;
  requestPermissionsAsync?: () => Promise<PermissionResponse>;
}

export type DownloadResponse =
  | {
      uri: string;
      cancelled: false;
    }
  | {
      cancelled: true;
    };
