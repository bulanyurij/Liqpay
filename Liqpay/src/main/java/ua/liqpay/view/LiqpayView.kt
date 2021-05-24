package ua.liqpay.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.AttributeSet
import android.util.Base64
import android.util.Base64.NO_WRAP
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import org.json.JSONObject
import ua.liqpay.BROADCAST_RECEIVER_ACTION
import ua.liqpay.PRIVAT24_APP_PACKAGE
import ua.liqpay.PRIVAT24_APP_URI_SCHEME
import ua.liqpay.utils.URLEncodeUtil
import ua.liqpay.utils.handleAppLink
import ua.liqpay.utils.merge
import java.net.URI
import java.net.URL

private const val SUCCESS_URL_QUERY = "status=success"
private const val CANCEL_URL_PATH = "checkout/cancel"
private const val CALLBACK_PATH = "api/mob/callback"
private const val DATA_KEY = "data"

@SuppressLint("SetJavaScriptEnabled")
class LiqpayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    init {
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(this, true)
        settings.javaScriptEnabled = true
        settings.loadWithOverviewMode = true
        settings.allowFileAccess = true
        settings.allowContentAccess = true
        settings.domStorageEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.loadsImagesAutomatically = true
        settings.builtInZoomControls = false
        settings.setSupportMultipleWindows(true)
        webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()
                if (url.contains(PRIVAT24_APP_URI_SCHEME)) {
                    handleAppLink(context, url, PRIVAT24_APP_PACKAGE)
                } else {
                    loadUrl(url)
                }
                return true
            }


            override fun onPageFinished(view: WebView, url: String) {

                super.onPageFinished(view, url)
            }

            override fun onLoadResource(view: WebView, link: String) {
                super.onLoadResource(view, link)
                val url = URL(link)
                if ((url.path.contains(CALLBACK_PATH) &&
                            url.query.contains(SUCCESS_URL_QUERY)) ||
                    link.contains(CANCEL_URL_PATH)
                ) {
                    val data = parseUrl(link)
                    sendEvent(data.toString())
                }
            }
        }
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
                if (DATA_KEY == it.first) {
                    val item = JSONObject(String(Base64.decode(it.second, NO_WRAP)))
                    merge(data, item)
                } else {
                    data.put(it.first, it.second)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return data
    }

    /**
     * Send broadcast event
     *
     * @param data Shared data
     */
    private fun sendEvent(data: String?) {
        Intent(BROADCAST_RECEIVER_ACTION).apply {
            setPackage(context.packageName)
            data?.let { putExtra(DATA_KEY, it) }
            context.sendBroadcast(this)
        }
    }
}