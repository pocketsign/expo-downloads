import { createPermissionHook, PermissionResponse, PermissionStatus, UnavailabilityError } from "expo-modules-core";

import Downloads from "./Downloads";
import { OpenFileOptions, SaveFileOptions, SaveFileResponse } from "./Downloads.types";

export { SaveFileResponse, OpenFileOptions, SaveFileOptions };

if (!Downloads) {
  console.warn("No native Downloads module found, are you sure the expo-downloads's module is linked properly?");
}

/**
 * Saves a file to the system.
 * Android: Saves to the global download folder.
 * iOS: Saves to a folder selected by the user.
 *
 * @param options Save options
 * @returns Returns a Promise containing the URL of the saved file.
 */
export const saveFile = (options: SaveFileOptions) => {
  return Downloads.saveFile(options);
};

/**
 * Opens a saved file.
 *
 * @param options Options to open a file
 */
export const openFile = async (options: OpenFileOptions) => {
  if (!Downloads.openFile) {
    throw new UnavailabilityError("@pocketsign/expo-downloads", "openFile");
  }
  return await Downloads.openFile(options);
};

const grantedPermissions: PermissionResponse = {
  granted: true,
  expires: "never",
  canAskAgain: true,
  status: PermissionStatus.GRANTED,
};

/**
 * Requests permission to access external storage.
 * For Android 9 and below, explicitly requests WRITE_EXTERNAL_STORAGE permission.
 * For other devices, no special permissions are required.
 * @returns Returns a Promise containing the permission response.
 */
export const requestPermissionsAsync = async (): Promise<PermissionResponse> => {
  if (Downloads.requestPermissionsAsync) {
    return await Downloads.requestPermissionsAsync();
  }
  return grantedPermissions;
};

/**
 * Gets the current state of permission to access external storage.
 * For Android 9 and below, verifies WRITE_EXTERNAL_STORAGE permission.
 * For other devices, no special permissions are required.
 * @returns Returns a Promise containing the permission response.
 */
export const getPermissionsAsync = async (): Promise<PermissionResponse> => {
  if (Downloads.getPermissionsAsync) {
    return await Downloads.getPermissionsAsync();
  }
  return grantedPermissions;
};

/**
 * Hook to manage permission to access external storage.
 * For Android 9 and below, processing of WRITE_EXTERNAL_STORAGE permission is required.
 * For other devices, no special permissions are required.
 */
export const usePermissions = createPermissionHook({
  getMethod: getPermissionsAsync,
  requestMethod: requestPermissionsAsync,
});
