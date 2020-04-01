package com.app.mscoremodels.saloon

import com.app.mscorebase.ui.dialogs.choicedialog.ChoiceItem

data class ChoosableSaloonService (private val service: SaloonService):
    ChoiceItem<String>(service.id, service.name)