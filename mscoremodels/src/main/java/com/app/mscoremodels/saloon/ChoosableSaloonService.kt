package com.app.mscoremodels.saloon

import com.app.mscorebase.ui.dialogs.choicedialog.ChoiceItem

data class ChoosableSaloonService(val service: SaloonService) :
    ChoiceItem<String>(service.id, service.name) {
    override fun toString(): String {
        return service.toString()
    }
}