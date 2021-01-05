package ua.liqpay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Looper
import org.json.JSONObject
import ua.liqpay.request.ErrorCode
import ua.liqpay.utils.base64
import ua.liqpay.utils.isNetworkAvailable
import ua.liqpay.utils.signature
import ua.liqpay.view.CheckoutActivity
import java.io.IOException
import java.net.URLEncoder
import java.util.*

const val BROADCAST_RECEIVER_ACTION = "ua.liqpay.action"

class LiqPay(private val context: Context, private val checkoutCallBack: LiqPayCallBack) {

    private val eventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == BROADCAST_RECEIVER_ACTION) {
                val response = intent.getStringExtra("data")
                if (response.isNullOrEmpty()) {
                    checkoutCallBack.onError(ErrorCode.CHECKOUT_CANCELED)
                } else {
                    checkoutCallBack.onSuccess(response)
                }
                context.unregisterReceiver(this)
            }
        }
    }

    private fun startCheckoutActivity(data: String, signature: String) {
        val intent = Intent(context, CheckoutActivity::class.java)
        intent.putExtra(CheckoutActivity.INTENT_POST_DATA, "data=" + URLEncoder.encode(data) +
                "&signature=" + signature + "&hash_device=" + getHashDevice(context) + "&channel=android")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    companion object {

        @JvmStatic
        fun api(context: Context, path: String, params: HashMap<String, String?> = hashMapOf(), privateKey: String, callBack: LiqPayCallBack) {
            val base64Data = JSONObject(params as Map<*, *>).toString().base64()
            val signature = signature(base64Data, privateKey)
            api(context, path, base64Data, signature, callBack)
        }

        private fun api(context: Context, path: String, base64Data: String?, signature: String?, callBack: LiqPayCallBack) {
            if (!isNetworkAvailable(context)) {
                callBack.onError(ErrorCode.FAIL_INTERNET_CONNECTION)
            } else if (Looper.myLooper() == Looper.getMainLooper()) {
                callBack.onError(ErrorCode.NEED_NON_UI_THREAD)
            } else {
                val postParams = HashMap<String, String?>()
                postParams["data"] = base64Data
                postParams["signature"] = signature
                try {
                    val resp = post(LIQPAY_API_URL_REQUEST + path, postParams)
                    callBack.onSuccess(resp)
                } catch (e: IOException) {
                    e.printStackTrace()
                    callBack.onError(ErrorCode.IO)
                }
            }
        }

        @JvmStatic
        fun checkout(context: Context, params: HashMap<String, String?>?, privateKey: String, callBack: LiqPayCallBack) {
            val base64Data = JSONObject(params as Map<*, *>).toString().base64()
            val signature = signature(base64Data, privateKey)
            checkout(context, base64Data, signature, callBack)
        }

        @JvmStatic
        fun checkout(context: Context, base64Data: String, signature: String, callBack: LiqPayCallBack) {
            if (!isNetworkAvailable(context)) {
                callBack.onError(ErrorCode.FAIL_INTERNET_CONNECTION)
            } else {
                val liqPay = LiqPay(context, callBack)
                context.registerReceiver(liqPay.eventReceiver, IntentFilter(BROADCAST_RECEIVER_ACTION))
                liqPay.startCheckoutActivity(base64Data, signature)
            }
        }
    }
}