package com.app.mscoremodels.saloon

import javax.inject.Inject

class SaloonFactory @Inject constructor() {
    fun createSaloonService(id: String, name: String, price: Double,
                            duration: ServiceDuration?, description: String,
                            imageUrl: String = "") =
        SaloonService(id, name, price, duration, description, imageUrl)

    fun createSaloonMaster(id: String, name: String, description: String,
                           portfolioUrl: String = "", imageUrl: String = "") =
        SaloonMaster(id, name, description, portfolioUrl, imageUrl)

    fun createServiceDuration(duration: Int?, name: String?) = ServiceDuration(duration, name)
    fun createServiceDuration(duration: ChoosableServiceDuration?) =
        ServiceDuration(duration?.serviceDuration?.duration, duration?.serviceDuration?.description)
    fun createChoosableServiceDuration(duration: ServiceDuration?) = ChoosableServiceDuration(duration)

    fun createChoosableServiceDurations(serviceDurations: List<ServiceDuration>): List<ChoosableServiceDuration> {
        val result = mutableListOf<ChoosableServiceDuration>()
        serviceDurations.mapTo(result){ serviceDuration ->
            ChoosableServiceDuration(serviceDuration)
        }
        return result
    }

    fun createChoosableServices(allServicesList: List<SaloonService>,
                                masterServicesList: List<SaloonService>): List<ChoosableSaloonService> {
        val result = mutableListOf<ChoosableSaloonService>()
            allServicesList.mapTo(result) { saloonService ->
                ChoosableSaloonService(saloonService).apply {
                    isSelected = masterServicesList.indexOf(saloonService) != 1
                }
            }
        return result
    }
}