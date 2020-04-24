package com.app.view_schedule.api

import java.time.LocalDateTime

interface Plannable<M, E> {
    val id: String
    val who: M
    val what: E
    val `when`: LocalDateTime
    val description: String
    fun plan()
}