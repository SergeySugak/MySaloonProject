package com.app.mscoremodels.saloon

data class SaloonService (val owner: String, var name: String){
    var id: String = ""
    var price: Double = 0.0
    var duration: Int = 0
    var description: String = ""
    var imageUrl: String = ""

    constructor(): this("", "")

    constructor(owner: String, id: String, name: String, price: Double,
                duration: Int, description: String,
                imageUrl: String): this(owner, name) {
        this.id = id
        this.price = price
        this.duration = duration
        this.description = description
        this.imageUrl = imageUrl
    }
}