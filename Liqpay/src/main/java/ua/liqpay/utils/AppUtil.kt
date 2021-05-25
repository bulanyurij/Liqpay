package ua.liqpay.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

/**
 * Check application is install.
 */
internal fun isInstallApp(context: Context, uri: String): Boolean {
    val pm = context.packageManager
    try {
        pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
        return true
    } catch (e: PackageManager.NameNotFoundException) {
    }
    return false
}

/**
 * Open or download application.
 */
internal fun handleAppLink(context: Context, url: String, app: String) {
    if (isInstallApp(context, app)) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        return
    }
    try {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$app")
            )
        )
    } catch (unused: ActivityNotFoundException) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$app")
            )
        )
    }
}