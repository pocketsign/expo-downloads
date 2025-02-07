package android.util

import java.util.Base64

object Base64 {
    const val DEFAULT = 0x0
    const val NO_WRAP = 0x2

    @JvmStatic
    fun encodeToString(input: ByteArray?, flags: Int): String {
        return Base64.getEncoder().encodeToString(input)
    }

    @JvmStatic
    fun decode(str: String?, flags: Int): ByteArray {
        return Base64.getDecoder().decode(str)
    }
}