package com.app.view_schedule.api

import java.util.*

interface SchedulerViewListener {
    fun onDateTimeRangeChanged(from: Calendar, to: Calendar)
}