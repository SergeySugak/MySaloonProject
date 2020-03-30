package com.app.mscoremodels.saloon

import com.app.mscorebase.ui.dialogs.choicedialog.ChoiceItem

class ChoosableServiceDuration(val duration: Int?,
                               val description: String?): ChoiceItem<Int>(duration, description){
    constructor(): this(null, null)
}