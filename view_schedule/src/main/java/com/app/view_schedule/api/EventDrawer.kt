package com.app.view_schedule.api

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

interface EventDrawer {
    fun draw(event: SchedulerEvent, canvas: Canvas, paint: Paint, rect: RectF)
}