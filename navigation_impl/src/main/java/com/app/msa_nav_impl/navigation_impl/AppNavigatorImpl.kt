package com.app.msa_nav_impl.navigation_impl

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.app.feature_consumable.ui.ConsumableFragment
import com.app.feature_consumable.ui.ConsumableFragment.Companion.ARG_EDIT_CONSUMABLE_ID
import com.app.feature_event_scheduler.ui.EventSchedulerFragment
import com.app.feature_master.ui.MasterFragment
import com.app.feature_master.ui.MasterFragment.Companion.ARG_EDIT_MASTER_ID
import com.app.feature_select_consumables.ui.ConsumablesSelectionDialog
import com.app.feature_select_event.ui.EventSelectionDialog
import com.app.feature_select_master.ui.MasterSelectionDialog
import com.app.feature_select_services.ui.ServicesSelectionDialog
import com.app.feature_select_uom.ui.UomSelectionDialog
import com.app.feature_service.ui.ServiceFragment
import com.app.feature_service.ui.ServiceFragment.Companion.ARG_EDIT_SERVICE_ID
import com.app.feature_service_duration.ui.ServiceDurationSelectionDialog
import com.app.msa_auth.ui.AuthActivity
import com.app.msa_main.main.MainActivity
import com.app.msa_nav_api.navigation.AppNavigator
import com.app.mscorebase.ui.dialogs.choicedialog.OnChoiceItemsSelectedListener
import com.app.mscorebase.ui.dialogs.messagedialog.DialogFragmentPresenterImpl.Companion.showDialogFragment
import com.app.mscoremodels.saloon.*
import java.util.*
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
        showDialogFragment(
            targetFragment,
            newServiceFragment,
            newServiceFragment.javaClass.simpleName
        )
    }

    override fun navigateToServiceDurationDialog(targetFragment: Fragment, title: String, durationId: Int?,
                                                 resultListener: OnChoiceItemsSelectedListener<ChoosableServiceDuration, Int?>){
        val fragment = ServiceDurationSelectionDialog.newInstance(title, durationId, resultListener)
        showDialogFragment(targetFragment, fragment, "")
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
        showDialogFragment(
            targetFragment,
            newServiceFragment,
            newServiceFragment.javaClass.simpleName
        )
    }

    override fun navigateToNewConsumableFragment(targetFragment: Fragment) {
        navigateToEditConsumableFragment(targetFragment, "")
    }

    override fun navigateToEditConsumableFragment(targetFragment: Fragment, consumableId: String) {
        val newConsumableFragment = ConsumableFragment.newInstance().apply {
            retainInstance = true
            if (!TextUtils.isEmpty(consumableId)) {
                arguments = bundleOf(Pair(ARG_EDIT_CONSUMABLE_ID, consumableId))
            }
        }
        showDialogFragment(
            targetFragment,
            newConsumableFragment,
            newConsumableFragment.javaClass.simpleName
        )
    }

    override fun navigateToNewEventFragment(
        targetFragment: Fragment,
        eventListener: AppNavigator.EventSchedulerListener?,
        eventDateTime: Calendar?
    ) {
        val newServiceFragment = EventSchedulerFragment.newInstance(eventDateTime, eventListener).apply {
            retainInstance = true
        }
        showDialogFragment(
            targetFragment,
            newServiceFragment,
            newServiceFragment.javaClass.simpleName
        )
    }

    override fun navigateToEditEventFragment(
        targetFragment: Fragment,
        event: SaloonEvent?, eventListener: AppNavigator.EventSchedulerListener?
    ) {
        val newServiceFragment = EventSchedulerFragment.newInstance(event, eventListener).apply {
            retainInstance = true
        }
        showDialogFragment(
            targetFragment,
            newServiceFragment,
            newServiceFragment.javaClass.simpleName
        )
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

    override fun navigateToSelectEventFragment(
        targetFragment: Fragment,
        title: String,
        filter: String,
        payload: String?,
        listener: OnChoiceItemsSelectedListener<ChoosableSaloonEvent, String?>
    ) {
        val fragment = EventSelectionDialog.newInstance(title, filter, payload, listener)
        showDialogFragment(targetFragment, fragment, "")
    }

    override fun navigateToSelectUom(targetFragment: Fragment,
                                     title: String, uom: String?,
                                     listener: OnChoiceItemsSelectedListener<ChoosableUom, String?>) {
        val fragment = UomSelectionDialog.newInstance(title, uom, listener)
        showDialogFragment(targetFragment, fragment, "")
    }

    override fun navigateToSelectConsumables(
        targetFragment: Fragment,
        title: String,
        selectedItems: List<SaloonUsedConsumable>,
        listener: OnChoiceItemsSelectedListener<ChoosableSaloonConsumable, String?>
    ) {
        val fragment = ConsumablesSelectionDialog.newInstance(title, selectedItems, listener)
        showDialogFragment(targetFragment, fragment, "")
    }
}