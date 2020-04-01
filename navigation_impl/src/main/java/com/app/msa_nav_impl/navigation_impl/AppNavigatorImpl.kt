package com.app.msa_nav_impl.navigation_impl

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.app.feature_master.ui.MasterFragment
import com.app.feature_master.ui.MasterFragment.Companion.ARG_EDIT_MASTER_ID
import com.app.feature_service.ui.ServiceFragment
import com.app.feature_service.ui.ServiceFragment.Companion.ARG_EDIT_SERVICE_ID
import com.app.msa_auth.ui.AuthActivity
import com.app.msa_main.main.MainActivity
import com.app.msa_nav_api.navigation.AppNavigator
import com.app.mscorebase.ui.dialogs.messagedialog.DialogFragmentPresenterImpl.Companion.showDialogFragment
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

    override fun navigateToNewServiceFragment(targetFragment: Fragment, requestCode: Int, tag: String?){
        navigateToEditServiceFragment(targetFragment, "", requestCode, tag)
    }

    override fun navigateToEditServiceFragment(targetFragment: Fragment, serviceId: String,
                                      requestCode: Int, tag: String?){
        val newServiceFragment = ServiceFragment.newInstance().apply {
            retainInstance = true
            setTargetFragment(targetFragment, requestCode)
            if (!TextUtils.isEmpty(serviceId)) {
                arguments = bundleOf(Pair(ARG_EDIT_SERVICE_ID, serviceId))
            }
        }
        showDialogFragment(targetFragment, newServiceFragment, tag)
    }

    override fun navigateToNewMasterFragment(targetFragment: Fragment, requestCode: Int, tag: String?){
        navigateToEditMasterFragment(targetFragment, "", requestCode, tag)
    }

    override fun navigateToEditMasterFragment(targetFragment: Fragment, masterId: String,
                                               requestCode: Int, tag: String?){
        val newServiceFragment = MasterFragment.newInstance().apply {
            retainInstance = true
            setTargetFragment(targetFragment, requestCode)
            if (!TextUtils.isEmpty(masterId)) {
                arguments = bundleOf(Pair(ARG_EDIT_MASTER_ID, masterId))
            }
        }
        showDialogFragment(targetFragment, newServiceFragment, tag)
    }

}