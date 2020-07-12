package com.app.feature_select_consumables.adapters

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
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

    var onQtyChanged: ((position: Int) -> Unit)? = null

    override fun getView(position: Int, outerView: View?, parent: ViewGroup): View {
        var convertView = outerView
        val viewHolder: ItemViewHolder
        if (convertView == null) {
            viewHolder = ItemViewHolder()
            val layoutResId = R.layout.item_consumables_item
            convertView = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
            viewHolder.name = convertView.findViewById(R.id.choice_item)
            viewHolder.qty = convertView.findViewById(R.id.qty)
            viewHolder.uom = convertView.findViewById(R.id.uom)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ItemViewHolder
        }
        viewHolder.position = position
        viewHolder.qty!!.removeTextChangedListener(viewHolder.qtyChangeListener)
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
        viewHolder.name!!.text = getChoices()[position].name
        viewHolder.name!!.isEnabled = isEnabled(position)
        viewHolder.qty!!.isEnabled = isEnabled(position)
        viewHolder.uom!!.isEnabled = isEnabled(position)
        if (getChoices()[position].saloonConsumable.qty > 0) {
            viewHolder.qty!!.setText(getChoices()[position].saloonConsumable.qty.toString())
        } else {
            viewHolder.qty!!.text = null
        }
        viewHolder.uom!!.text = getChoices()[position].saloonConsumable.uom
        val visibility = if (getChoices()[position].isVisible) View.VISIBLE else View.GONE
        viewHolder.name!!.visibility = visibility
        viewHolder.qty!!.visibility = visibility
        viewHolder.uom!!.visibility = visibility
        if (visibility == View.VISIBLE){
            viewHolder.qtyChangeListener = object: TextWatcher{
                override fun afterTextChanged(s: Editable?) {
                    if (position == viewHolder.position) {
                        getChoices()[position].saloonConsumable.qty = if (!TextUtils.isEmpty(s.toString())) s.toString().toInt() else 0
                        getChoices()[position].isSelected = getChoices()[position].saloonConsumable.qty > 0
                        onQtyChanged?.let { it(position) }
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }
            viewHolder.qty!!.addTextChangedListener(viewHolder.qtyChangeListener)
        }
        else {
            viewHolder.name!!.setOnClickListener(null)
        }
        convertView!!.visibility = viewHolder.name!!.visibility
        return convertView
    }

    private class ItemViewHolder {
        var position: Int = 0
        var name: TextView? = null
        var qty: EditText? = null
        var uom: TextView? = null
        var qtyChangeListener: TextWatcher? = null
    }
}