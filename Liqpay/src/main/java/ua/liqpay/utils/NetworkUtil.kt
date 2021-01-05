package ua.liqpay.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

/**
 * Check network state
 *
 * @param context [Context]
 */
fun isNetworkAvailable(context: Context?): Boolean {
    if (context == null) return false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) or
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) or
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    } else {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            return true
        }
    }
    return false
}