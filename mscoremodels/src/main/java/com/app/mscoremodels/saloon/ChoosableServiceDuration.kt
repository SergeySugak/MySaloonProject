package com.app.mscoremodels.saloon

import com.app.mscorebase.ui.dialogs.choicedialog.ChoiceItem

class ChoosableServiceDuration(val serviceDuration: ServiceDuration?) :
    ChoiceItem<Int>(serviceDuration?.duration, serviceDuration?.description) {
    constructor() : this(null)
}