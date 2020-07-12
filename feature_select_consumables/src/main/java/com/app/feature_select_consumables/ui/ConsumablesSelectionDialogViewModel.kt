package com.app.feature_select_consumables.ui

import androidx.lifecycle.viewModelScope
import com.app.feature_select_consumables.adapters.ConsumablesAdapter
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.common.Result
import com.app.mscorebase.ui.dialogs.choicedialog.MSChoiceDialogFragmentViewModel
import com.app.mscoremodels.saloon.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class ConsumablesSelectionDialogViewModel
@Inject constructor(
    appState: AppStateManager,
    override val adapter: ConsumablesAdapter,
    private val dbRepository: DbRepository,
    private val saloonFactory: SaloonFactory
) : MSChoiceDialogFragmentViewModel<ChoosableSaloonConsumable, String?>(appState, adapter) {

    private var selectedConsumables = emptyList<SaloonUsedConsumable>()

    fun loadChoosableConsumables() {
        viewModelScope.launch(Dispatchers.IO) {
            val allConsumablesResult = dbRepository.getConsumablesAsUsed()
            if (allConsumablesResult is Result.Success) {
                val choosable = saloonFactory.createChoosableConsumables(
                    allConsumablesResult.data.sorted(),
                    selectedConsumables
                )
                withContext(Dispatchers.Main) { setChoices(choosable) }
            } else {
                intError.postValue((allConsumablesResult as Result.Error).exception)
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

    fun setConsumables(consumables: MutableList<SaloonUsedConsumable>) {
        this.selectedConsumables = consumables
        adapter.onQtyChanged = {position: Int ->
            setSelected(position)
        }
    }

    override fun setSelected(position: Int) {
        if (getChoices()[position].isSelected) {
            if (selectedItems.indexOf(getChoices()[position]) == -1) {
                selectedItems.add(getChoices()[position])
            }
        } else {
            selectedItems.remove(getChoices()[position])
        }
    }
}