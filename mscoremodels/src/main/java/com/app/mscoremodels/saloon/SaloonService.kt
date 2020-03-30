package com.app.mscoremodels.saloon

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class SaloonService (val owner: String, var name: String){
    var id: String = ""
    var price: Double = 0.0
    var duration: ServiceDuration? = null
    var description: String = ""
    var imageUrl: String = ""

    constructor(): this("", "")

    constructor(owner: String, id: String, name: String, price: Double,
                duration: ServiceDuration?, description: String,
                imageUrl: String): this(owner, name) {
        this.id = id
        this.price = price
        this.duration = duration
        this.description = description
        this.imageUrl = imageUrl
    }
}