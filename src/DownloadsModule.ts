import { NativeModule, requireNativeModule } from "expo";

import { DownloadsModuleEvents } from "./Downloads.types";

declare class DownloadsModule extends NativeModule<DownloadsModuleEvents> {
  PI: number;
  hello(): string;
  setValueAsync(value: string): Promise<void>;
}

// This call loads the native module object from the JSI.
export default requireNativeModule<DownloadsModule>("Downloads");
