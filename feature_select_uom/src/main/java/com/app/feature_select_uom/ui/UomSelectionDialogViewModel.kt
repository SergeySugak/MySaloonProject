package com.app.feature_select_uom.ui

import androidx.lifecycle.viewModelScope
import com.app.feature_select_uom.adapters.UomsAdapter
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.common.Result
import com.app.mscorebase.ui.dialogs.choicedialog.MSChoiceDialogFragmentViewModel
import com.app.mscoremodels.saloon.ChoosableUom
import com.app.mscoremodels.saloon.SaloonFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class UomSelectionDialogViewModel
@Inject constructor(
    appState: AppStateManager,
    adapter: UomsAdapter,
    private val dbRepository: DbRepository,
    private val saloonFactory: SaloonFactory
) : MSChoiceDialogFragmentViewModel<ChoosableUom, String?>(appState, adapter) {

    private var uoms = emptyList<String>()

    fun getUoms(selectedUom: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val allUomsResult = dbRepository.getUoms()
            if (allUomsResult is Result.Success) {
                val choosable = saloonFactory.createChoosableUom(allUomsResult.data, uoms)
                choosable.forEach { item ->
                    item.isSelected = item.id == selectedUom
                }
                withContext(Dispatchers.Main) {
                    setChoices(choosable)
                }
            } else {
                intError.postValue((allUomsResult as Result.Error).exception)
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

    fun setUoms(uoms: MutableList<String>) {
        this.uoms = uoms
    }
}