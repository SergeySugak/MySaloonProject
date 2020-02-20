package com.app.msa.navigation

import android.content.Context

interface AppNavigator {
    fun navigateToAuthActivity(from: Context)
    fun navigateToMainActivity(from: Context)
}