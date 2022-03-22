package org.peng.sucicada

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader
import org.peng.sucicada.Util.log
import java.io.File
import java.util.*


class SuActivity : AppCompatActivity() {
    //    val lock1 = ReentrantLock()
//    val lock2 = ReentrantLock()
    lateinit var git: GitDownloader

    @SuppressLint("SetJavaScriptEnabled", "SdCardPath")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_su)

//        val assetsDir = "/mnt/sdcard/Download/test/assets/"
        val webView: WebView = findViewById(R.id.webview)
        val assetsDir = File(filesDir, "assets")
        log(Arrays.asList(assetsDir.list()).toString())

//        lock1.lock()
        Thread {
            run {
//                lock2.lock()
                git = GitDownloader(assetsDir.path)
                git.syncAssets()
//                lock1.unlock()
//                lock1.unlock()
            }
        }.start()

//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
//        lock1.lock()
//        lock1.unlock()
        startWebView(webView, assetsDir)
    }

    var gitOverAlertFlag = false

    fun gitOverAlert(webView: WebView) {
        if (git.isOver() && !gitOverAlertFlag) {
//            webView.loadUrl(
//                """
//                    javascript: alert("app update over")
//                    """.trimIndent()
//            )
            gitOverAlertFlag = true
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun startWebView(webView: WebView, assetsDir: File) {
        val index = File(assetsDir, "index.html")
        while (!index.exists()) {
            log("$index not exist. wait 1s")
            Thread.sleep(1000)
        }
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler(
                "/",
                WebViewAssetLoader.InternalStoragePathHandler(this, assetsDir)
            )
            .build()

        webView.webViewClient = object : WebViewClient() {
            @RequiresApi(21)
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {


                var realUrl =
                    "${request.url.scheme}://${request.url.authority}${request.url.path}"
                if (realUrl.endsWith("/")) {
                    realUrl += "index.html"
                }
                val uri = Uri.parse(realUrl)
                return assetLoader.shouldInterceptRequest(uri)
            }

            override fun onPageFinished(view: WebView?, weburl: String?) {
                gitOverAlert(webView)
            }
            // for API < 21
//            override fun shouldInterceptRequest(view: WebView,
//                                                request: WebResourceRequest): WebResourceResponse? {
//                return assetLoader.shouldInterceptRequest(Uri.parse(request))
//            }
        }
        webView.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                        //表示按返回键时的操作
                        webView.goBack()
                        return true
                    }
                }
                return false
            }
        })
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

        webView.settings.domStorageEnabled = true;
        webView.settings.databaseEnabled = true;

        // Assets are hosted under http(s)://appassets.androidplatform.net/assets/... .
        // If the application's assets are in the "main/assets" folder this will read the file
        // from "main/assets/www/index.html" and load it as if it were hosted on:
        // https://appassets.androidplatform.net/assets/www/index.html
        WebView.setWebContentsDebuggingEnabled(true);
        log("start load url")
        webView.loadUrl("https://appassets.androidplatform.net/")
    }
}