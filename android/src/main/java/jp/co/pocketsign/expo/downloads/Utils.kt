package jp.co.pocketsign.expo.downloads

import java.io.File

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