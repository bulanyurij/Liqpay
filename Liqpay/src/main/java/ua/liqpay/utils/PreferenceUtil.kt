package ua.liqpay.utils

import android.content.Context
import java.util.*

const val PREF_APP_NAME = "pref_liqpay"
const val PREF_DEVICE_ID = "pref_device_id"

/**
 * Generate and save device ID
 *
 * @param context [Context]
 */
fun getDeviceId(context: Context): String {
    val preferences = context.getSharedPreferences(PREF_APP_NAME, Context.MODE_PRIVATE)
    var hashDevice = preferences.getString(PREF_DEVICE_ID, null)
    if (hashDevice == null) {
        val deviceUuid = UUID.randomUUID().toString()
        hashDevice = deviceUuid
        preferences.edit().putString(PREF_DEVICE_ID, hashDevice).apply()
    }
    return hashDevice
}
