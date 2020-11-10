package ua.liqpay.utils

import java.net.URLEncoder

fun String.encode(): String {
    return URLEncoder.encode(this, "UTF-8")
}

fun Any.encode(): String {
    return toString().encode()
}