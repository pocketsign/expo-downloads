package jp.co.pocketsign.expo.downloads

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import expo.modules.interfaces.permissions.Permissions.askForPermissionsWithPermissionsManager
import expo.modules.interfaces.permissions.Permissions.getPermissionsWithPermissionsManager
import expo.modules.kotlin.Promise
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import java.io.BufferedOutputStream
import java.io.FileOutputStream

val grantedPermissions = mapOf(
    "canAskAgain" to true, "granted" to true, "expires" to "never", "status" to "granted"
)

class DownloadsModule : Module() {

    private lateinit var mContext: Context

    override fun definition() = ModuleDefinition {
        Name("Downloads")

        OnCreate {
            mContext = requireNotNull(appContext.reactContext) { "ReactContext is null" }
        }

        AsyncFunction("saveToDownloads") { fileName: String, mimeType: String, base64Data: String ->
            validateArguments(fileName, mimeType, base64Data)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10(Q) 以降: MediaStore APIを使用
                saveWithMediaStore(fileName, mimeType, base64Data)
            } else {
                // Android 9以下: 直接Downloadディレクトリに保存
                saveLegacy(fileName, base64Data)
            }
        }

        AsyncFunction("requestPermissionsAsync") { promise: Promise ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                promise.resolve(grantedPermissions)
                return@AsyncFunction
            }
            askForPermissionsWithPermissionsManager(
                appContext.permissions, promise, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        AsyncFunction("getPermissionsAsync") { promise: Promise ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                promise.resolve(grantedPermissions)
                return@AsyncFunction
            }
            getPermissionsWithPermissionsManager(
                appContext.permissions, promise, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveWithMediaStore(fileName: String, mimeType: String, base64Data: String): DownloadResponse {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, mimeType)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val resolver = mContext.contentResolver

        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = resolver.insert(collection, contentValues) ?: throw ContentUriCreationException()

        try {
            resolver.openOutputStream(uri)?.use { outputStream ->
                decodeBase64InChunks(base64Data, outputStream)
            } ?: throw OutputStreamCreationException()

            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
            return DownloadResponse(uri = uri.toString())
        } catch (e: OutOfMemoryError) {
            resolver.delete(uri, null, null)
            throw OutOfMemoryException()
        } catch (e: Exception) {
            resolver.delete(uri, null, null)
            throw e
        }
    }

    private fun saveLegacy(fileName: String, base64Data: String): DownloadResponse {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists() && !downloadsDir.mkdirs()) {
            throw DirectoryCreationException()
        }

        val fileToSave = getUniqueFile(downloadsDir, fileName)

        try {
            FileOutputStream(fileToSave).use { fileOutputStream ->
                BufferedOutputStream(fileOutputStream).use { bufferedOutputStream ->
                    decodeBase64InChunks(base64Data, bufferedOutputStream)
                }
            }
            return DownloadResponse(uri = fileToSave.toString())
        } catch (e: OutOfMemoryError) {
            fileToSave.delete() // エラー時、作成したファイルを削除
            throw OutOfMemoryException()
        } catch (e: Exception) {
            fileToSave.delete() // エラー時、作成したファイルを削除
            throw e
        }
    }
}
