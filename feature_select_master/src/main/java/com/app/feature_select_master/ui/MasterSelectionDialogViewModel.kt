package com.app.feature_select_master.ui

import androidx.lifecycle.viewModelScope
import com.app.feature_select_master.adapters.MastersAdapter
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.common.Result
import com.app.mscorebase.ui.dialogs.choicedialog.MSChoiceDialogFragmentViewModel
import com.app.mscoremodels.saloon.ChoosableSaloonMaster
import com.app.mscoremodels.saloon.SaloonFactory
import com.app.mscoremodels.saloon.SaloonMaster
import com.app.mscoremodels.saloon.SaloonService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class MasterSelectionDialogViewModel
    @Inject constructor(appState: AppStateManager,
                        adapter: MastersAdapter,
                        private val dbRepository: DbRepository,
                        private val saloonFactory: SaloonFactory
    ): MSChoiceDialogFragmentViewModel<ChoosableSaloonMaster, String?>(appState, adapter) {

    private var requireServices = emptyList<SaloonService>()
    private var masterId: String? = null

    fun loadMasters() {
        viewModelScope.launch(Dispatchers.IO) {
            val allMastersResult = dbRepository.getMasters(requireServices)
            if (allMastersResult is Result.Success) {
                val selectedMasters = mutableListOf<SaloonMaster>()
                masterId?.let{
                    selectedMasters.addAll(allMastersResult.data.filter { master ->
                        master.id == it
                    })
                }
                val choosable = saloonFactory.createChoosableMasters(allMastersResult.data,
                    selectedMasters)
                    withContext(Dispatchers.Main){setChoices(choosable)}
            }
            else {
                intError.postValue((allMastersResult as Result.Error).exception)
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

    fun setSelectedMasterId(masterId: String?){
        this.masterId = masterId
    }

    fun setRequiredServices(requiredServices: List<SaloonService>) {
        this.requireServices = requiredServices
    }
}