package com.app.feature_masters.adapters

import com.app.mscoremodels.saloon.SaloonMaster

interface MastersAdapter {
    fun setItems(items: List<SaloonMaster>)
    fun setOnMasterClickListener(onMasterClick: (master: SaloonMaster) -> Unit)
    fun setOnMasterDeleteListener(onDeleteMaster: (master: SaloonMaster) -> Unit)
}