package com.app.feature_masters.ui

import androidx.lifecycle.viewModelScope
import com.app.feature_masters.R
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.common.Result
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.MSFragmentViewModel
import com.app.mscoremodels.saloon.SaloonMaster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MastersViewModel
@Inject constructor(
    private val appState: AppStateManager,
    private val dbRepository: DbRepository
) : MSFragmentViewModel(appState) {

    private val _masters = StatefulMutableLiveData<MutableList<SaloonMaster>>(mutableListOf())
    val masters: StatefulLiveData<MutableList<SaloonMaster>> = _masters

    private val listenerId: String

    override fun restoreState(writer: StateWriter) {

    }

    override fun saveState(writer: StateWriter) {

    }

    fun deleteMaster(masterId: String?) {
        masterId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val hasRelatedEventsResult = dbRepository.masterHasRelatedEvent(masterId)
                if (hasRelatedEventsResult is Result.Success) {
                    if (!hasRelatedEventsResult.data){
                        val result = dbRepository.deleteMasterInfo(masterId)
                        if (result is Result.Error) {
                            intError.postValue(result.exception)
                        }
                    }
                    else {
                        intError.postValue(Exception(
                            appState.context.getString(R.string.str_cant_delete_active_master)))
                    }
                }
                else {
                    intError.postValue((hasRelatedEventsResult as Result.Error).exception)
                }
            }
        }
    }

    override fun onCleared() {
        dbRepository.stopListeningToMasters(listenerId)
        super.onCleared()
    }

    private fun onMasterInserted(master: SaloonMaster) {
        var masters = _masters.value
        if (masters == null) {
            masters = mutableListOf()
        }
        masters.add(master)
        _masters.value = masters
    }

    private fun onMasterUpdated(changedMasterId: String, master: SaloonMaster) {
        var masters = _masters.value
        if (masters == null) {
            masters = mutableListOf()
            masters.add(master)
        } else {
            for (curMaster in masters) {
                if (curMaster.id == changedMasterId) {
                    val pos = masters.indexOf(curMaster)
                    masters[pos] = master
                    break
                }
            }
        }
        _masters.value = masters
    }

    private fun onMasterDeleted(deletedMasterId: String) {
        val masters = _masters.value
        if (masters != null) {
            for (curMaster in masters) {
                if (curMaster.id == deletedMasterId) {
                    val pos = masters.indexOf(curMaster)
                    masters.removeAt(pos)
                    break
                }
            }
        }
        _masters.value = masters
    }

    private fun onDatabaseError(exception: Exception) {
        intError.value = exception
    }

    init {
        listenerId = dbRepository.startListenToMasters(
            ::onMasterInserted, ::onMasterUpdated, ::onMasterDeleted, ::onDatabaseError
        )
    }
}
