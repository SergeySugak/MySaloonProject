package com.app.feature_master.models

import com.app.mscorebase.ui.dialogs.choicedialog.ChoiceMode
import com.app.mscorebase.ui.dialogs.choicedialog.SimpleChoiceAdapter
import com.app.mscoremodels.saloon.ChoosableSaloonService
import javax.inject.Inject

class MasterServicesAdapter @Inject constructor(): SimpleChoiceAdapter<ChoosableSaloonService>(ChoiceMode.cmMulti)