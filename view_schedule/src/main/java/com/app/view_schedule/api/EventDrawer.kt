package com.app.view_schedule.api

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

interface EventDrawer {
    fun draw(context: Context, event: SchedulerEvent, canvas: Canvas, paint: Paint, rect: RectF)
}