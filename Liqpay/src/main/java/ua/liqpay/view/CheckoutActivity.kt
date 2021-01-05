package ua.liqpay.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Base64
import android.webkit.WebView
import android.webkit.WebViewClient
import org.json.JSONObject
import ua.liqpay.utils.URLEncodeUtil
import java.net.URI

class CheckoutActivity : Activity() {

    private lateinit var webView: WebView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        webView = WebView(this)
        setContentView(webView)
        initWebView()
    }

    override fun onDestroy() {
        webView.destroy()
        sendEvent(null)
        super.onDestroy()
    }

    /**
     * Init web view and load web page
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.show()

        webView.apply {
            postUrl(LIQPAY_API_URL_CHECKOUT, sharedData())
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
                        finish()
                    }
                }
            }
        }
    }

    /**
     * Get shared data from intent
     */
    private fun sharedData(): ByteArray {
        return intent.getStringExtra(INTENT_POST_DATA)?.encodeToByteArray() ?: byteArrayOf()
    }



    /**
     * Parse url data
     *
     * @param url Web url
     *
     * @return [JSONObject]
     */
    private fun parseUrl(url: String?): JSONObject {
        val data = JSONObject()
        URLEncodeUtil.parse(URI(url), "UTF-8").forEach {
            try {
                if ("data" == it.first) {
                    val item = JSONObject(String(Base64.decode(it.second, 2)))
                    LiqPayUtil.addAll(data, item)
                } else {
                    data.put(it.first, it.second)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return data
    }


    companion object {
        const val INTENT_POST_DATA = "post_data"
    }
}