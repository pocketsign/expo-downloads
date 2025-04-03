import { NativeModule, requireNativeModule } from "expo";
import { PermissionResponse } from "expo-modules-core";

import { SaveFileResponse, IDownloadsModule, OpenFileOptions, SaveFileOptions } from "./Downloads.types";

declare class DownloadsModule extends NativeModule implements IDownloadsModule {
  saveFile(options: SaveFileOptions): Promise<SaveFileResponse>;
  openFile(options: OpenFileOptions): Promise<void>;
  getPermissionsAsync?: () => Promise<PermissionResponse>;
  requestPermissionsAsync?: () => Promise<PermissionResponse>;
}

export default requireNativeModule<DownloadsModule>("Downloads");
