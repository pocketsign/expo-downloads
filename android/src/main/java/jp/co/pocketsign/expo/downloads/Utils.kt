package jp.co.pocketsign.expo.downloads

import android.util.Base64
import java.io.File
import java.io.OutputStream

fun getUniqueFile(directory: File, name: String): File {
    var fileToSave = File(directory, name)
    if (fileToSave.exists()) {
        val dotIndex = name.lastIndexOf('.')
        val baseName = if (dotIndex != -1) name.substring(0, dotIndex) else name
        val extension = if (dotIndex != -1) name.substring(dotIndex) else ""
        var counter = 1
        var newName = "$baseName ($counter)$extension"
        var newFile = File(directory, newName)
        while (newFile.exists()) {
            counter++
            newName = "$baseName ($counter)$extension"
            newFile = File(directory, newName)
        }
        fileToSave = newFile
    }
    return fileToSave
}

fun decodeBase64InChunks(base64Data: String, outputStream: OutputStream) {
    val chunkSize = 4 * 8192
    var start = 0
    val totalLength = base64Data.length
    while (start < totalLength) {
        val end = minOf(start + chunkSize, totalLength)
        val chunk = base64Data.substring(start, end)
        val decodedBytes = Base64.decode(chunk, Base64.DEFAULT)
        outputStream.write(decodedBytes)
        start = end
    }
    outputStream.flush()
}

fun processDataWithEncoding(data: String, encoding: Encoding, outputStream: OutputStream) {
    when (encoding) {
        Encoding.base64 -> decodeBase64InChunks(data, outputStream)
        Encoding.utf8 -> {
            outputStream.write(data.toByteArray(Charsets.UTF_8))
            outputStream.flush()
        }
    }
}

fun validateArguments(name: String, type: String) {
    if (name.trim().isEmpty()) {
        throw InvalidArgumentException("name cannot be blank")
    }

    val mimePattern = Regex("^[\\w.+-]+/[\\w.+-]+$")
    if (!mimePattern.matches(type.trim())) {
        throw InvalidArgumentException("type format is invalid")
    }
}
