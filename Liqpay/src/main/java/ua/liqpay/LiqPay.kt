package ua.liqpay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import org.json.JSONObject
import ua.liqpay.utils.base64
import ua.liqpay.utils.signature
import ua.liqpay.view.BUNDLE_DATA
import ua.liqpay.view.LiqpayActivity

internal const val LIQPAY_BROADCAST_RECEIVER_ACTION = "ua.liqpay.action"
internal const val LIQPAY_DATA_KEY = "data"
internal const val LIQPAY_SIGNATURE_KEY = "signature"

class LiqPay(
    private val context: Context,
    private val callback: LiqpayCallback
) {

    /**
     * Start checkout page.
     *
     * @param privateKey Secret access key for API
     * @param publicKey Unique ID of your company in LiqPay system
     * @param action Transaction type. Possible values: pay - payment,
     *                  hold - amount of hold on sender's account, subscribe - regular payment,
     *                  paydonate - donation, auth - card preauth
     * @param amount Payment amount. For example: 5, 7.34
     * @param currency Payment currency. Possible values: USD, EUR, RUB, UAH, BYN, KZT
     * @param description Payment description
     * @param orderId Unique purchase ID in your shop. Maximum length is 255 symbols.
     * @param language Customer's language ru, uk, en
     *
     * More info: https://www.liqpay.ua/documentation/en/api/aquiring/checkout/doc
     *
     */
    fun checkout(
        privateKey: String,
        publicKey: String,
        action: String = "pay",
        amount: Double,
        currency: String = "UAH",
        description: String,
        orderId: String,
        language: String = "uk"
    ) {
        val params = hashMapOf(
            "action" to action,
            "amount" to amount,
            "currency" to currency,
            "description" to description,
            "order_id" to orderId,
            "language" to language
        )
        checkout(privateKey, publicKey, params)
    }

    /**
     * Start checkout page.
     *
     * @param privateKey Secret access key for API
     * @param publicKey Unique ID of your company in LiqPay system
     * @param params Custom API params. Info: https://www.liqpay.ua/documentation/en/api/aquiring/checkout/doc
     *
     */
    fun checkout(privateKey: String, publicKey: String, params: Map<String, Any?> = hashMapOf()) {
        val tempMap = params.toMutableMap().apply {
            put("public_key", publicKey)
            if (!containsKey("version")) {
                put("version", LIQPAY_API_VERSION)
            }
        }
        val base64Data = JSONObject(tempMap).toString().base64()
        val signature = signature(base64Data, privateKey)
        checkout(base64Data, signature)
    }

    /**
     * Start checkout page.
     *
     * @param base64Data Checkout data encoded by the base64
     * @param signature The unique signature of each request base64_encode(sha1(private_key + data + private_key))
     *
     */
    fun checkout(
        base64Data: String,
        signature: String
    ) {
        val liqPay = LiqPay(context, callback)
        context.registerReceiver(
            liqPay.eventReceiver,
            IntentFilter(LIQPAY_BROADCAST_RECEIVER_ACTION)
        )
        LiqpayActivity.start(context, base64Data, signature)
    }

    /**
     * Result event receiver.
     */
    private val eventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == LIQPAY_BROADCAST_RECEIVER_ACTION) {
                val response = intent.getStringExtra(BUNDLE_DATA)
                if (response.isNullOrEmpty()) {
                    callback.onCancel()
                } else {
                    callback.onSuccess(response)
                }
                context.unregisterReceiver(this)
            }
        }
    }
}