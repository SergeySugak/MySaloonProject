package com.app.view_schedule.api

import java.util.*

interface SchedulerEvent {

    val id: String
    val dateTimeStart: Calendar
    val dateTimeFinish: Calendar
    val header: String
    val text: String
    val color: Int
}