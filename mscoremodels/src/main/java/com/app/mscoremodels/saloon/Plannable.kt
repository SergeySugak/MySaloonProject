package com.app.mscoremodels.saloon

import java.util.*

interface Plannable<M, E> {
    val id: String
    val who: M
    val what: E
    val dateTime: Calendar
    val description: String
    fun plan()
}