package ua.liqpay.utils

import android.util.Log

private const val LOG_TAG = "Liqpay"
private const val IS_ENABLE = true

/**
 * Log error message
 *
 * @param message Error message
 */
fun logE(message: Any?) {
    if (IS_ENABLE)
        Log.e(LOG_TAG, message.toString())
}

/**
 * Log info message
 *
 * @param message Info message
 */
fun logI(message: Any?) {
    if (IS_ENABLE)
        Log.i(LOG_TAG, message.toString())
}

fun Throwable?.logE(){
    this?.localizedMessage?.let {
        logE(it)
    }
}

fun Exception?.logE(){
    this?.localizedMessage?.let {
        logE(it)
    }
}