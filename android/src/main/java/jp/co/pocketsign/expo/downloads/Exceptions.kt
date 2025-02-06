package jp.co.pocketsign.expo.downloads

import expo.modules.kotlin.exception.CodedException

internal class ContentUriCreationException : CodedException("Failed to create content URI")
internal class DirectoryCreationException : CodedException("Failed to create downloads directory")
internal class OutputStreamCreationException : CodedException("Failed to create downloads outputStream")
internal class InvalidArgumentException(message: String) : CodedException("Invalid arguments: $message")
