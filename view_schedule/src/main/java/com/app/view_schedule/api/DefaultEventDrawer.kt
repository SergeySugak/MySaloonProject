package com.app.view_schedule.api

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

class DefaultEventDrawer: EventDrawer {
    override fun draw(event: SchedulerEvent, canvas: Canvas, paint: Paint, rect: RectF) {
        paint.color = Color.BLACK
        paint.strokeWidth = 2f
        canvas.drawRoundRect(rect, 2f, 2f, paint)
    }
}