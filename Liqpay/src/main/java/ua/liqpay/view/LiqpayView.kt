package ua.liqpay.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.util.Base64
import android.util.Base64.NO_WRAP
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import org.json.JSONObject
import ua.liqpay.LIQPAY_BROADCAST_RECEIVER_ACTION
import ua.liqpay.LIQPAY_DATA_KEY
import ua.liqpay.PRIVAT24_APP_PACKAGE
import ua.liqpay.PRIVAT24_APP_URI_SCHEME
import ua.liqpay.utils.URLEncodeUtil
import ua.liqpay.utils.handleAppLink
import ua.liqpay.utils.merge
import java.net.URI
import java.net.URL

private const val SUCCESS_URL_QUERY = "status=success"
private const val CANCEL_URL_PATH = "/cancel"
private const val LIQPAY_HOST = "liqpay.ua"
internal const val BUNDLE_DATA = "data"

@SuppressLint("SetJavaScriptEnabled")
class LiqpayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    internal var loadingListener: LoadingListener? = null

    init {
        loadingListener?.showLoading()
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
                loadingListener?.hideLoading()
                super.onPageFinished(view, url)
            }

            override fun onLoadResource(view: WebView, link: String) {
                super.onLoadResource(view, link)
                val url = URL(link)
                if (!url.host.contains(LIQPAY_HOST)) {
                    return
                }
                when {
                    url.query?.contains(SUCCESS_URL_QUERY) ?: false -> handleSuccessEvent(link)
                    link.contains(CANCEL_URL_PATH) -> handleCancelEvent()
                    else -> handleUnknownEvent(link)
                }
            }
        }
    }

    /**
     * Handle success payment status.
     *
     * @param link Redirect web link
     */
    private fun handleSuccessEvent(link: String) {
        val data = parseUrl(link)
        sendEvent(data.toString())
    }

    /**
     * Handle cancel payment status.
     */
    private fun handleCancelEvent() {
        sendEvent(null)
    }

    /**
     * Handle unknown payment status.
     *
     * @param link Redirect web link
     */
    private fun handleUnknownEvent(link: String) {

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
                if (LIQPAY_DATA_KEY == it.first) {
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
        Intent(LIQPAY_BROADCAST_RECEIVER_ACTION).apply {
            setPackage(context.packageName)
            data?.let { putExtra(BUNDLE_DATA, it) }
            context.sendBroadcast(this)
        }
    }
}