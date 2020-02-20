package com.app.msa.navigation_impl

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.app.msa.main.MainActivity
import com.app.msa.navigation.AppNavigator
import com.app.msa.ui.AuthActivity
import javax.inject.Inject

class AppNavigatorImpl @Inject constructor(): AppNavigator {
    override fun navigateToAuthActivity(from: Context) {
        val intent = Intent(from, AuthActivity::class.java)
        ContextCompat.startActivity(from, intent, bundleOf())
    }

    override fun navigateToMainActivity(from: Context) {
        val intent = Intent(from, MainActivity::class.java)
        ContextCompat.startActivity(from, intent, bundleOf())
    }
}