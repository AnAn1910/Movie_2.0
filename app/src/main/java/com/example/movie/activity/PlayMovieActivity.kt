package com.example.movie.activity

import android.annotation.SuppressLint
//import android.annotation.TargetApi
import android.graphics.Bitmap
import android.graphics.BitmapFactory
//import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.movie.MyApplication
import com.example.movie.R
import com.example.movie.model.Movie
import io.github.rupinderjeet.kprogresshud.KProgressHUD


class PlayMovieActivity : AppCompatActivity() {

    private var progressHUD: KProgressHUD? = null
    private var webView: WebView? = null
    private var mMovie: Movie? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        @Suppress("DEPRECATION")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_play_movie)

        getDataIntent()
        webView = findViewById<View?>(R.id.web_view) as WebView?
        progressHUD = KProgressHUD.create(this@PlayMovieActivity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait...")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show()
        val webSettings = webView?.settings
        webSettings?.javaScriptEnabled = true
        webView?.settings?.cacheMode=WebSettings.LOAD_NO_CACHE
        webSettings?.allowFileAccess = true
        webSettings?.builtInZoomControls = false
        webSettings?.domStorageEnabled = true
        //improve webView performance
        //webSettings?.setRenderPriority(WebSettings.RenderPriority.HIGH)
        webSettings?.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        webView?.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webSettings?.useWideViewPort = true
        //webSettings?.savePassword = true
        //webSettings?.setEnableSmoothTransition(true)
        webView?.webChromeClient = ChromeClient()
        webView?.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                title = "Loading " + mMovie?.getTitle()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                title = view?.title
                progressHUD?.dismiss()
            }
        }
        mMovie?.getUrl()?.let { webView?.loadUrl(it) }
        val layoutFooter = findViewById<View?>(R.id.layout_footer) as LinearLayout?
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Your back button handling logic goes here
            }
        }

        layoutFooter?.setOnClickListener {
            callback.handleOnBackPressed()
        }

        this.onBackPressedDispatcher.addCallback(this, callback)
        setHistory()
    }

    private fun getDataIntent() {
        val bundle = intent.extras ?: return
        mMovie = bundle.getBundle("object_movie") as Movie?
    }


    private fun setHistory() {
        if (mMovie!!.isHistory()) {
            return
        }
        val map = mapOf("history" to true)
        MyApplication[this]?.getDatabaseReference()
                ?.child(mMovie?.getId().toString())?.updateChildren(map)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView?.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webView?.restoreState(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        webView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView?.onResume()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (webView!!.canGoBack()) {
                        webView!!.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private inner class ChromeClient : WebChromeClient() {
        private var mCustomView: View? = null
        private var mCustomViewCallback: CustomViewCallback? = null
        private var mOriginalOrientation = 0
        private var mOriginalSystemUiVisibility = 0
        override fun getDefaultVideoPoster(): Bitmap? {
            return if (mCustomView == null) {
                null
            } else BitmapFactory.decodeResource(applicationContext.resources, 2130837573)
        }

        override fun onHideCustomView() {
            (window.decorView as FrameLayout).removeView(mCustomView)
            mCustomView = null
            WindowInsetsControllerCompat(window, window.decorView).show(mOriginalSystemUiVisibility)
            requestedOrientation = mOriginalOrientation
            mCustomViewCallback?.onCustomViewHidden()
            mCustomViewCallback = null
        }

        override fun onShowCustomView(paramView: View?, paramCustomViewCallback: CustomViewCallback?) {
            if (mCustomView != null) {
                onHideCustomView()
                return
            }
            mCustomView = paramView
            mOriginalSystemUiVisibility = WindowInsetsControllerCompat(window, window.decorView).systemBarsBehavior
            mOriginalOrientation = requestedOrientation
            mCustomViewCallback = paramCustomViewCallback
            (window.decorView as FrameLayout).addView(mCustomView, FrameLayout.LayoutParams(-1, -1))
            WindowInsetsControllerCompat(window, window.decorView).apply {
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                hide(WindowInsetsCompat.Type.systemBars())
            }
        }

    }
}