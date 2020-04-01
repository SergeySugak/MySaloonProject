package com.app.feature_master.ui

import androidx.lifecycle.viewModelScope
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.common.Result
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.MSFragmentViewModel
import com.app.mscoremodels.saloon.SaloonFactory
import com.app.mscoremodels.saloon.SaloonMaster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MasterViewModel
    @Inject constructor(val appState: AppStateManager,
                        private val saloonFactory: SaloonFactory,
                        private val dbRepository: DbRepository): MSFragmentViewModel(appState) {

    private val intMasterInfoSaveState = StatefulMutableLiveData<Boolean>()
    val masterInfoSaveState: StatefulLiveData<Boolean> = intMasterInfoSaveState
    var masterId: String = ""
    private val intMasterInfo = StatefulMutableLiveData<SaloonMaster>()
    val masterInfo: StatefulLiveData<SaloonMaster> = intMasterInfo

    override fun restoreState(writer: StateWriter) {

    }

    override fun saveState(writer: StateWriter) {

    }

    fun saveMasterInfo(name: String, description: String, portfolioUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            intMasterInfoSaveState.postValue(
                run {
                    val result = dbRepository.saveMasterInfo(
                        saloonFactory.createSaloonMaster(masterId, name, description, portfolioUrl)
                    )
                    if (result is Result.Success) {
                        result.data
                    } else {
                        intError.postValue((result as Result.Error).exception)
                        false
                    }
                }
            )
        }
    }

    fun loadData(masterId: String) {
        this.masterId = masterId
        viewModelScope.launch(Dispatchers.IO) {
            val masterResult = dbRepository.loadMasterInfo(masterId)
            if (masterResult is Result.Success) {
                intMasterInfo.postValue(masterResult.data)
            } else {
                intError.postValue((masterResult as Result.Error).exception)
            }
        }
    }
}
