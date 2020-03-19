package com.app.mscoremodels.saloon

data class SaloonService (var name: String){
    var price: Double = 0.0
    var duration: Int = 0
    var description: String = ""
    var imageUrl: String = ""

    constructor(name: String, price: Double,
                duration: Int, description: String,
                imageUrl: String): this(name) {
        this.price = price
        this.duration = duration
        this.description = description
        this.imageUrl = imageUrl
    }
}