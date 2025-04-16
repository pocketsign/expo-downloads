import { PermissionResponse } from "expo-modules-core";

export interface IDownloadsModule {
  saveFile: (options: SaveFileOptions) => Promise<SaveFileResponse>;
  openFile?: (options: OpenFileOptions) => Promise<void>;
  getPermissionsAsync?: () => Promise<PermissionResponse>;
  requestPermissionsAsync?: () => Promise<PermissionResponse>;
}

/**
 * File save response
 */
export type SaveFileResponse =
  | {
      /** URI of the saved file */
      uri: string;
      /** Not cancelled */
      cancelled: false;
    }
  | {
      /** Cancelled */
      cancelled: true;
    };

/**
 * File save options
 */
export type SaveFileOptions = {
  /** File name to save */
  name: string;
  /** MIME type of the file */
  type: string;
  /** File data */
  data: string;
  /** File encoding @default "utf8" */
  encoding?: "base64" | "utf8";
};

/**
 * Options to open a file
 */
export type OpenFileOptions = {
  /** URI of the file to open */
  uri: string;
  /** MIME type of the file */
  type: string;
};
