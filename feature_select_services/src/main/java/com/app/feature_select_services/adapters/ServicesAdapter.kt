package com.app.feature_select_services.adapters

import com.app.mscorebase.ui.dialogs.choicedialog.ChoiceMode
import com.app.mscorebase.ui.dialogs.choicedialog.SimpleChoiceAdapter
import com.app.mscoremodels.saloon.ChoosableSaloonService
import javax.inject.Inject

class ServicesAdapter @Inject constructor(): SimpleChoiceAdapter<ChoosableSaloonService>(ChoiceMode.cmMulti)