package com.app.mscoremodels.saloon

import com.app.mscorebase.ui.dialogs.choicedialog.ChoiceItem

data class ChoosableSaloonMaster(val master: SaloonMaster) :
    ChoiceItem<String>(master.id, master.name) {
    override fun toString(): String {
        return master.toString()
    }
}