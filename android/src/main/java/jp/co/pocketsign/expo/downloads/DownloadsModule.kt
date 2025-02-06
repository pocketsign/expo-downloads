package jp.co.pocketsign.expo.downloads

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Base64
import android.util.Base64InputStream
import androidx.annotation.RequiresApi
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import java.io.ByteArrayInputStream
import java.io.OutputStream

class DownloadsModule : Module() {

    private lateinit var mContext: Context

    override fun definition() = ModuleDefinition {
        Name("Downloads")

        OnCreate {
            mContext = requireNotNull(appContext.reactContext) { "ReactContext is null" }
        }

        AsyncFunction("saveToDownloads") { fileName: String, mimeType: String, base64Data: String ->
            validateArgs(fileName, mimeType, base64Data)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10(Q) 以降: MediaStore APIを使用
                saveWithMediaStore(fileName, mimeType, base64Data)
            } else {
                // Android 9以下: Storage Access Frameworkを使用
                saveLegacy(fileName, mimeType, base64Data)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveWithMediaStore(fileName: String, mimeType: String, base64Data: String): String {
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
                decodeBase64UsingStream(base64Data, outputStream)
            } ?: throw OutputStreamCreationException()

            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
            return uri.toString()
        } catch (e: Exception) {
            resolver.delete(uri, null, null)
            throw e
        }
    }

    private fun saveLegacy(fileName: String, mimeType: String, base64Data: String): String {
        // Storage Access Frameworkを使用してDownloadsフォルダにファイルを作成
        val downloadsTreeUri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3ADownload")
        val fileUri =
            DocumentsContract.createDocument(mContext.contentResolver, downloadsTreeUri, mimeType, fileName)
                ?: throw DirectoryCreationException()

        try {
            mContext.contentResolver.openOutputStream(fileUri)?.use { outputStream ->
                decodeBase64UsingStream(base64Data, outputStream)
            } ?: throw OutputStreamCreationException()
            return fileUri.toString()
        } catch (e: Exception) {
            mContext.contentResolver.delete(fileUri, null, null)
            throw e
        }
    }

    private fun decodeBase64UsingStream(base64Data: String, outputStream: OutputStream) {
        val byteArrayInputStream = ByteArrayInputStream(base64Data.toByteArray(Charsets.UTF_8))
        Base64InputStream(byteArrayInputStream, Base64.DEFAULT).use { base64InputStream ->
            val buffer = ByteArray(8192)
            var bytesRead = base64InputStream.read(buffer)
            while (bytesRead != -1) {
                outputStream.write(buffer, 0, bytesRead)
                bytesRead = base64InputStream.read(buffer)
            }
        }
        outputStream.flush()
    }

    private fun validateArgs(fileName: String, mimeType: String, base64Data: String) {
        if (fileName.trim().isEmpty()) {
            throw InvalidArgumentException("fileName cannot be blank")
        }
        
        val mimePattern = Regex("^[\\w.+-]+/[\\w.+-]+$")
        if (!mimePattern.matches(mimeType.trim())) {
            throw InvalidArgumentException("mimeType format is invalid")
        }
        
        if (base64Data.isBlank()) {
            throw InvalidArgumentException("base64Data cannot be blank")
        }
        
        var nonWhitespaceCharCount = 0
        for (ch in base64Data) {
            if (!ch.isWhitespace()) {
                if (!(ch in 'A'..'Z' || ch in 'a'..'z' || ch in '0'..'9' || ch == '+' || ch == '/' || ch == '=')) {
                    throw InvalidArgumentException("base64Data contains invalid character: $ch")
                }
                nonWhitespaceCharCount++
            }
        }
        if (nonWhitespaceCharCount % 4 != 0) {
            throw InvalidArgumentException("base64Data length (ignoring whitespace) must be a multiple of 4")
        }
    }
}
