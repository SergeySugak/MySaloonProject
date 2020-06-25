package com.app.feature_select_event.adapters

import com.app.mscorebase.ui.dialogs.choicedialog.ChoiceMode
import com.app.mscorebase.ui.dialogs.choicedialog.SimpleChoiceAdapter
import com.app.mscoremodels.saloon.ChoosableSaloonEvent
import javax.inject.Inject

class EventsAdapter @Inject constructor() :
    SimpleChoiceAdapter<ChoosableSaloonEvent>(ChoiceMode.cmSingle)