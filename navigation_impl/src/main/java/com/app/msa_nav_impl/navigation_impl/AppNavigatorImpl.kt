package com.app.msa_nav_impl.navigation_impl

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.app.msa_auth.ui.AuthActivity
import com.app.msa_main.main.MainActivity
import com.app.msa_nav_api.navigation.AppNavigator
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

//    override fun navigateToNewServiceFragment(from: Fragment, requestCode: Int){
//        val intent = Intent(from, NewServiceActivity::class.java)
//        ContextCompat.startActivity(from, intent, bundleOf())
//    }
}