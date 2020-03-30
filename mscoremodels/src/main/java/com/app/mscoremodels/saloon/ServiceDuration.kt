package com.app.mscoremodels.saloon

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
open class ServiceDuration(
    val duration: Int?,
    val description: String?) {
    constructor(): this(null, null)
}