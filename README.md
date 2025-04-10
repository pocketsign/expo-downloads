# @pocketsign/expo-downloads

An Expo module for directly downloading files to the native Downloads folder on Android and iOS.

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [API Specification](#api-specification)
  - [Functions](#functions)
  - [Permission-related Functions](#permission-related-functions)
  - [Exceptions](#exceptions)
- [Platform-specific Behavior](#platform-specific-behavior)

## Installation

```bash
npx expo install @pocketsign/expo-downloads
```

## Usage

Below is a sample code which saves a text file named `example.txt`—sourced from Base64 encoded data—to the native Downloads folder.
For Android versions 9 and below, the `WRITE_EXTERNAL_STORAGE` permission is checked.

```javascript
import { saveFile, openFile, getPermissionsAsync, requestPermissionsAsync } from "@pocketsign/expo-downloads";

const options = {
  fileName: "example.txt",
  mimeType: "text/plain",
  base64Data: "SGVsbG8sIFdvcmxkIQ==", // "Hello, World!" encoded in Base64
};

// For Android 9 and below, request WRITE_EXTERNAL_STORAGE permission.
const permissions = await getPermissionsAsync();
if (!permissions.granted) {
  const newPermissions = await requestPermissionsAsync();
  if (!newPermissions.granted) {
    console.error("Could not obtain permissions");
    return;
  }
}

try {
  const result = await saveFile(options);
  if (result.cancelled) {
    console.log("Download was cancelled");
  } else {
    console.log(`File saved successfully: ${result.uri}`);
    // Example of opening the file immediately after saving
    await openFile({ uri: result.uri, mimeType: options.mimeType });
  }
} catch (error) {
  console.error("Error saving or opening file:", error);
}
```

## API Specification

### Functions

#### `saveFile(options: SaveFileOptions): Promise<SaveFileResponse>`

- **Description**:
  Saves the Base64 encoded file data using the provided options.

- **Parameters** (`SaveFileOptions` object):

  - `fileName`: The name under which the file will be saved.
  - `mimeType`: The MIME type of the file.
  - `base64Data`: The file data encoded in Base64.

- **Returns** (`SaveFileResponse` object):
  - `uri`: The URI of the saved file (upon success).
  - `cancelled`: `true` if the operation was cancelled by the user (iOS only).

#### `openFile(options: OpenFileOptions): Promise<void>`

- **Description**:
  Opens the downloaded file using the native file viewer on the device.

- **Parameters** (`OpenFileOptions` object):
  - `uri`: The URI of the file to be opened. (Use the `uri` field of the `SaveFileResponse` object returned by `saveFile`.)
  - `mimeType`: The MIME type of the file.

### Permission-related Functions

- **`requestPermissionsAsync`**:  
  Requests the `WRITE_EXTERNAL_STORAGE` permission for Android versions 9 and below. (This permission is not required on Android 10+ and iOS.)

- **`getPermissionsAsync`**:  
  Retrieves the current status of the storage write permission.

### Exceptions

#### Common Exceptions

- **ERR_INVALID_ARGUMENT** (iOS / Android)

  - Thrown if the `fileName` is empty, if the `mimeType` format is invalid, or if the `base64Data` is improperly formatted.

- **ERR_FILE_OPEN**
  - Thrown when an error occurs while attempting to open a file. This exception is raised if the specified file does not exist or if a compatible application cannot be found to open it.

#### iOS Specific Exceptions

- **ERR_DOWNLOAD_IN_PROGRESS**

  - Thrown if a download is already in progress, preventing simultaneous operations.

- **ERR_MISSING_VIEW_CONTROLLER**
  - Thrown if the current view controller cannot be obtained, which prevents the file-saving dialog from being displayed.

#### Android Specific Exceptions

- **ERR_CONTENT_URI_CREATION** (Android 10 and above)

  - Thrown if creating the content URI using the MediaStore API fails.

- **ERR_OUTPUT_STREAM_CREATION** (Android 10 and above)

  - Thrown if an OutputStream for writing to the file cannot be created.

- **ERR_DIRECTORY_CREATION** (Android 9 and below)

  - Thrown if the Downloads folder does not exist or if attempting to create it fails.

- **ERR_OUT_OF_MEMORY**
  - Thrown when the file is too large and an OutOfMemoryError occurs during saving due to insufficient memory.

## Platform-specific Behavior

### iOS

- **Implementation**:  
  Uses `UIDocumentPickerViewController` to allow the user to select a save location.

### Android

#### Android 10 and above (Q+)

- **Implementation**:  
  Uses the MediaStore API to save files to the system-managed Downloads folder.
- **Permissions**:  
  No special storage permissions are required on API level 29 and above.

#### Android 9 and below

- **Implementation**:  
  Writes directly to the device's Downloads folder using legacy methods.
- **Permissions**:  
  The `WRITE_EXTERNAL_STORAGE` permission is required. Use `requestPermissionsAsync` and `getPermissionsAsync` to handle permissions.

### Web

- **Implementation**:  
  Utilizes HTML's `Blob` and `<a>` tag to download the file.
