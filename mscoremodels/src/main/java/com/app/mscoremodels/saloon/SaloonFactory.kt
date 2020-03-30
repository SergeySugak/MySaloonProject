package com.app.mscoremodels.saloon

import javax.inject.Inject

class SaloonFactory @Inject constructor() {
    fun createSaloonService(owner: String, id: String, name: String, price: Double,
                            duration: ServiceDuration?, description: String,
                            imageUrl: String = ""): SaloonService {
        return SaloonService(owner, id, name, price, duration, description, imageUrl)
    }

    fun createServiceDuration(duration: Int?, name: String?) = ServiceDuration(duration, name)
    fun createChoosableServiceDuration(duration: Int?, name: String?) = ChoosableServiceDuration(duration, name)
    fun createServiceDuration(duration: ChoosableServiceDuration?) = ServiceDuration(duration?.duration, duration?.description)
    fun createChoosableServiceDuration(duration: ServiceDuration?) = ChoosableServiceDuration(duration?.duration, duration?.description)
}