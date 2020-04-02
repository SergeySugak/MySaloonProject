package com.app.mscoremodels.saloon

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class ServiceDuration(
    val duration: Int?,
    val description: String?) {
    constructor(): this(null, null)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServiceDuration

        if (duration != other.duration) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = duration ?: 0
        result = 31 * result + (description?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return description ?: "${ServiceDuration::class.java.simpleName} = $duration"
    }
}