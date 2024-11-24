package com.example.wwbinspectionapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wwbinspectionapp.R
import com.example.wwbinspectionapp.databinding.ActivitySplashBinding
import com.example.wwbinspectionapp.utils.StatusBarManager

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarManager.changeStatusBarColor(this, window, R.color.white)

        // Load logo animation (fade-in + scale)
        val logoAnimation = AnimationUtils.loadAnimation(this, R.anim.scale)

        // Load text animation (slide-in from bottom + fade-in)
        val textAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_fade_in_translate)

        // Start animations
        binding.appLogo.startAnimation(logoAnimation)
        webView = binding.appTitle
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        webView.loadUrl("file:///android_asset/animi.html")

        val timer = Thread {
            try {
                Thread.sleep(3240)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } finally {
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
        }
        timer.start()
    }
}
