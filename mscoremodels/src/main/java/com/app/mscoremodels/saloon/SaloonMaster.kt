package com.app.mscoremodels.saloon

import com.google.firebase.database.IgnoreExtraProperties

@Suppress("ConvertSecondaryConstructorToPrimary")
@IgnoreExtraProperties
class SaloonMaster constructor() {
    var id: String = ""
    var name: String = ""
    var description: String = ""
    var portfolioUrl: String = ""
    var imageUrl: String = ""

    constructor(id: String, name: String, description: String,
                portfolioUrl: String = "", imageUrl: String = ""): this () {
        this.id = id
        this.name = name
        this.description = description
        this.portfolioUrl = portfolioUrl
        this.imageUrl = imageUrl
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SaloonMaster

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (portfolioUrl != other.portfolioUrl) return false
        if (imageUrl != other.imageUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + portfolioUrl.hashCode()
        result = 31 * result + imageUrl.hashCode()
        return result
    }
}