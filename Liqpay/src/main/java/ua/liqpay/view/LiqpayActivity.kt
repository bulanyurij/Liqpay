package ua.liqpay.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import ua.liqpay.LIQPAY_URL_CHECKOUT
import java.net.URLEncoder

const val BUNDLE_LIQPAY_DATA = "bundle_liqpay_data"

internal class LiqpayActivity : Activity() {

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        progressDialog = ProgressDialog(this).apply {
            setMessage("Loading...")
            show()
        }
        progressDialog.dismiss()
        setContentView(createLiqpayView())
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog.dismiss()
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
        }
    }

    /**
     * Get shared data from intent
     */
    private fun sharedData(): ByteArray {
        return intent.getStringExtra(BUNDLE_LIQPAY_DATA)?.toByteArray() ?: byteArrayOf()
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
                putExtra(BUNDLE_LIQPAY_DATA, "data=" + URLEncoder.encode(data) + "&signature=" + signature)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}