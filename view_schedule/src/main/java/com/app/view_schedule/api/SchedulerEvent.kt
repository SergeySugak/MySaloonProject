package com.app.view_schedule.api

import android.graphics.Canvas
import android.graphics.RectF
import android.os.Parcelable
import java.util.*

interface SchedulerEvent: Parcelable {
    var id: String
    var dateTime: Calendar
    var duration: Int
    var header: String
    var text: String
}