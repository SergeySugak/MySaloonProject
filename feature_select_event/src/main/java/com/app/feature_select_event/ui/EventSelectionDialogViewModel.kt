package com.app.feature_select_event.ui

import androidx.lifecycle.viewModelScope
import com.app.feature_select_event.adapters.EventsAdapter
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.common.Result
import com.app.mscorebase.ui.dialogs.choicedialog.MSChoiceDialogFragmentViewModel
import com.app.mscoremodels.saloon.ChoosableSaloonEvent
import com.app.mscoremodels.saloon.SaloonFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class EventSelectionDialogViewModel
@Inject constructor(
    appState: AppStateManager,
    adapter: EventsAdapter,
    private val dbRepository: DbRepository,
    private val saloonFactory: SaloonFactory
) : MSChoiceDialogFragmentViewModel<ChoosableSaloonEvent, String?>(appState, adapter) {

    fun loadEvents(filter: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val allEventsResult = dbRepository.getAllEvents()
            if (allEventsResult is Result.Success) {
                val choosable = saloonFactory.createChoosableEvents(
                    allEventsResult.data,
                    emptyList(),
                    filter
                )
                withContext(Dispatchers.Main) { setChoices(choosable) }
            } else {
                intError.postValue((allEventsResult as Result.Error).exception)
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