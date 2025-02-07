package jp.co.pocketsign.expo.downloads

import android.util.Base64
import java.io.File
import java.io.OutputStream

fun getUniqueFile(directory: File, fileName: String): File {
    var fileToSave = File(directory, fileName)
    if (fileToSave.exists()) {
        val dotIndex = fileName.lastIndexOf('.')
        val baseName = if (dotIndex != -1) fileName.substring(0, dotIndex) else fileName
        val extension = if (dotIndex != -1) fileName.substring(dotIndex) else ""
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

fun validateArguments(fileName: String, mimeType: String, base64Data: String) {
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
