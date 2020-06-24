package com.app.feature_service.ui

import androidx.lifecycle.viewModelScope
import com.app.feature_service.models.ServiceDurationAdapter
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.common.Result
import com.app.mscorebase.ui.dialogs.choicedialog.MSChoiceDialogFragmentViewModel
import com.app.mscoremodels.saloon.ChoosableServiceDuration
import com.app.mscoremodels.saloon.SaloonFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class ServiceDurationSelectionDialogViewModel
@Inject constructor(
    appState: AppStateManager,
    adapter: ServiceDurationAdapter,
    private val dbRepository: DbRepository,
    private val saloonFactory: SaloonFactory
) : MSChoiceDialogFragmentViewModel<ChoosableServiceDuration, Int?>(appState, adapter) {

    fun getServiceDurations(selectedDurationId: Int) {
        intIsInProgress.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val result = dbRepository.getServiceDurations()
            if (result is Result.Success) {
                val choosable = saloonFactory.createChoosableServiceDurations(result.data)
                choosable.forEach { item ->
                    item.isSelected = item.id == selectedDurationId
                }
                withContext(Dispatchers.Main) {
                    setChoices(choosable)
                    intIsInProgress.value = false
                }
            } else {
                intError.postValue((result as Result.Error).exception)
                intIsInProgress.postValue(false)
            }
        }
    }

    override fun saveState(writer: StateWriter) {
        val state: Map<String, String> = HashMap()
        //fill state
        writer.writeState(this, state)
    }

    override fun restoreState(writer: StateWriter) {
        val state = writer.readState(this) ?: return
        //read state
    }
}