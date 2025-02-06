package jp.co.pocketsign.expo.downloads

import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import java.io.File

class UtilsTest {

    // 一時ディレクトリ生成用のヘルパー関数（クラス内のプライベートメソッド）
    private fun withTempDir(block: (File) -> Unit) {
        // createTempDirectory() は Path を返すため、toFile() で File に変換
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
            // ファイルが存在しない場合は、元のファイル名が返る
            assertEquals(File(dir, fileName).absolutePath, uniqueFile.absolutePath)
        }
    }

    @Test
    fun testUniqueFile_singleConflict() {
        withTempDir { dir ->
            val fileName = "test.txt"
            // 既存のファイルを作成
            File(dir, fileName).createNewFile()

            val uniqueFile = getUniqueFile(dir, fileName)
            // 期待されるファイル名は "test (1).txt"
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
            // 期待されるファイル名は "test (3).txt"
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
            // 期待されるファイル名は "testfile (1)"
            val expectedFile = File(dir, "testfile (1)")
            assertEquals(expectedFile.absolutePath, uniqueFile.absolutePath)
        }
    }
} 