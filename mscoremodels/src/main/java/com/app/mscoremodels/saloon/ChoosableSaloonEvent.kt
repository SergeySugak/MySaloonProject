package com.app.mscoremodels.saloon

import android.content.Context
import com.app.mscorebase.ui.dialogs.choicedialog.ChoiceItem
import com.app.mscoremodels.R
import java.text.SimpleDateFormat
import java.util.*

private fun getEventDescription(context: Context, event: SaloonEvent): String {
    val formatter = SimpleDateFormat(context.getString(R.string.str_def_date_format), Locale.getDefault())
    return "${formatter.format(event.whenStart.time)}: ${event.master.name},  ${event.description}"
}

data class ChoosableSaloonEvent(val context: Context, val event: SaloonEvent) :
    ChoiceItem<String>(event.id, getEventDescription(context, event)) {
    override fun toString(): String {
        return event.toString()
    }
}