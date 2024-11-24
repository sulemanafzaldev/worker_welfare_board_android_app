package com.example.wwbinspectionapp.utils

import android.app.Activity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.wwbinspectionapp.R

object StatusBarManager {
    //changing status bar color
    fun changeStatusBarColor(activity: Activity, window: Window, color: Int) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(activity, color)

        val decorView: View = window.decorView
        val wic = WindowInsetsControllerCompat(window, decorView)
        //changing status bar icon color
        wic.isAppearanceLightStatusBars = color == R.color.white
    }
}