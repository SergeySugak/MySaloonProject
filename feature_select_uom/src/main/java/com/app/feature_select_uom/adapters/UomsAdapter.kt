package com.app.feature_select_uom.adapters

import com.app.mscorebase.ui.dialogs.choicedialog.ChoiceMode
import com.app.mscorebase.ui.dialogs.choicedialog.SimpleChoiceAdapter
import com.app.mscoremodels.saloon.ChoosableSaloonService
import com.app.mscoremodels.saloon.ChoosableUom
import javax.inject.Inject

class UomsAdapter @Inject constructor() :
    SimpleChoiceAdapter<ChoosableUom>(ChoiceMode.cmSingle)