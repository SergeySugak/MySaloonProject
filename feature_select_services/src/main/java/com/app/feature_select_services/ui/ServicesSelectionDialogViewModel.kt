package com.app.feature_select_services.ui

import androidx.lifecycle.viewModelScope
import com.app.feature_select_services.adapters.ServicesAdapter
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.common.Result
import com.app.mscorebase.ui.dialogs.choicedialog.MSChoiceDialogFragmentViewModel
import com.app.mscoremodels.saloon.ChoosableSaloonService
import com.app.mscoremodels.saloon.SaloonFactory
import com.app.mscoremodels.saloon.SaloonService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class ServicesSelectionDialogViewModel
    @Inject constructor(appState: AppStateManager,
                        adapter: ServicesAdapter,
                        private val dbRepository: DbRepository,
                        private val saloonFactory: SaloonFactory
    ): MSChoiceDialogFragmentViewModel<ChoosableSaloonService, String?>(appState, adapter) {

    private var masterServices = emptyList<SaloonService>()

    fun loadServices() {
        viewModelScope.launch(Dispatchers.IO) {
            val allServicesResult = dbRepository.getServices()
            if (allServicesResult is Result.Success) {
                val choosable = saloonFactory.createChoosableServices(allServicesResult.data,
                    masterServices)
                    withContext(Dispatchers.Main){setChoices(choosable)}
            }
            else {
                intError.postValue((allServicesResult as Result.Error).exception)
            }
        }
    }

    override fun saveState(writer: StateWriter) {
        val state: Map<String, String> = HashMap()
        //fill state
        writer.writeState(this, state)
    }

    override fun restoreState(writer: StateWriter) {
        val state = writer.readState(this)
        //read state
    }

    fun setMasterServices(masterServices: MutableList<SaloonService>) {
        this.masterServices = masterServices
    }
}