package com.app.mscorebase.ui.dialogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView


abstract class RecyclerSingleChoiceAdapter<T> constructor(@LayoutRes val layoutResID: Int):
    RecyclerView.Adapter<RecyclerSingleChoiceAdapter.SingleChoiceAdapterViewHolder>() {
    private val items = mutableListOf<T>()
    var checkedPosition = 0
        private set

    private lateinit var viewBinder: (view: View, position: Int) -> Unit

    constructor(): this(0)

    fun setItems(newItems: List<T>){
        this.items.clear()
        this.items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun setViewBinder(viewBinder: (view: View, position: Int) -> Unit){
        this.viewBinder = viewBinder
    }

    override fun getItemCount() = items.size

    fun getItem(position: Int): T {
        return items[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChoiceAdapterViewHolder {
        val layoutId =  if (layoutResID == 0) android.R.layout.simple_list_item_single_choice  else layoutResID
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return SingleChoiceAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: SingleChoiceAdapterViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            if (checkedPosition != position) {
                val oldPosition = checkedPosition
                checkedPosition = position
                notifyItemChanged(oldPosition)
                notifyItemChanged(position)
            }
        }
        if (::viewBinder.isInitialized) {
            viewBinder(holder.itemView, position)
        }
    }

    class SingleChoiceAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view)
}