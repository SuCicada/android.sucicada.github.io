package org.peng.sucicada

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewAssetLoader.AssetsPathHandler


class SuActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_su)

        val webView: WebView = findViewById(R.id.webview)

        val assetLoader = WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", AssetsPathHandler(this))
                .build()

        webView.webViewClient = object : WebViewClient() {
            @RequiresApi(21)
            override fun shouldInterceptRequest(view: WebView,
                                                request: WebResourceRequest): WebResourceResponse? {
                var realUrl = "${request.url.scheme}://${request.url.authority}/assets/public${request.url.path}"
                if (realUrl.endsWith("/")) {
                    realUrl += "index.html"
                }
                val uri = Uri.parse(realUrl)
                return assetLoader.shouldInterceptRequest(uri)
            }

            // for API < 21
//            override fun shouldInterceptRequest(view: WebView,
//                                                request: WebResourceRequest): WebResourceResponse? {
//                return assetLoader.shouldInterceptRequest(Uri.parse(request))
//            }
        }

        val webViewSettings = webView.settings
        // Setting this off for security. Off by default for SDK versions >= 16.
        webViewSettings.allowFileAccessFromFileURLs = true
        // Off by default, deprecated for SDK versions >= 30.
        webViewSettings.allowUniversalAccessFromFileURLs = true
        // Keeping these off is less critical but still a good idea, especially if your app is not
        // using file:// or content:// URLs.
        webViewSettings.allowFileAccess = true
        webViewSettings.allowContentAccess = true

        //设置为ChromeClinet 才能执行js代码
        webView.webChromeClient = WebChromeClient()
        //设置开启js支持
        webView.settings.javaScriptEnabled = true;
        // 是否支持缩放
        webView.settings.setSupportZoom(true);

        // Assets are hosted under http(s)://appassets.androidplatform.net/assets/... .
        // If the application's assets are in the "main/assets" folder this will read the file
        // from "main/assets/www/index.html" and load it as if it were hosted on:
        // https://appassets.androidplatform.net/assets/www/index.html
        WebView.setWebContentsDebuggingEnabled(true);

        webView.loadUrl("https://appassets.androidplatform.net/")
    }
}