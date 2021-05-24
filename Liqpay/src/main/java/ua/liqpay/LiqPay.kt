package ua.liqpay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Looper
import org.json.JSONObject
import ua.liqpay.request.ErrorCode
import ua.liqpay.request.HttpRequest
import ua.liqpay.request.LIQPAY_API_URL_REQUEST
import ua.liqpay.utils.base64
import ua.liqpay.utils.getDeviceId
import ua.liqpay.utils.isNetworkAvailable
import ua.liqpay.utils.signature
import ua.liqpay.view.BUNDLE_LIQPAY_DATA
import ua.liqpay.view.LiqpayActivity
import java.io.IOException
import java.net.URLEncoder
import java.util.*
import kotlin.collections.HashMap

const val BROADCAST_RECEIVER_ACTION = "ua.liqpay.action"

class LiqPay(private val context: Context,
             private val callback: LiqpayCallback) {

    /**
     * Start liqpay checkout
     *
     * @param privateKey Liqpay private key
     * @param params Other params
     */
    fun checkout(privateKey: String, params: Map<String, Any?> = hashMapOf()) {
        val base64Data = JSONObject(params).toString().base64()
        val signature = signature(base64Data, privateKey)
        checkout(base64Data, signature)
    }

    fun checkout(
        base64Data: String,
        signature: String
    ) {
        if (!isNetworkAvailable(context)) {
            callback.onError(ErrorCode.FAIL_INTERNET_CONNECTION)
        } else {
            val liqPay = LiqPay(context, callback)
            context.registerReceiver(
                liqPay.eventReceiver,
                IntentFilter(BROADCAST_RECEIVER_ACTION)
            )
            LiqpayActivity.start(context, base64Data, signature)
        }
    }

    fun api(
        context: Context,
        path: String,
        params: HashMap<String, String?> = hashMapOf(),
        privateKey: String
    ) {
        val base64Data = JSONObject(params as Map<*, *>).toString().base64()
        val signature = signature(base64Data, privateKey)
        api(context, path, base64Data, signature)
    }

    private fun api(
        context: Context,
        path: String,
        base64Data: String?,
        signature: String?
    ) {
        if (!isNetworkAvailable(context)) {
            callback.onError(ErrorCode.FAIL_INTERNET_CONNECTION)
        } else if (Looper.myLooper() == Looper.getMainLooper()) {
            callback.onError(ErrorCode.NEED_NON_UI_THREAD)
        } else {
            val postParams = HashMap<String, String?>()
            postParams["data"] = base64Data
            postParams["signature"] = signature
            try {
                val request = HttpRequest()
                val resp = request.post(LIQPAY_API_URL_REQUEST + path, postParams)
                callback.onSuccess(resp)
            } catch (e: IOException) {
                e.printStackTrace()
                callback.onError(ErrorCode.OTHER)
            }
        }
    }

    private val eventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == BROADCAST_RECEIVER_ACTION) {
                val response = intent.getStringExtra("data")
                if (response.isNullOrEmpty()) {
                    callback.onError(ErrorCode.CHECKOUT_CANCELED)
                } else {
                    callback.onSuccess(response)
                }
                context.unregisterReceiver(this)
            }
        }
    }
}