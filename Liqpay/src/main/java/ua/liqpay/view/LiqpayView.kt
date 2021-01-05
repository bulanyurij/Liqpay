package ua.liqpay.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.webkit.WebView
import android.webkit.WebViewClient
import ua.liqpay.BROADCAST_RECEIVER_ACTION

@SuppressLint("SetJavaScriptEnabled")
class LiqpayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    init {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                progressDialog.cancel()
                super.onPageFinished(view, url)
            }

            override fun onLoadResource(view: WebView, url: String) {
                super.onLoadResource(view, url)
                val startUrl = "/api/mob/webview"
                if (url.contains(startUrl)) {
                    val data = parseUrl(url)
                    sendEvent(data.toString())
                }
            }
        }
    }

    fun load(){

    }

    /**
     * Send broadcast event
     *
     * @param data Shared data
     */
    private fun sendEvent(data: String?) {
        Intent(BROADCAST_RECEIVER_ACTION).apply {
            setPackage(context.packageName)
            data?.let { putExtra("data", it) }
            context.sendBroadcast(this)
        }
    }
}