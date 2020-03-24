package com.app.mscoremodels.saloon

import javax.inject.Inject

class SaloonFactory @Inject constructor() {
    fun createSaloonService(owner: String, id: String, name: String, price: Double, duration: Int,
                            description: String, imageUrl: String = ""): SaloonService {
        return SaloonService(owner, id, name, price, duration, description, imageUrl)
    }
}