package com.app.mscorebase.ui.dialogs.choicedialog

import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.ui.MSFragmentViewModel
import java.io.Serializable
import java.util.*

open class MSChoiceDialogFragmentViewModel<C : ChoiceItem<out Serializable>, P>(
    private val appState: AppStateManager,
    val adapter: SimpleChoiceAdapter<C>
) : MSFragmentViewModel(appState) {
    val choiceMode
        get() = adapter.choiceMode

    var selectedPosition = -1
    private var choices: List<C> = emptyList()
    var resultListener: OnChoiceItemsSelectedListener<C, P?>? = null

    fun setChoices(choices: List<C>) {
        this.choices = choices
        adapter.setChoices(visibleItems)
    }

    private fun getChoices(): List<C> {
        return adapter.getChoices()
    }

    val visibleItems: List<C>
        get() {
            val result: MutableList<C> = ArrayList()
            for (i in choices.indices) {
                if (choices[i].isVisible) {
                    result.add(choices[i])
                }
            }
            return result
        }

    fun setSelected(position: Int, selected: Boolean) {
        if (position in 0..getChoices().size) {
            if (choiceMode === ChoiceMode.cmMulti) {
                getChoices()[position].isSelected = selected
            } else {
                selectedPosition = if (selected) position else -1
            }
        }
    }

    val multiSelectedPositions: BooleanArray
        get() {
            val result = BooleanArray(getChoices().size)
            for (i in getChoices().indices) {
                result[i] = getChoices()[i].isSelected
            }
            return result
        }

    val selectedItem: List<C>
        get() {
            val selectedPos = selectedPosition
            var invisible = 0
            var visible = 0
            for (i in getChoices().indices) {
                if (!getChoices()[i].isVisible) {
                    invisible++
                } else {
                    if (visible == selectedPos) {
                        break
                    }
                    visible++
                }
            }
            val result: MutableList<C> = ArrayList()
            result.add(getChoices()[invisible + selectedPos])
            return result
        }

    val multiSelections: List<C>
        get() {
            val result = ArrayList<C>(getChoices().size)
            for (i in getChoices().indices) {
                if (getChoices()[i].isSelected) {
                    result.add(getChoices()[i])
                }
            }
            return result
        }

    val choicesArray: Array<String?>
        get() {
            val temp: MutableList<String?> = ArrayList(getChoices().size)
            for (i in getChoices().indices) {
                if (getChoices()[i].isVisible) {
                    temp.add(getChoices()[i].name)
                }
            }
            return temp.toTypedArray()
        }

    override fun saveState(writer: StateWriter) {
        val state: Map<String, String> =
            HashMap()
        //fill state
        writer.writeState(this, state)
    }

    override fun restoreState(writer: StateWriter) {
        val state = writer.readState(this)
        //read state
    }

    override fun clearState(writer: StateWriter) {
        writer.clearState(this, true)
        appState.detachStateManager(this)
    }
}