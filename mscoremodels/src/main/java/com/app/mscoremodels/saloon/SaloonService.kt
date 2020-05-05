package com.app.mscoremodels.saloon

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class SaloonService constructor(){
    var name: String = ""
    var id: String = ""
    var price: Double = 0.0
    var duration: ServiceDuration? = null
    var description: String = ""
    var imageUrl: String = ""

    constructor(id: String, name: String, price: Double,
                duration: ServiceDuration?, description: String,
                imageUrl: String): this() {
        this.id = id
        this.name = name
        this.price = price
        this.duration = duration
        this.description = description
        this.imageUrl = imageUrl
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SaloonService

        if (name != other.name) return false
        if (id != other.id) return false
        if (price != other.price) return false
        if (duration != other.duration) return false
        if (description != other.description) return false
        if (imageUrl != other.imageUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + (duration?.hashCode() ?: 0)
        result = 31 * result + description.hashCode()
        result = 31 * result + imageUrl.hashCode()
        return result
    }

    override fun toString(): String {
        return name
    }


}