package com.app.mscoremodels.services

import com.app.mscorebase.ui.dialogs.choicedialog.ChoiceItem

class ServiceDuration(duration: Int, val description: String): ChoiceItem<Int>(duration, description)