package com.app.view_schedule.api

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.text.TextUtils
import java.util.*

class DefaultEventDrawer: EventDrawer {

    private val textPaint = TextPaint()

    override fun draw(context: Context,
                      event: SchedulerEvent, canvas: Canvas, paint: Paint, rect: RectF) {
        //Рисуем обертку
        paint.style = Paint.Style.FILL;
        paint.color = event.color
        canvas.drawRoundRect(rect, DEF_RADIUS, DEF_RADIUS, paint)

        paint.color = DEF_BORDER_COLOR
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = DEF_STROKE
        canvas.drawRoundRect(rect, DEF_RADIUS, DEF_RADIUS, paint)

        //Заголовок время нач. - время окон.
        prepareEventHeaderTextPaint()
        drawEventHeader(event, canvas, paint, rect)

        //Описание - заголовок события типа "Стрижка", "Окрашивание" и т. п.
        prepareEventTextPaint()
        drawEventText(event, canvas, rect)
    }

    private fun prepareEventHeaderTextPaint() {
        textPaint.color = DEF_HEADER_TEXT_COLOR
        textPaint.textSize = DEF_HEADER_TEXT_SIZE
        textPaint.typeface = DEF_HEADER_TEXT_TYPEFACE
    }

    private fun prepareEventTextPaint() {
        textPaint.color = DEF_TEXT_COLOR
        textPaint.textSize = DEF_TEXT_SIZE
        textPaint.typeface = DEF_TEXT_TYPEFACE
    }

    private fun getHeaderBottom(top: Float): Float {
        val textHeight = textPaint.descent() - textPaint.ascent()
        return top + 2 * DEF_HEADER_V_PADDING + textHeight
    }

    private fun drawEventHeader(event: SchedulerEvent, canvas: Canvas, paint: Paint, rect: RectF) {
        val textHeight = textPaint.descent() - textPaint.ascent()
        val textOffset = (textHeight / 2) - textPaint.descent()
        val bottom = getHeaderBottom(rect.top)
        val midY = (2 * DEF_HEADER_V_PADDING + textHeight) / 2 + textOffset
        canvas.drawLine(rect.left, bottom, rect.right, bottom, paint)
        val startTimeH = "${event.dateTimeStart.get(Calendar.HOUR_OF_DAY)}".padStart(2, '0')
        val startTimeM = "${event.dateTimeStart.get(Calendar.MINUTE)}".padStart(2, '0')
        val finishTimeH = "${event.dateTimeFinish.get(Calendar.HOUR_OF_DAY)}".padStart(2, '0')
        val finishTimeM = "${event.dateTimeFinish.get(Calendar.MINUTE)}".padStart(2, '0')

        canvas.drawText("$startTimeH:$startTimeM - $finishTimeH:$finishTimeM",
            rect.left + DEF_HEADER_H_PADDING, rect.top + midY, paint)
    }

    private fun drawEventText(event: SchedulerEvent, canvas: Canvas, rect: RectF) {
        val text = TextUtils.ellipsize(event.header, textPaint, rect.width() - 2 * DEF_TEXT_V_PADDING,
            TextUtils.TruncateAt.END).toString()
        val textHeight = textPaint.descent() - textPaint.ascent()
        val textOffset = (textHeight / 2) - textPaint.descent()
        val midY = (2 * DEF_TEXT_V_PADDING + textHeight) / 2 + textOffset
        val top = getHeaderBottom(rect.top)
        canvas.drawText(text, rect.left + DEF_TEXT_H_PADDING, top + midY, textPaint)
    }

    companion object {
        const val DEF_BORDER_COLOR = Color.BLACK
        const val DEF_STROKE = 1f
        const val DEF_RADIUS = 4f
        const val DEF_HEADER_TEXT_COLOR = Color.BLACK
        const val DEF_HEADER_TEXT_SIZE = 16f
        const val DEF_HEADER_H_PADDING = 10f
        const val DEF_HEADER_V_PADDING = 10f
        val DEF_HEADER_TEXT_TYPEFACE: Typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        const val DEF_TEXT_COLOR = Color.BLACK
        const val DEF_TEXT_SIZE = 16f
        const val DEF_TEXT_H_PADDING = 10f
        const val DEF_TEXT_V_PADDING = 10f
        val DEF_TEXT_TYPEFACE: Typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
}