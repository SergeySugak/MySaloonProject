package com.app.msa_nav_api.navigation

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.app.mscorebase.ui.dialogs.choicedialog.NoDataFoundListener
import com.app.mscorebase.ui.dialogs.choicedialog.OnChoiceItemsSelectedListener
import com.app.mscoremodels.saloon.*

interface AppNavigator {
    fun navigateToAuthActivity(from: Context)
    fun navigateToMainActivity(from: Context)

    fun navigateToNewServiceFragment(targetFragment: Fragment)
    fun navigateToEditServiceFragment(targetFragment: Fragment, serviceId: String)
    fun navigateToServiceDurationDialog(targetFragment: Fragment, title: String, durationId: Int?,
                                        resultListener: OnChoiceItemsSelectedListener<ChoosableServiceDuration, Int?>)

    fun navigateToNewMasterFragment(targetFragment: Fragment)
    fun navigateToEditMasterFragment(
        targetFragment: Fragment, masterId: String
    )

    fun navigateToNewEventFragment(
        targetFragment: Fragment,
        eventListener: EventSchedulerListener?
    )

    fun navigateToEditEventFragment(
        targetFragment: Fragment, event: SaloonEvent?,
        eventListener: EventSchedulerListener?
    )

    fun navigateToSelectServicesFragment(
        targetFragment: Fragment,
        title: String,
        payload: String?,
        selectedItems: List<SaloonService>,
        listener: OnChoiceItemsSelectedListener<ChoosableSaloonService, String?>
    )

    fun navigateToSelectMasterFragment(
        targetFragment: Fragment,
        title: String,
        payload: String?,
        requiredServices: List<SaloonService>,
        listener: OnChoiceItemsSelectedListener<ChoosableSaloonMaster, String?>
    )

    fun navigateToSelectEventFragment(
        targetFragment: Fragment,
        title: String,
        filter: String,
        payload: String?,
        listener: OnChoiceItemsSelectedListener<ChoosableSaloonEvent, String?>
    )

    fun navigateToNewConsumableFragment(targetFragment: Fragment)
    fun navigateToEditConsumableFragment(targetFragment: Fragment, consumableId: String)

    fun navigateToSelectUom(targetFragment: Fragment,
                            title: String, uom: String?,
                            listener: OnChoiceItemsSelectedListener<ChoosableUom, String?>)

    interface EventSchedulerListener : Parcelable {
        fun onAdded(event: SaloonEvent)
        fun onUpdated(event: SaloonEvent)
        fun onDeleted(event: SaloonEvent)
        override fun writeToParcel(dest: Parcel?, flags: Int) {}
        override fun describeContents() = 0
    }

}