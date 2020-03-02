package com.app.msa_nav_api.navigation

import android.content.Context
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity

interface AppNavigator {
    fun navigateToAuthActivity(from: Context)
    fun navigateToMainActivity(from: Context)
}