package com.djawnstj.pixel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.djawnstj.pixel.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PACS_URL = "https://google.com"
    }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViewListener()
        initWebView()
    }

    private fun initViewListener() {
        binding.scrapButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                binding.target.getBitmap(binding.imageView::setImageBitmap)
            }
        }

        binding.changeButton.setOnClickListener {
            binding.title.isVisible = !binding.title.isVisible
            binding.webView.isVisible = !binding.webView.isVisible
        }

        binding.copyButton.setOnClickListener {
            val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("url", PACS_URL)

            clipboardManager.setPrimaryClip(clipData)
        }
    }

    private fun initWebView() {
        binding.webView.apply {

            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            settings.savePassword = false
            settings.saveFormData = false
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true

            val databasePath = this@MainActivity.getDir("database", Context.MODE_PRIVATE).path
            settings.databaseEnabled = true
            settings.domStorageEnabled = true
            settings.databasePath = databasePath
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true

            loadUrl(PACS_URL)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun View.getBitmap(callback: (Bitmap?) -> Unit) {
        window.let { window ->
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            val locationOfViewWindow = IntArray(2)
            getLocationInWindow(locationOfViewWindow)

            try {
                PixelCopy.request(
                    window,
                    Rect(locationOfViewWindow[0], locationOfViewWindow[1], locationOfViewWindow[0] + width, locationOfViewWindow[1] + height),
                    bitmap, { copyResult ->
                        if (copyResult == PixelCopy.SUCCESS) callback.invoke(bitmap)
                        else callback.invoke(null)
                    }, Handler(Looper.getMainLooper()))
            } catch (e: Exception) {
                callback.invoke(null)
            }
        }
    }

}