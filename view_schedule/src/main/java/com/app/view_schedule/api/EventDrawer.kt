package com.app.view_schedule.api

import android.graphics.Canvas
import android.graphics.RectF

interface EventDrawer {
    fun draw(event: SchedulerEvent, canvas: Canvas, rect: RectF)
}