package com.app.feature_services.models

import com.app.mscoremodels.saloon.SaloonService

interface ServicesAdapter {
    fun setItems(items: List<SaloonService>)
    fun setOnServiceClickListener(onServiceClick: (service: SaloonService)->Unit)
    fun setOnServiceDeleteListener(onDeleteService: (service: SaloonService)->Unit)
}