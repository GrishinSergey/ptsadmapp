package com.sagrishin.ptsadmapp.core.crypt

import java.nio.charset.Charset
import java.util.*

object AesCryptUseCase {

    const val SECRET_KEY = "zAP5MBA4B4Ijz0MZaS48"

    fun encrypt(data: String): String {
        val dataBytes = data.toByteArray()
        val bytes = ByteArray(dataBytes.size)
        Base64.getEncoder().encode(dataBytes, bytes)
        return bytes.toString(Charset.defaultCharset())
    }

    fun decrypt(encryptedValue: String): String {
        return String(Base64.getDecoder().decode(encryptedValue))
    }

}
