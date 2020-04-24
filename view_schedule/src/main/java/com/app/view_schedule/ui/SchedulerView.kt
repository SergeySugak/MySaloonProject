package com.app.view_schedule.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import com.app.view_schedule.R
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class SchedulerView(context: Context, attrs: AttributeSet?, defStyleAttr: Int):
    View(context, attrs, defStyleAttr) {

    var fitDays: Int = 5
    var fitHours: Int = 8
    var minHour: Int = 9
    var maxHour: Int = 18
    var hourFraction: HourFraction = HourFraction.hf1
    @ColorInt
    var colorMon: Int = Color.WHITE
    @ColorInt
    var colorTue: Int = Color.LTGRAY
    @ColorInt
    var colorWed: Int = Color.WHITE
    @ColorInt
    var colorThu: Int = Color.LTGRAY
    @ColorInt
    var colorFri: Int = Color.WHITE
    @ColorInt
    var colorSat: Int = 0xAAFFEBEE.toInt() //Kotlin Bug https://youtrack.jetbrains.com/issue/KT-2780
    @ColorInt
    var colorSun: Int = 0xAAFFEBEE.toInt() //Kotlin Bug https://youtrack.jetbrains.com/issue/KT-2780
    @ColorInt
    var daySepColor: Int = Color.GRAY
    @ColorInt
    var hourSepColor: Int = Color.GRAY
    var hoursHeaderWidth: Int = 40
    var daysHeaderHeight: Int = 40
    var daySepWidth: Int = 1
    var hourSepWidth: Int = 1
    var dateFormater = SimpleDateFormat(context.getString(R.string.str_def_date_format), Locale.getDefault())
    var startingDate = Calendar.getInstance()

    val paint = Paint()

    constructor(context: Context): this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet): this(context, attrs, 0){
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SchedulerView)
        minHour = attributes.getInt(R.styleable.SchedulerView_minHour, minHour)
        maxHour = attributes.getInt(R.styleable.SchedulerView_maxHour, maxHour)
        if (minHour >= maxHour){
            throw IllegalArgumentException("Can't have max hour less or equal to min hour!")
        }
        fitDays = attributes.getInt(R.styleable.SchedulerView_fitDays, fitDays)
        fitDays = fitDays.coerceAtMost(maxHour - minHour)
        fitHours = attributes.getInt(R.styleable.SchedulerView_fitHours, fitHours)
        colorMon = attributes.getColor(R.styleable.SchedulerView_colorMon, colorMon)
        colorTue = attributes.getColor(R.styleable.SchedulerView_colorTue, colorTue)
        colorWed = attributes.getColor(R.styleable.SchedulerView_colorWed, colorWed)
        colorWed = attributes.getColor(R.styleable.SchedulerView_colorWed, colorWed)
        colorThu = attributes.getColor(R.styleable.SchedulerView_colorThu, colorThu)
        colorFri = attributes.getColor(R.styleable.SchedulerView_colorFri, colorFri)
        colorSat = attributes.getColor(R.styleable.SchedulerView_colorSat, colorSat)
        colorSun = attributes.getColor(R.styleable.SchedulerView_colorSun, colorSun)
        daySepColor = attributes.getColor(R.styleable.SchedulerView_daySepColor, daySepColor)
        hourSepColor = attributes.getColor(R.styleable.SchedulerView_hourSepColor, hourSepColor)
        hourFraction = HourFraction.fromInt(attributes.getInt(R.styleable.SchedulerView_hourFraction, 1))
        daySepWidth = attributes.getDimensionPixelSize(R.styleable.SchedulerView_daySepWidth, daySepWidth)
        hourSepWidth = attributes.getDimensionPixelSize(R.styleable.SchedulerView_hourSepWidth, hourSepWidth)
        daysHeaderHeight = attributes.getDimensionPixelSize(R.styleable.SchedulerView_daysHeaderHeight, daysHeaderHeight)
        hoursHeaderWidth = attributes.getDimensionPixelSize(R.styleable.SchedulerView_hoursHeaderWidth, hoursHeaderWidth)
        val userDateFormat = attributes.getString(R.styleable.SchedulerView_dateFormat)
        userDateFormat?.let{
            dateFormater.applyPattern(it)
        }
        val userStaringDate = attributes.getString(R.styleable.SchedulerView_startingDate)
        userStaringDate?.let{
            val date = dateFormater.parse(it)
            date?.let {
                startingDate.time = it
            }
        }

        attributes.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //общие переменные для заголовков и ячеек
        val topPadding = paddingTop.toFloat()
        val bottomPadding = paddingBottom.toFloat()
        val leftPadding = paddingLeft.toFloat()
        val rightPadding = paddingRight.toFloat()
        var startingOffset: Float

        //переменные для рисования ячеек
        val top = topPadding + daysHeaderHeight + hourSepWidth
        val bottom = height - bottomPadding
        val left = leftPadding + hoursHeaderWidth + daySepWidth
        val right = width - rightPadding
        val viewPortWidth = right - left + 1
        val viewPortHeight = bottom - top + 1

        //определяем сколько места у нас есть для отрисовки всех разделителей и ячеек

        //нам сказали уместить fitDays дней по ширине
        //с учетом разделитей, приклеивающихся справа
        //вычисляем ширину дня
        val cellWidth = (viewPortWidth - (daySepWidth * fitDays)) / fitDays
        startingOffset = left
        //рисуем вертикальные прямоугольники для отображения дней и вертикальные же разделители
        val drawingDate = Calendar.getInstance()
        drawingDate.time = startingDate.time
        for (i in 0 until fitDays){
            if (i > 0) {
                drawingDate.add(Calendar.DATE, 1)
            }
            paint.color = dateToColor(drawingDate)
            canvas.drawRect(startingOffset, top, startingOffset + cellWidth, bottom, paint)
            startingOffset += cellWidth
            paint.color = daySepColor
            paint.strokeWidth = daySepWidth.toFloat()
            canvas.drawLine(startingOffset, topPadding, startingOffset, bottom, paint)
            startingOffset += daySepWidth
        }

        //нам сказали уместить fitHours часов по высоте
        //с учетом разделитей, приклеивающихся снизу
        //вычисляем высоту дня
        val cellHeight = viewPortHeight / fitHours - (hourSepWidth * fitHours)
        startingOffset = top
        //рисуем горизотнальные линии для отображения разделителей часов
        for (i in 1 until fitHours){

        }

        //рисуем вертикальный заголовок для часов
        paint.color = hourSepColor
        paint.strokeWidth = hourSepWidth.toFloat()
        startingOffset = leftPadding + hoursHeaderWidth
        canvas.drawLine(startingOffset, topPadding,
                        startingOffset, height - bottomPadding, paint)

        //рисуем горизонтальный заголовок для дней
        paint.color = daySepColor
        paint.strokeWidth = daySepWidth.toFloat()
        startingOffset = topPadding + daysHeaderHeight
        canvas.drawLine(leftPadding, startingOffset,
                        width - rightPadding, startingOffset, paint)
    }

    @ColorInt
    private fun dateToColor(date: Calendar): Int {
        return when (date.get(Calendar.DAY_OF_WEEK)){
            Calendar.MONDAY -> colorMon
            Calendar.TUESDAY -> colorTue
            Calendar.WEDNESDAY -> colorWed
            Calendar.THURSDAY -> colorThu
            Calendar.FRIDAY -> colorFri
            Calendar.SATURDAY -> colorSat
            Calendar.SUNDAY -> colorSun
            else -> throw IllegalArgumentException("Unknown day of week")
        }
    }

    fun scrollToDate(date: LocalDate){

    }

    fun scrollToHour(hour: Int){

    }

    enum class HourFraction(val value: Int) {
        hf1(1), hf5(10), hf10(6), hf15(4), hf20(3), hf30(2);

        companion object {
            fun fromInt(value: Int): HourFraction {
                return when (value) {
                    5 -> hf5
                    10 -> hf10
                    15 -> hf15
                    20 -> hf20
                    30 -> hf30
                    else -> hf1
                }
            }
        }
    }
}