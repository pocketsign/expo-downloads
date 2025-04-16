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
import androidx.core.content.FileProvider
import android.net.Uri
import android.content.Intent

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

        AsyncFunction("saveFile") { options: SaveFileOptions ->
            validateArguments(options.name, options.type)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10(Q) 以降: MediaStore APIを使用
                saveWithMediaStore(options.name, options.type, options.data, options.encoding ?: Encoding.utf8)
            } else {
                // Android 9以下: 直接Downloadディレクトリに保存
                saveLegacy(options.name, options.data, options.encoding ?: Encoding.utf8)
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

        AsyncFunction("openFile") { options: OpenFileOptions ->
            val parsedUri = try {
                Uri.parse(options.uri)
            } catch (e: Exception) {
                throw InvalidArgumentException("uri is invalid")
            }
            if (parsedUri.scheme == null || (parsedUri.scheme != "content" && parsedUri.scheme != "file")) {
                throw InvalidArgumentException("uri is invalid")
            }
            openFile(parsedUri, options.type)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveWithMediaStore(name: String, type: String, data: String, encoding: Encoding): DownloadResponse {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, name)
            put(MediaStore.Downloads.MIME_TYPE, type)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val resolver = mContext.contentResolver

        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = resolver.insert(collection, contentValues) ?: throw ContentUriCreationException()

        try {
            resolver.openOutputStream(uri)?.use { outputStream ->
                processDataWithEncoding(data, encoding, outputStream)
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

    private fun saveLegacy(name: String, data: String, encoding: Encoding): DownloadResponse {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists() && !downloadsDir.mkdirs()) {
            throw DirectoryCreationException()
        }

        val fileToSave = getUniqueFile(downloadsDir, name)

        try {
            FileOutputStream(fileToSave).use { fileOutputStream ->
                BufferedOutputStream(fileOutputStream).use { bufferedOutputStream ->
                    processDataWithEncoding(data, encoding, bufferedOutputStream)
                }
            }
            val contentUri = FileProvider.getUriForFile(
                mContext,
                "${mContext.packageName}.fileprovider",
                fileToSave
            )
            return DownloadResponse(uri = contentUri.toString())
        } catch (e: OutOfMemoryError) {
            fileToSave.delete() // エラー時、作成したファイルを削除
            throw OutOfMemoryException()
        } catch (e: Exception) {
            fileToSave.delete() // エラー時、作成したファイルを削除
            throw e
        }
    }

    private fun openFile(uri: Uri, type: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, type)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            mContext.startActivity(intent)
        } catch (e: Exception) {
            throw FileOpenException()
        }
    }
}
