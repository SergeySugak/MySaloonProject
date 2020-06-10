package com.app.feature_select_master.adapters

import com.app.mscorebase.ui.dialogs.choicedialog.ChoiceMode
import com.app.mscorebase.ui.dialogs.choicedialog.SimpleChoiceAdapter
import com.app.mscoremodels.saloon.ChoosableSaloonMaster
import javax.inject.Inject

class MastersAdapter @Inject constructor(): SimpleChoiceAdapter<ChoosableSaloonMaster>(ChoiceMode.cmSingle)