package com.app.mscorebase.ui.dialogs.choicedialog

import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable

//Стандартные layout
//android.R.layout.simple_list_item_single_choice
//android.R.layout.simple_list_item_multiple_choice
abstract class BaseRecyclerChoiceAdapter<T : ChoiceItem<out Serializable>,
        H : RecyclerView.ViewHolder>
    (
    val choiceMode: ChoiceMode,
    @LayoutRes val layoutResId: Int
) : RecyclerView.Adapter<H>() {

    private val items = mutableListOf<T>()
    var checkedPosition = 0
        private set

    fun setItems(newItems: List<T>) {
        this.items.clear()
        this.items.addAll(newItems)
        notifyDataSetChanged()
    }

    abstract fun viewBinder(view: View, position: Int)

    override fun getItemCount() = items.size

    fun getItem(position: Int): T {
        return items[position]
    }

    override fun onBindViewHolder(holder: H, position: Int) {
        holder.itemView.setOnClickListener {
            when (choiceMode) {
                ChoiceMode.cmSingle ->
                    if (checkedPosition != position) {
                        val oldPosition = checkedPosition
                        checkedPosition = position
                        items[oldPosition].isSelected = false
                        items[position].isSelected = true
                        notifyItemChanged(oldPosition)
                        notifyItemChanged(position)
                    }
                ChoiceMode.cmMulti -> {
                    items[position].isSelected = !items[position].isSelected
                    notifyItemChanged(position)
                }
            }
        }
        viewBinder(holder.itemView, position)
    }
}