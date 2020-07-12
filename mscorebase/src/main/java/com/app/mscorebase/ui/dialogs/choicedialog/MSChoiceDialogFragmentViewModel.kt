package com.app.mscorebase.ui.dialogs.choicedialog

import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.MSFragmentViewModel
import java.io.Serializable
import java.util.*

open class MSChoiceDialogFragmentViewModel<C : ChoiceItem<out Serializable>, P>(
    private val appState: AppStateManager,
    open val adapter: SimpleChoiceAdapter<C>
) : MSFragmentViewModel(appState) {
    val choiceMode
        get() = adapter.choiceMode

    private var choices: List<C> = emptyList()
    var singleChoicePosition: Int = -1
        private set
    var resultListener: OnChoiceItemsSelectedListener<C, P?>? = null
    private val intChoicesUpdated = StatefulMutableLiveData<Boolean>()
    val choicesUpdated: StatefulLiveData<Boolean> = intChoicesUpdated

    val selectedItems = mutableListOf<C>()

    private fun getVisibleChoices(choiceItems: List<C>): List<C> {
        val result: MutableList<C> = ArrayList()
        for (i in choiceItems.indices) {
            if (choiceItems[i].isVisible) {
                result.add(choiceItems[i])
            }
        }
        return result
    }

    fun getChoices() = choices

    fun setChoices(choiceItems: List<C>) {
        selectedItems.clear()
        choiceItems.forEach { item ->
            if (item.isSelected) {
                selectedItems.add(item)
                if (choiceMode === ChoiceMode.cmSingle) {
                    singleChoicePosition = choiceItems.indexOf(item)
                    return@forEach
                }
            }
        }
        choices = getVisibleChoices(choiceItems)
        adapter.setChoices(choices)
        intChoicesUpdated.value = true
    }

    val visibleItems: List<C>
        get() = choices

    open fun setSelected(position: Int) {
        if (position in 0..choices.size) {
            if (choiceMode === ChoiceMode.cmSingle) {
                if (selectedItems.size > 0) {
                    if (singleChoicePosition != -1) {
                        choices[singleChoicePosition].isSelected = false
                    }
                    selectedItems.clear()
                }
                selectedItems.add(choices[position])
                singleChoicePosition = position
                choices[singleChoicePosition].isSelected = true
            } else {
                val index = selectedItems.indexOf(choices[position])
                if (index == -1) {
                    selectedItems.add(choices[position])
                    choices[position].isSelected = true
                } else {
                    selectedItems.removeAt(index)
                    choices[position].isSelected = false
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun saveState(writer: StateWriter) {
        val state: Map<String, String> =
            HashMap()
        //fill state
        writer.writeState(this, state)
    }

    override fun restoreState(writer: StateWriter) {
        val state = writer.readState(this) ?: return
        //read state
    }

    override fun clearState(writer: StateWriter) {
        writer.clearState(this, true)
        appState.detachStateHolder(this)
    }
}