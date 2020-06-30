package com.app.feature_service_duration.adapters

import com.app.mscorebase.ui.dialogs.choicedialog.ChoiceMode
import com.app.mscorebase.ui.dialogs.choicedialog.SimpleChoiceAdapter
import com.app.mscoremodels.saloon.ChoosableServiceDuration
import javax.inject.Inject

class ServiceDurationAdapter @Inject constructor() :
    SimpleChoiceAdapter<ChoosableServiceDuration>(ChoiceMode.cmSingle)