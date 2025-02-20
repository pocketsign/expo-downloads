import {
  CodedError,
  createPermissionHook,
  PermissionResponse,
  PermissionStatus,
  UnavailabilityError,
} from "expo-modules-core";

import Downloads from "./Downloads";

export { DownloadResponse } from "./Downloads.types";

if (!Downloads) {
  console.warn("No native Downloads module found, are you sure the expo-downloads's module is linked properly?");
}

/**
 * Saves a file to the Downloads folder.
 * @param fileName The name of the file to save.
 * @param mimeType The MIME type of the file.
 * @param base64Data Base64 encoded file data.
 * @returns A Promise that resolves with the URL of the saved file.
 */
export const saveToDownloads = (fileName: string, mimeType: string, base64Data: string) =>
  Downloads.saveToDownloads(fileName, mimeType, base64Data);

/**
 * Opens a file from the Downloads folder.
 * @param uri The URI of the file to open.
 * @param mimeType The MIME type of the file.
 */
export const openDownloadFile = async (uri: string, mimeType: string) => {
  if (!Downloads.openDownloadFile) {
    throw new UnavailabilityError("@pocketsign/expo-downloads", "openDownloadFile");
  }
  return await Downloads.openDownloadFile(uri, mimeType);
};

const grantedPermissions: PermissionResponse = {
  granted: true,
  expires: "never",
  canAskAgain: true,
  status: PermissionStatus.GRANTED,
};

/**
 * Requests permissions for accessing external storage.
 * On Android 9 and below, this function explicitly requests WRITE_EXTERNAL_STORAGE permission.
 * For devices other than Android 9 and below, no special permission is required.
 * @returns A Promise that resolves with the permission response.
 */
export const requestPermissionsAsync = async (): Promise<PermissionResponse> => {
  if (Downloads.requestPermissionsAsync) {
    return await Downloads.requestPermissionsAsync();
  }
  return grantedPermissions;
};

/**
 * Gets the current permissions for accessing external storage.
 * On Android 9 and below, this function verifies the WRITE_EXTERNAL_STORAGE permission.
 * For devices other than Android 9 and below, no special permission is required.
 * @returns A Promise that resolves with the permission response.
 */
export const getPermissionsAsync = async (): Promise<PermissionResponse> => {
  if (Downloads.getPermissionsAsync) {
    return await Downloads.getPermissionsAsync();
  }
  return grantedPermissions;
};

/**
 * A hook to manage permissions for accessing external storage.
 * On Android 9 and below, WRITE_EXTERNAL_STORAGE permission handling is required.
 * For devices other than Android 9 and below, no special permission is required.
 */
export const usePermissions = createPermissionHook({
  getMethod: getPermissionsAsync,
  requestMethod: requestPermissionsAsync,
});
