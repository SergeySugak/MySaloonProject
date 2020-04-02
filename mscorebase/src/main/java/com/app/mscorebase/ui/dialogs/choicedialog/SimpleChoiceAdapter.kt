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

open class SimpleChoiceAdapter<C : ChoiceItem<out Serializable>>(val choiceMode: ChoiceMode) :
    BaseAdapter() {
    private var choices: List<C> = ArrayList()
    private val enabledTypedValue: TypedValue? = TypedValue()
    private val disabledTypedValue: TypedValue? = TypedValue()

    fun setChoices(choiceItems: List<C>) {
        choices = choiceItems
        notifyDataSetChanged()
    }

    fun getChoices(): List<C> = Collections.unmodifiableList(choices)

    override fun getCount(): Int {
        return choices.size
    }

    override fun getItem(position: Int): C? {
        return choices[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun isEnabled(position: Int): Boolean {
        return choices[position].isEnabled
    }

    override fun getView(position: Int, outerView: View?, parent: ViewGroup): View {
        var convertView = outerView
        val viewHolder: ItemViewHolder
        if (convertView == null) {
            viewHolder = ItemViewHolder()
            val layoutResId = if (choiceMode === ChoiceMode.cmSingle) {
                R.layout.item_generic_single_choice
            } else {
                R.layout.item_generic_multi_choice
            }
            convertView = LayoutInflater.from(parent.context)
                .inflate(layoutResId, parent, false)
            viewHolder.checkedTextView =
                convertView.findViewById(R.id.choice_item)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ItemViewHolder
        }
        if (enabledTypedValue == null) {
            val attrResId = if (choiceMode === ChoiceMode.cmSingle) {
                android.R.attr.listChoiceIndicatorSingle
            } else {
                android.R.attr.listChoiceIndicatorMultiple
            }
            convertView!!.context.theme.resolveAttribute(
                attrResId,
                enabledTypedValue, true
            )
        }
        if (disabledTypedValue == null) {
            convertView!!.context.theme.resolveAttribute(
                android.R.attr.listChoiceBackgroundIndicator,
                disabledTypedValue, true
            )
        }
        viewHolder.checkedTextView!!.text = choices[position].name
        viewHolder.checkedTextView!!.isChecked = choices[position].isSelected
        viewHolder.checkedTextView!!.isEnabled = isEnabled(position)
        val checkMark = if (isEnabled(position)) {
            enabledTypedValue!!.resourceId
        } else {
            disabledTypedValue!!.resourceId
        }
        if (checkMark > 0) {
            viewHolder.checkedTextView!!.setCheckMarkDrawable(checkMark)
        }
        viewHolder.checkedTextView!!.visibility =
            if (choices[position].isVisible) View.VISIBLE else View.GONE

        convertView!!.visibility = viewHolder.checkedTextView!!.visibility
        return convertView
    }

    private class ItemViewHolder {
        var checkedTextView: CheckedTextView? = null
    }
}