package com.app.mscoremodels.saloon

import com.app.mscorebase.ui.dialogs.choicedialog.ChoiceItem

data class ChoosableSaloonConsumable(val saloonConsumable: SaloonConsumable):
    ChoiceItem<String>(saloonConsumable.id, saloonConsumable.name)