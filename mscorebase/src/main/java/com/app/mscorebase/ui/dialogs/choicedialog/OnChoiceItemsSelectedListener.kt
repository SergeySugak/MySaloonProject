package com.app.mscorebase.ui.dialogs.choicedialog

import java.io.Serializable

interface OnChoiceItemsSelectedListener<T: ChoiceItem<out Serializable>, P> {
    fun onChoiceItemsSelected(selections: List<T>, payload: P?)
    fun onNoItemSelected(payload: P?)
}