package com.app.feature_newservice.ui

import androidx.lifecycle.viewModelScope
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppState
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.MSFragmentViewModel
import com.app.mscoremodels.services.ServiceDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class NewServiceViewModel
    @Inject constructor(val appState: AppState,
                        val dbRepository: DbRepository): MSFragmentViewModel(appState) {

    private val _serviceDurations = StatefulMutableLiveData<List<ServiceDuration>>()
    val serviceDurations: StatefulLiveData<List<ServiceDuration>> = _serviceDurations

    override fun restoreState(writer: StateWriter) {

    }

    override fun saveState(writer: StateWriter) {

    }

    fun getServiceDurations() {
        viewModelScope.launch(Dispatchers.IO) {
            _serviceDurations.postValue(dbRepository.getServiceDurations())
        }
    }
}
