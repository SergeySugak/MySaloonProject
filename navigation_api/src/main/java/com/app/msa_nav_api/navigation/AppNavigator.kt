package com.app.msa_nav_api.navigation

import android.content.Context
import androidx.fragment.app.Fragment
import com.app.mscorebase.ui.dialogs.choicedialog.OnChoiceItemsSelectedListener
import com.app.mscoremodels.saloon.ChoosableSaloonMaster
import com.app.mscoremodels.saloon.ChoosableSaloonService
import com.app.mscoremodels.saloon.SaloonMaster
import com.app.mscoremodels.saloon.SaloonService

interface AppNavigator {
    fun navigateToAuthActivity(from: Context)
    fun navigateToMainActivity(from: Context)
    fun navigateToNewServiceFragment(targetFragment: Fragment, requestCode: Int, tag: String? = null)
    fun navigateToEditServiceFragment(targetFragment: Fragment, serviceId: String,
                                      requestCode: Int, tag: String? = null)
    fun navigateToNewMasterFragment(targetFragment: Fragment, requestCode: Int, tag: String? = null)
    fun navigateToEditMasterFragment(targetFragment: Fragment, masterId: String,
                                      requestCode: Int, tag: String? = null)
    fun navigateToNewEventFragment(targetFragment: Fragment, requestCode: Int, tag: String? = null)
    fun navigateToEditEventFragment(targetFragment: Fragment, eventId: String,
                                     requestCode: Int, tag: String? = null)
    fun navigateToSelectServicesFragment(targetFragment: Fragment,
                                         title: String,
                                         payload: String?,
                                         selectedItems: List<SaloonService>,
                                         listener: OnChoiceItemsSelectedListener<ChoosableSaloonService, String?>)

    fun navigateToSelectMasterFragment(targetFragment: Fragment,
                                       title: String,
                                       payload: String?,
                                       requiredServices: List<SaloonService>,
                                       listener: OnChoiceItemsSelectedListener<ChoosableSaloonMaster, String?>)
}