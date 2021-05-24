package ua.liqpay.view

import android.annotation.SuppressLint
import android.os.Message
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.android.material.bottomsheet.BottomSheetDialog

internal class WindowChromeClient(private val action: () -> Unit) : WebChromeClient() {

        @SuppressLint("SetJavaScriptEnabled")
        override fun onCreateWindow(
            webView: WebView, isDialog: Boolean,
            isUserGesture: Boolean, resultMsg: Message
        ): Boolean {
            val dialogWebView = WebView(webView.context).apply {
                settings.javaScriptEnabled = true
                settings.setSupportZoom(true)
                settings.builtInZoomControls = true
                settings.pluginState = WebSettings.PluginState.ON
                settings.setSupportMultipleWindows(true)
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        view.loadUrl(url)
                        return true
                    }
                }
            }
            BottomSheetDialog(webView.context).apply {
                setContentView(dialogWebView)
                setOnDismissListener {
                    action.invoke()
                }
                show()
            }
            val transport = resultMsg.obj as WebView.WebViewTransport
            transport.webView = dialogWebView
            resultMsg.sendToTarget()
            return true
        }
    }