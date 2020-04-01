package com.app.mscorebase.ui.dialogs.choicedialog

import java.io.Serializable

interface OnChoiceItemsSelectedListener<T: ChoiceItem<out Serializable>, P> {
    fun onChoiceItemsSelected(item: List<T>, payload: P?)
    fun onNoItemSelected(payload: P?)
}