package com.app.feature_select_consumables.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.EditText
import android.widget.TextView
import com.app.feature_select_consumables.R
import com.app.mscorebase.ui.dialogs.choicedialog.ChoiceMode
import com.app.mscorebase.ui.dialogs.choicedialog.SimpleChoiceAdapter
import com.app.mscoremodels.saloon.ChoosableSaloonConsumable
import javax.inject.Inject

class ConsumablesAdapter @Inject constructor() :
    SimpleChoiceAdapter<ChoosableSaloonConsumable>(ChoiceMode.cmMulti){

    override fun getView(position: Int, outerView: View?, parent: ViewGroup): View {
        var convertView = outerView
        val viewHolder: ItemViewHolder
        if (convertView == null) {
            viewHolder = ItemViewHolder()
            val layoutResId = R.layout.item_consumables_multi_choice_item
            convertView = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
            viewHolder.checkedTextView = convertView.findViewById(R.id.choice_item)
            viewHolder.qty = convertView.findViewById(R.id.qty)
            viewHolder.uom = convertView.findViewById(R.id.uom)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ItemViewHolder
        }
        if (enabledTypedValue == null) {
            val attrResId = android.R.attr.listChoiceIndicatorMultiple
            convertView!!.context.theme.resolveAttribute(attrResId, enabledTypedValue, true)
        }
        if (disabledTypedValue == null) {
            convertView!!.context.theme.resolveAttribute(
                android.R.attr.listChoiceBackgroundIndicator,
                disabledTypedValue, true
            )
        }
        viewHolder.checkedTextView!!.text = getChoices()[position].name
        viewHolder.checkedTextView!!.isChecked = getChoices()[position].isSelected
        viewHolder.checkedTextView!!.isEnabled = isEnabled(position)
        viewHolder.qty!!.setText(getChoices()[position].saloonConsumable.qty.toInt().toString())
        viewHolder.qty!!.isEnabled = isEnabled(position)
        viewHolder.uom!!.text = getChoices()[position].saloonConsumable.uom
        val checkMark = if (isEnabled(position)) {
            enabledTypedValue!!.resourceId
        } else {
            disabledTypedValue!!.resourceId
        }
        if (checkMark > 0) {
            viewHolder.checkedTextView!!.setCheckMarkDrawable(checkMark)
        }
        val visibility = if (getChoices()[position].isVisible) View.VISIBLE else View.GONE
        viewHolder.checkedTextView!!.visibility = visibility
        if (visibility == View.VISIBLE){
            viewHolder.checkedTextView!!.setOnClickListener{
                viewHolder.checkedTextView!!.isChecked = !viewHolder.checkedTextView!!.isChecked
                getChoices()[position].isSelected = viewHolder.checkedTextView!!.isChecked
            }
        }
        else {
            viewHolder.checkedTextView!!.setOnClickListener(null)
        }
        viewHolder.qty!!.visibility = visibility
        viewHolder.uom!!.visibility = visibility

        convertView!!.visibility = viewHolder.checkedTextView!!.visibility
        return convertView
    }

    private class ItemViewHolder {
        var checkedTextView: CheckedTextView? = null
        var qty: EditText? = null
        var uom: TextView? = null
    }
}