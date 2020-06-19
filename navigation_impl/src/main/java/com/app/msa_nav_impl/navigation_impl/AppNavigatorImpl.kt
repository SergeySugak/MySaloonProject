package com.app.msa_nav_impl.navigation_impl

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.app.feature_event_scheduler.ui.EventSchedulerFragment
import com.app.feature_master.ui.MasterFragment
import com.app.feature_master.ui.MasterFragment.Companion.ARG_EDIT_MASTER_ID
import com.app.feature_select_master.ui.MasterSelectionDialog
import com.app.feature_select_services.ui.ServicesSelectionDialog
import com.app.feature_service.ui.ServiceFragment
import com.app.feature_service.ui.ServiceFragment.Companion.ARG_EDIT_SERVICE_ID
import com.app.msa_auth.ui.AuthActivity
import com.app.msa_main.main.MainActivity
import com.app.msa_nav_api.navigation.AppNavigator
import com.app.mscorebase.ui.dialogs.choicedialog.OnChoiceItemsSelectedListener
import com.app.mscorebase.ui.dialogs.messagedialog.DialogFragmentPresenterImpl.Companion.showDialogFragment
import com.app.mscoremodels.saloon.ChoosableSaloonMaster
import com.app.mscoremodels.saloon.ChoosableSaloonService
import com.app.mscoremodels.saloon.SaloonEvent
import com.app.mscoremodels.saloon.SaloonService
import javax.inject.Inject

class AppNavigatorImpl @Inject constructor() : AppNavigator {
    override fun navigateToAuthActivity(from: Context) {
        val intent = Intent(from, AuthActivity::class.java)
        ContextCompat.startActivity(from, intent, bundleOf())
    }

    override fun navigateToMainActivity(from: Context) {
        val intent = Intent(from, MainActivity::class.java)
        ContextCompat.startActivity(from, intent, bundleOf())
    }

    override fun navigateToNewServiceFragment(targetFragment: Fragment) {
        navigateToEditServiceFragment(targetFragment, "")
    }

    override fun navigateToEditServiceFragment(targetFragment: Fragment, serviceId: String) {
        val newServiceFragment = ServiceFragment.newInstance().apply {
            retainInstance = true
            if (!TextUtils.isEmpty(serviceId)) {
                arguments = bundleOf(Pair(ARG_EDIT_SERVICE_ID, serviceId))
            }
        }
        showDialogFragment(targetFragment, newServiceFragment, newServiceFragment.javaClass.simpleName)
    }

    override fun navigateToNewMasterFragment(targetFragment: Fragment) {
        navigateToEditMasterFragment(targetFragment, "")
    }

    override fun navigateToEditMasterFragment(targetFragment: Fragment, masterId: String) {
        val newServiceFragment = MasterFragment.newInstance().apply {
            retainInstance = true
            if (!TextUtils.isEmpty(masterId)) {
                arguments = bundleOf(Pair(ARG_EDIT_MASTER_ID, masterId))
            }
        }
        showDialogFragment(targetFragment, newServiceFragment, newServiceFragment.javaClass.simpleName)
    }

    override fun navigateToNewEventFragment(targetFragment: Fragment,
                                            eventListener: AppNavigator.EventSchedulerListener?) {
        navigateToEditEventFragment(targetFragment, "", eventListener)
    }

    override fun navigateToEditEventFragment(targetFragment: Fragment, id: String,
                                             eventListener: AppNavigator.EventSchedulerListener?) {
        val newServiceFragment = EventSchedulerFragment.newInstance(id, eventListener).apply {
            retainInstance = true
        }
        showDialogFragment(targetFragment, newServiceFragment, newServiceFragment.javaClass.simpleName)
    }

    override fun navigateToSelectServicesFragment(
        targetFragment: Fragment,
        title: String, payload: String?,
        selectedItems: List<SaloonService>,
        listener: OnChoiceItemsSelectedListener<ChoosableSaloonService, String?>
    ) {
        val fragment = ServicesSelectionDialog.newInstance(title, payload, selectedItems, listener)
        showDialogFragment(targetFragment, fragment, "")
    }

    override fun navigateToSelectMasterFragment(
        targetFragment: Fragment,
        title: String,
        payload: String?,
        requiredServices: List<SaloonService>,
        listener: OnChoiceItemsSelectedListener<ChoosableSaloonMaster, String?>
    ) {
        val fragment = MasterSelectionDialog.newInstance(title, payload, requiredServices, listener)
        showDialogFragment(targetFragment, fragment, "")
    }
}