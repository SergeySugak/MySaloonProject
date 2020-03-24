package com.app.feature_newservice.ui

import androidx.lifecycle.viewModelScope
import com.app.feature_newservice.models.ServiceDurationAdapter
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.common.Result
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.dialogs.choicedialog.MSChoiceDialogFragmentViewModel
import com.app.mscoremodels.saloon.ServiceDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class ServiceDurationSelectionDialogViewModel
    @Inject constructor(appState: AppStateManager,
                        adapter: ServiceDurationAdapter,
                        private val dbRepository: DbRepository
    ): MSChoiceDialogFragmentViewModel<ServiceDuration>(appState, adapter) {

    private val _serviceDurations = StatefulMutableLiveData<List<ServiceDuration>>()
    val serviceDurations: StatefulLiveData<List<ServiceDuration>> = _serviceDurations

    fun getServiceDurations() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = dbRepository.getServiceDurations()
            if (result is Result.Success) {
                _serviceDurations.postValue(result.data)
                withContext(Dispatchers.Main) {
                    if (_serviceDurations.value != null) {
                        setChoices(_serviceDurations.value!!)
                    }
                }
            }
            else {
                intError.postValue((result as Result.Error).exception)
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

    init {
        getServiceDurations()
    }
}