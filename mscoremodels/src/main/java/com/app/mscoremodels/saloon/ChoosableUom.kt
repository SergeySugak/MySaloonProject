package com.app.mscoremodels.saloon

import com.app.mscorebase.ui.dialogs.choicedialog.ChoiceItem

class ChoosableUom(uom: String) :
    ChoiceItem<String>(uom, uom) {
    constructor() : this("")
}