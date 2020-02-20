package com.app.mscorebase.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager

fun isWiFiEnabled(context: Context): Boolean {
    var result = false
    val wifi =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    if (wifi != null) {
        result = wifi.isWifiEnabled
    }
    return result
}

fun isOnline(context: Context): Boolean {
    var result = false
    val cm =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (cm != null) {
        val networkInfo = cm.activeNetworkInfo
        if (networkInfo != null) result = networkInfo.isConnectedOrConnecting
    }
    return result
}