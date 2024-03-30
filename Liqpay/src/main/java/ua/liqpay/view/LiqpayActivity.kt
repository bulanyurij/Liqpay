package ua.liqpay.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import ua.liqpay.LIQPAY_BROADCAST_RECEIVER_ACTION
import ua.liqpay.LIQPAY_DATA_KEY
import ua.liqpay.LIQPAY_SIGNATURE_KEY
import ua.liqpay.LIQPAY_URL_CHECKOUT
import java.net.URLEncoder

private const val BUNDLE_LIQPAY_DATA = "bundle_liqpay_data"

/**
 * Liapay payment activity.
 */
internal class LiqpayActivity : Activity() {

    private lateinit var loadingDialog: LoaderViewDialog
    private lateinit var eventReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        loadingDialog = LoaderViewDialog(this)
        initCancelPaymentReceiver()
        setContentView(createLiqpayView())
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog.dismiss()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                eventReceiver,
                IntentFilter(LIQPAY_BROADCAST_RECEIVER_ACTION),RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(
                eventReceiver,
                IntentFilter(LIQPAY_BROADCAST_RECEIVER_ACTION)
            )
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(eventReceiver)
    }

    /**
     * Create liqpay view.
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun createLiqpayView(): LiqpayView {
        return LiqpayView(this).apply {
            postUrl(LIQPAY_URL_CHECKOUT, sharedData())
            webChromeClient = WindowChromeClient {
                setContentView(createLiqpayView())
            }
            loadingListener = object : LoadingListener{
                override fun showLoading() {
                    loadingDialog.show()
                }

                override fun hideLoading() {
                    loadingDialog.dismiss()
                }
            }
        }
    }

    /**
     * Get shared data from intent
     */
    private fun sharedData(): ByteArray {
        return intent.getStringExtra(BUNDLE_LIQPAY_DATA)?.toByteArray() ?: byteArrayOf()
    }

    private fun initCancelPaymentReceiver(){
        eventReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == LIQPAY_BROADCAST_RECEIVER_ACTION) {
                    val response = intent.getStringExtra(BUNDLE_DATA)
                    if (response.isNullOrEmpty()) {
                        finish()
                    }
                }
            }
        }
    }

    companion object {

        /**
         * Start liqpay activity.
         *
         * @param context [Context]
         *
         * @param data Liqpay data
         *
         * @param signature Liqpay signature
         */
        @JvmStatic
        internal fun start(context: Context, data: String, signature: String) {
            val intent = Intent(context, LiqpayActivity::class.java).apply {
                putExtra(BUNDLE_LIQPAY_DATA, "${LIQPAY_DATA_KEY}=" + URLEncoder.encode(data) +
                        "&${LIQPAY_SIGNATURE_KEY}=" + signature)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}