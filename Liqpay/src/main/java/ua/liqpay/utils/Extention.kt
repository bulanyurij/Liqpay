package ua.liqpay.utils

import android.util.Base64
import java.net.URLEncoder
import java.security.MessageDigest

fun String.encodeUTF8(): String {
    return URLEncoder.encode(this, "UTF-8")
}

fun Any.encodeUTF8(): String {
    return toString().encodeUTF8()
}

fun String.base64(): String {
    return toByteArray().base64()
}

fun ByteArray.base64(): String {
    return Base64.encodeToString(this, Base64.NO_WRAP)
}

fun String.sha1(): ByteArray {
    return try {
        MessageDigest.getInstance("SHA-1").run {
            reset()
            update(toByteArray())
            digest()
        }
    } catch (e: Exception) {
        throw RuntimeException("Can't calc SHA-1 hash", e)
    }
}