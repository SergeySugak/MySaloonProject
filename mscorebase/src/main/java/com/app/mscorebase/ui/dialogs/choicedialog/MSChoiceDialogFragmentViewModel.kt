package com.app.mscorebase.ui.dialogs.choicedialog

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import com.app.mscorebase.appstate.AppState
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.ui.MSFragmentViewModel
import java.io.Serializable
import java.util.*

open class MSChoiceDialogFragmentViewModel<C : ChoiceItem<out Serializable>>(
    private val appState: AppState,
    val adapter: SingleChoiceAdapter<C>
) : MSFragmentViewModel(appState) {
    val LDE_DATA_LOADED =
        MutableLiveData<Boolean>()
    var choiceMode = ChoiceItem.ChoiceMode.cmSingle
    var selectedPosition = -1
    private val choices: MutableLiveData<List<C>> = MutableLiveData()
    var resultListener: OnChoiceItemsSelectedListener<C, out Parcelable?>? = null

    fun setChoices(choices: List<C>) {
        this.choices.value = choices
        adapter?.setItems(visibleItems)
    }

    fun getChoices(): List<C> {
        return if (choices.value == null)
            emptyList()
        else {
            Collections.unmodifiableList(choices.value!!)
        }
    }

    val visibleItems: List<C>
        get() {
            val result: MutableList<C> = ArrayList()
            val choices = choices.value
            if (choices != null) {
                for (i in choices.indices) {
                    if (choices[i].isVisible) {
                        result.add(choices[i])
                    }
                }
            }
            return result
        }

    fun setSelected(position: Int, selected: Boolean) {
        if (choiceMode === ChoiceItem.ChoiceMode.cmMulti) {
            if (choices.value != null) {
                choices.value?.get(position)?.isSelected = selected
            }
        } else {
            selectedPosition = if (selected) position else -1
        }
    }

    val multiSelectedPositions: BooleanArray
        get() = if (choices.value == null || choices.value!!.isEmpty()) {
            BooleanArray(0)
        } else {
            val result = BooleanArray(choices.value!!.size)
            for (i in choices.value!!.indices) {
                result[i] = choices.value!!.get(i).isSelected
            }
            result
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
        get() = if (choices.value == null || choices.value!!.isEmpty()) {
            emptyList()
        } else {
            val result: MutableList<C> = ArrayList()
            for (i in choices.value!!.indices) {
                if (choices.value!![i].isSelected) {
                    result.add(choices.value!![i])
                }
            }
            result
        }

    val choicesArray: Array<String?>
        get() = if (choices.value == null || choices.value!!.isEmpty()) arrayOfNulls(
            0
        ) else {
            val temp: MutableList<String?> =
                ArrayList(choices.value!!.size)
            for (i in choices.value!!.indices) {
                if (choices.value!![i].isVisible) {
                    temp.add(choices.value!![i].name)
                }
            }
            temp.toTypedArray()
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