package com.app.feature_select_consumables.adapters

import com.app.mscorebase.ui.dialogs.choicedialog.ChoiceMode
import com.app.mscorebase.ui.dialogs.choicedialog.SimpleChoiceAdapter
import com.app.mscoremodels.saloon.ChoosableSaloonConsumable
import com.app.mscoremodels.saloon.ChoosableSaloonService
import javax.inject.Inject

class ConsumablesAdapter @Inject constructor() :
    SimpleChoiceAdapter<ChoosableSaloonConsumable>(ChoiceMode.cmMulti)