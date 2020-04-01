package com.app.msa_nav_api.navigation

import android.content.Context
import androidx.fragment.app.Fragment

interface AppNavigator {
    fun navigateToAuthActivity(from: Context)
    fun navigateToMainActivity(from: Context)
    fun navigateToNewServiceFragment(targetFragment: Fragment, requestCode: Int, tag: String? = null)
    fun navigateToEditServiceFragment(targetFragment: Fragment, serviceId: String,
                                      requestCode: Int, tag: String? = null)
    fun navigateToNewMasterFragment(targetFragment: Fragment, requestCode: Int, tag: String? = null)
    fun navigateToEditMasterFragment(targetFragment: Fragment, masterId: String,
                                      requestCode: Int, tag: String? = null)
}