package com.app.feature_newservice.ui

import androidx.lifecycle.viewModelScope
import com.app.feature_newservice.modes.ServiceDurationAdapter
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppState
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.dialogs.choicedialog.MSChoiceDialogFragmentViewModel
import com.app.mscoremodels.services.ServiceDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.HashMap
import javax.inject.Inject

class ServiceDurationSelectionDialogViewModel
    @Inject constructor(appState: AppState,
                        adapter: ServiceDurationAdapter,
                        private val dbRepository: DbRepository
    ): MSChoiceDialogFragmentViewModel<ServiceDuration>(appState, adapter) {

    private val _serviceDurations = StatefulMutableLiveData<List<ServiceDuration>>()
    val serviceDurations: StatefulLiveData<List<ServiceDuration>> = _serviceDurations

    fun getServiceDurations() {
        viewModelScope.launch(Dispatchers.IO) {
            _serviceDurations.postValue(dbRepository.getServiceDurations())
            withContext(Dispatchers.Main){
                if (_serviceDurations.value != null) {
                    setChoices(_serviceDurations.value!!)
                }
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