package com.app.mscorebase.utils

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import kotlin.math.pow
import kotlin.math.sqrt

const val TABLET_SCREEN_SIZE = 7

fun isEmulator() = Build.FINGERPRINT.startsWith("generic")
        || Build.FINGERPRINT.startsWith("unknown")
        || Build.MODEL.contains("google_sdk")
        || Build.MODEL.contains("Emulator")
        || Build.MODEL.contains("Android SDK built for x86")
        || Build.BOARD == "QC_Reference_Phone" //bluestacks
        || Build.MANUFACTURER.contains("Genymotion")
        || Build.HOST.startsWith("Build") //MSI App Player
        || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
        || "google_sdk" == Build.PRODUCT

fun isTablet(context: Context): Boolean {
    return try {
        // Compute screen size
        val dm: DisplayMetrics = context.resources.displayMetrics
        val screenWidth: Double = dm.widthPixels / dm.xdpi.toDouble()
        val screenHeight: Double = dm.heightPixels / dm.ydpi.toDouble()
        val size = sqrt(screenWidth.pow(2) + screenHeight.pow(2))
        // Tablet devices should have a screen size greater than 7 inches
        size >= TABLET_SCREEN_SIZE
    } catch (t: Throwable) {
        false
    }
}

fun getScreenSize(context: Context): Point {
    val dm: DisplayMetrics = context.resources.displayMetrics
    return Point(dm.widthPixels, dm.heightPixels)
}