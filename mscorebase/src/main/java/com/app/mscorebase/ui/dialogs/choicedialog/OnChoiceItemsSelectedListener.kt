package com.app.mscorebase.ui.dialogs.choicedialog

import android.os.Parcelable
import java.io.Serializable

interface OnChoiceItemsSelectedListener<T: ChoiceItem<out Serializable>, P>: Parcelable {
    fun onChoiceItemsSelected(selections: List<T>, payload: P?)
    fun onNoItemSelected(payload: P?)
}