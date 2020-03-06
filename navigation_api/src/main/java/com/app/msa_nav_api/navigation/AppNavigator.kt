package com.app.msa_nav_api.navigation

import android.content.Context
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

interface AppNavigator {
    fun navigateToAuthActivity(from: Context)
    fun navigateToMainActivity(from: Context)
//    fun navigateToNewServiceFragment(from: Fragment, requestCode: Int)
}