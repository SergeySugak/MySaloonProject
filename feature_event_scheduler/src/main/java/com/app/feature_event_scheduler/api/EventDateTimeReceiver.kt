package com.app.feature_event_scheduler.api

interface EventDateTimeReceiver {
    fun setEventDate(year: Int, month: Int, day: Int)
    fun setEventTime(hour: Int, minute: Int)
}