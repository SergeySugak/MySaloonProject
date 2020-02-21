package com.app.msa_nav_api.navigation

import android.content.Context

interface AppNavigator {
    fun navigateToAuthActivity(from: Context)
    fun navigateToMainActivity(from: Context)
}