package com.app.feature_consumables.ui

import androidx.lifecycle.viewModelScope
import com.app.feature_consumables.R
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.common.Result
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.MSFragmentViewModel
import com.app.mscoremodels.saloon.SaloonConsumable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ConsumablesViewModel
@Inject constructor(
    private val appState: AppStateManager,
    private val dbRepository: DbRepository
) : MSFragmentViewModel(appState) {

    private val _consumables = StatefulMutableLiveData<MutableList<SaloonConsumable>>(mutableListOf())
    val consumables: StatefulLiveData<MutableList<SaloonConsumable>> = _consumables

    private val listenerId: String

    override fun restoreState(writer: StateWriter) {

    }

    override fun saveState(writer: StateWriter) {

    }

    fun deleteConsumable(consumableId: String?) {
        consumableId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val hasRelatedEventsResult = dbRepository.consumableHasRelatedEvent(consumableId)
                if (hasRelatedEventsResult is Result.Success) {
                    if (!hasRelatedEventsResult.data){
                        val result = dbRepository.deleteConsumableInfo(consumableId)
                        if (result is Result.Error) {
                            intError.postValue(result.exception)
                        }
                    }
                    else {
                        intError.postValue(Exception(
                            appState.context.getString(R.string.str_cant_delete_active_consumable)))
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

    private fun onConsumableInserted(consumable: SaloonConsumable) {
        var consumables = _consumables.value
        if (consumables == null) {
            consumables = mutableListOf()
        }
        consumables.add(consumable)
        consumables.sort()
        _consumables.value = consumables
    }

    private fun onConsumableUpdated(changedConsumableId: String, consumable: SaloonConsumable) {
        var consumables = _consumables.value
        if (consumables == null) {
            consumables = mutableListOf()
            consumables.add(consumable)
        } else {
            for (curConsumable in consumables) {
                if (curConsumable.id == changedConsumableId) {
                    val pos = consumables.indexOf(curConsumable)
                    consumables[pos] = consumable
                    break
                }
            }
        }
        consumables.sort()
        _consumables.value = consumables
    }

    private fun onConsumableDeleted(deletedConsumableId: String) {
        val consumables = _consumables.value
        if (consumables != null) {
            for (curConsumable in consumables) {
                if (curConsumable.id == deletedConsumableId) {
                    val pos = consumables.indexOf(curConsumable)
                    consumables.removeAt(pos)
                    break
                }
            }
        }
        _consumables.value = consumables
    }

    private fun onDatabaseError(exception: Exception) {
        intError.value = exception
    }

    init {
        listenerId = dbRepository.startListenToConsumables(
            ::onConsumableInserted, ::onConsumableUpdated, ::onConsumableDeleted, ::onDatabaseError
        )
    }
}
