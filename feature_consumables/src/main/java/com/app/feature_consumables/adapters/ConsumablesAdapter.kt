package com.app.feature_consumables.adapters

import com.app.mscoremodels.saloon.SaloonConsumable

interface ConsumablesAdapter {
    fun setItems(items: List<SaloonConsumable>)
    fun setOnConsumableClickListener(onConsumableClick: (consumable: SaloonConsumable) -> Unit)
    fun setOnConsumableDeleteListener(onDeleteConsumable: (consumable: SaloonConsumable) -> Unit)
}