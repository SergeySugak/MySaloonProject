package com.app.mscorebase.ui.dialogs.choicedialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable

abstract class RecyclerSingleChoiceAdapter<T : ChoiceItem<out Serializable>> :
    BaseRecyclerChoiceAdapter<T, RecyclerSingleChoiceAdapter.ViewHolder>
        (ChoiceMode.cmSingle, android.R.layout.simple_list_item_single_choice) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val checkedTextView: CheckedTextView by lazy { view.findViewById<CheckedTextView>(android.R.id.text1) }
    }
}