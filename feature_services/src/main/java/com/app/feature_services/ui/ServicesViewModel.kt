package com.app.feature_services.ui

import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.MSFragmentViewModel
import javax.inject.Inject

class ServicesViewModel
    @Inject constructor(private val appState: AppStateManager,
                        private val dbRepository: DbRepository) : MSFragmentViewModel(appState) {

    private val _services = StatefulMutableLiveData<List<com.app.mscoremodels.saloon.SaloonService>>()
    val services: StatefulLiveData<List<com.app.mscoremodels.saloon.SaloonService>> = _services

    override fun restoreState(writer: StateWriter) {

    }

    override fun saveState(writer: StateWriter) {

    }

    fun loadData() {
        setInProgress(true)
        //_services.postValue(dbRepository.getServicesList())
    }
}
