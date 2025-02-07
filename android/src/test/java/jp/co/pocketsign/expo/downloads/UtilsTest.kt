package jp.co.pocketsign.expo.downloads

import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import java.io.File
import java.io.ByteArrayOutputStream
import android.util.Base64

class UtilsTest {

    private fun withTempDir(block: (File) -> Unit) {
        val tempDir = createTempDirectory("temp").toFile()
        try {
            block(tempDir)
        } finally {
            tempDir.deleteRecursively()
        }
    }

    @Test
    fun testUniqueFile_noConflict() {
        withTempDir { dir ->
            val fileName = "test.txt"
            val uniqueFile = getUniqueFile(dir, fileName)
            assertEquals(File(dir, fileName).absolutePath, uniqueFile.absolutePath)
        }
    }

    @Test
    fun testUniqueFile_singleConflict() {
        withTempDir { dir ->
            val fileName = "test.txt"
            File(dir, fileName).createNewFile()

            val uniqueFile = getUniqueFile(dir, fileName)
            val expectedFile = File(dir, "test (1).txt")
            assertEquals(expectedFile.absolutePath, uniqueFile.absolutePath)
        }
    }

    @Test
    fun testUniqueFile_multipleConflicts() {
        withTempDir { dir ->
            val fileName = "test.txt"
            File(dir, fileName).createNewFile()
            File(dir, "test (1).txt").createNewFile()
            File(dir, "test (2).txt").createNewFile()

            val uniqueFile = getUniqueFile(dir, fileName)
            val expectedFile = File(dir, "test (3).txt")
            assertEquals(expectedFile.absolutePath, uniqueFile.absolutePath)
        }
    }

    @Test
    fun testUniqueFile_noExtension() {
        withTempDir { dir ->
            val fileName = "testfile"
            File(dir, fileName).createNewFile()

            val uniqueFile = getUniqueFile(dir, fileName)
            val expectedFile = File(dir, "testfile (1)")
            assertEquals(expectedFile.absolutePath, uniqueFile.absolutePath)
        }
    }

    @Test
    fun testDecodeBase64InChunks_basic() {
        val originalText = "Hello, World!"
        val base64String = Base64.encodeToString(originalText.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        val expectedBytes = originalText.toByteArray(Charsets.UTF_8)
        val outputStream = ByteArrayOutputStream()
        decodeBase64InChunks(base64String, outputStream)
        val decodedBytes = outputStream.toByteArray()
        assertTrue(decodedBytes.contentEquals(expectedBytes))
    }

    @Test
    fun testDecodeBase64InChunks_large() {
        val originalBytes = ByteArray(50000) { (it % 256).toByte() }
        val base64String = Base64.encodeToString(originalBytes, Base64.NO_WRAP)
        val outputStream = ByteArrayOutputStream()
        decodeBase64InChunks(base64String, outputStream)
        val decodedBytes = outputStream.toByteArray()
        assertTrue(decodedBytes.contentEquals(originalBytes))
    }

    @Test
    fun testValidateArguments_valid() {
        validateArguments("test.txt", "text/plain", "SGVsbG8sIFdvcmxkIQ==")
    }

    @Test
    fun testValidateArguments_blankFileName() {
        assertFailsWith<InvalidArgumentException> {
            validateArguments("   ", "text/plain", "SGVsbG8sIFdvcmxkIQ==")
        }
    }

    @Test
    fun testValidateArguments_invalidMimeType() {
        assertFailsWith<InvalidArgumentException> {
            validateArguments("test.txt", "invalidMime", "SGVsbG8sIFdvcmxkIQ==")
        }
    }

    @Test
    fun testValidateArguments_blankBase64Data() {
        assertFailsWith<InvalidArgumentException> {
            validateArguments("test.txt", "text/plain", "    ")
        }
    }

    @Test
    fun testValidateArguments_invalidCharacterInBase64Data() {
        assertFailsWith<InvalidArgumentException> {
            validateArguments("test.txt", "text/plain", "SGVsbG8, IFdvcmxkIQ==")
        }
    }

    @Test
    fun testValidateArguments_invalidBase64Length() {
        assertFailsWith<InvalidArgumentException> {
            validateArguments("test.txt", "text/plain", "ABC")
        }
    }
} 