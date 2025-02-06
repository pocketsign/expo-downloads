import { NativeModule, requireNativeModule } from "expo";

import { DownloadsModuleInterface } from "./Downloads.types";

declare class DownloadsModule
  extends NativeModule
  implements DownloadsModuleInterface
{
  saveToDownloads(
    fileName: string,
    mimeType: string,
    base64Data: string
  ): Promise<string>;
}

export default requireNativeModule<DownloadsModule>("Downloads");
