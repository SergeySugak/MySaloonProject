package com.app.mscorebase.ui.dialogs.choicedialog

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckedTextView
import com.app.mscorebase.R
import java.io.Serializable
import java.util.*

class SingleChoiceAdapter<C : ChoiceItem<out Serializable>> internal constructor() :
    BaseAdapter() {
    private var items: List<C> = ArrayList()
    private val enabledTypedValue: TypedValue? = TypedValue()
    private val disabledTypedValue: TypedValue? = TypedValue()
    fun setItems(choiceItems: List<C>) {
        items = Collections.unmodifiableList(choiceItems)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): C? {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun isEnabled(position: Int): Boolean {
        return items[position]!!.isEnabled
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var convertView = convertView
        val viewHolder: ItemViewHolder
        if (convertView == null) {
            viewHolder = ItemViewHolder()
            convertView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_generic_single_choice, parent, false)
            viewHolder.checkedTextView =
                convertView.findViewById(R.id.single_choice_item)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ItemViewHolder
        }
        if (enabledTypedValue == null) {
            convertView!!.context.theme.resolveAttribute(
                android.R.attr.listChoiceIndicatorSingle,
                enabledTypedValue, true
            )
        }
        if (disabledTypedValue == null) {
            convertView!!.context.theme.resolveAttribute(
                android.R.attr.listChoiceBackgroundIndicator,
                disabledTypedValue, true
            )
        }
        viewHolder.checkedTextView!!.text = items[position]!!.name
        viewHolder.checkedTextView!!.isEnabled = isEnabled(position)
        val checkMark: Int
        checkMark = if (isEnabled(position)) {
            enabledTypedValue!!.resourceId
        } else {
            disabledTypedValue!!.resourceId
        }
        if (checkMark > 0) {
            viewHolder.checkedTextView!!.setCheckMarkDrawable(checkMark)
        }
        viewHolder.checkedTextView!!.visibility =
            if (items[position]!!.isVisible) View.VISIBLE else View.GONE
        convertView!!.visibility = viewHolder.checkedTextView!!.visibility
        return convertView
    }

    private class ItemViewHolder {
        var checkedTextView: CheckedTextView? = null
    }
}