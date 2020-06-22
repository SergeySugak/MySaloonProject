package com.app.view_schedule.api

import java.util.*

interface SchedulerEvent {
    fun getEventId(): String
    fun getDateTimeStart(): Calendar
    fun getDateTimeFinish(): Calendar
    fun getHeader(): String
    fun getText(): String
    fun getEventColor(): Int
}