package ua.liqpay.utils

import android.content.Context
import android.content.pm.PackageManager

/**
 * Check all permission is granted
 *
 * @param context [Context]
 * @param permissions Array of permission
 */
fun grantedPermissions(context: Context, vararg permissions: String): Boolean {
    permissions.forEach {
        if (context.checkCallingOrSelfPermission(it) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}