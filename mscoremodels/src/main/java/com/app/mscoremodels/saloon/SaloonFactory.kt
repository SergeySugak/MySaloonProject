package com.app.mscoremodels.saloon

import javax.inject.Inject

class SaloonFactory @Inject constructor() {
    fun createSaloonService(name: String, price: Double, duration: Int,
                            description: String, imageUrl: String = ""): SaloonService {
        return SaloonService(name, price, duration, description, imageUrl)
    }

}