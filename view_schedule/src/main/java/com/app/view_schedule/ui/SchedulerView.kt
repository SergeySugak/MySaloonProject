package com.app.view_schedule.ui

import android.content.Context
import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import androidx.annotation.ColorInt
import com.app.view_schedule.R
import com.app.view_schedule.api.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

//View для отображения планировщика событий
//Для управления рисованием событий необходимо определить интерфейс EventDrawer
//и либо указать его в xml в атрибуте eventDrawer
//либо задать его программно вызовом setEventDrawer.
//Дефолтний рисователь событий - DefaultEventDrawer
//eventDrawer должен самостоятельно сохранять свое состяние при переворотах
//Для задания списка запланированных событий надо вызвать setEvents
//В него передать свои реализации SchedulerEvent
class SchedulerView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    init {
        isSaveEnabled = true
        if (id == NO_ID) {
            id = generateViewId()
        }
    }

    private fun onPropertyChanged() {
        if (!delayInvalidation) {
            postInvalidate()
        }
    }

    private fun onHourSepColor() {
        dashedPaint.color = hourSepColor
    }

    private fun onDateFormatChange() {
        dateFormatter.applyPattern(dateFormat)
        if (!delayInvalidation) {
            postInvalidate()
        }
    }

    private val today = Calendar.getInstance()
    var delayInvalidation = false
    var fitDays: Int by AfterChangeInvalidationInitiatorProperty(DEF_FIT_DAYS) { onPropertyChanged() }
    var fitHours: Int by AfterChangeInvalidationInitiatorProperty(DEF_FIT_HOURS) { onPropertyChanged() }
    var minHour: Int by AfterChangeInvalidationInitiatorProperty(DEF_MIN_HOUR) { onPropertyChanged() }
    var maxHour: Int by AfterChangeInvalidationInitiatorProperty(DEF_MAX_HOUR) { onPropertyChanged() }
    var hourFraction: HourFraction by AfterChangeInvalidationInitiatorProperty(
        HourFraction.fromInt(
            DEF_HOUR_FRACTION
        )
    ) { onPropertyChanged() }
    var colorMon: Int by AfterChangeInvalidationInitiatorProperty(DEF_COLOR_MON) { onPropertyChanged() }
    var colorTue: Int by AfterChangeInvalidationInitiatorProperty(DEF_COLOR_TUE) { onPropertyChanged() }
    var colorWed: Int by AfterChangeInvalidationInitiatorProperty(DEF_COLOR_WED) { onPropertyChanged() }
    var colorThu: Int by AfterChangeInvalidationInitiatorProperty(DEF_COLOR_THU) { onPropertyChanged() }
    var colorFri: Int by AfterChangeInvalidationInitiatorProperty(DEF_COLOR_FRI) { onPropertyChanged() }
    var colorSat: Int by AfterChangeInvalidationInitiatorProperty(DEF_COLOR_SAT) { onPropertyChanged() }
    var colorSun: Int by AfterChangeInvalidationInitiatorProperty(DEF_COLOR_SUN) { onPropertyChanged() }
    var daySepColor: Int by AfterChangeInvalidationInitiatorProperty(DEF_DAY_SEP_COLOR) { onPropertyChanged() }
    var hourSepColor: Int by BeforeChangeInvalidationInitiatorProperty(DEF_HOUR_SEP_COLOR) { onHourSepColor() }
    var hoursHeaderWidth: Int by AfterChangeInvalidationInitiatorProperty(DEF_HOURS_HEADER_WIDTH) { onPropertyChanged() }
    var daysHeaderHeight: Int by AfterChangeInvalidationInitiatorProperty(DEF_DAYS_HEADER_HEIGHT) { onPropertyChanged() }
    var daySepWidth: Int by AfterChangeInvalidationInitiatorProperty(DEF_DAY_SEP_WIDTH) { onPropertyChanged() }
    var hourSepWidth: Int by AfterChangeInvalidationInitiatorProperty(DEF_HOUR_SEP_WIDTH) { onPropertyChanged() }
    var daysHeaderTextSize: Int by AfterChangeInvalidationInitiatorProperty(
        DEF_DAYS_HEADER_TEXT_SIZE
    ) { onPropertyChanged() }
    var daysHeaderTextColor: Int by AfterChangeInvalidationInitiatorProperty(
        DEF_DAYS_HEADER_TEXT_COLOR
    ) { onPropertyChanged() }
    var hoursHeaderTextSize: Int by AfterChangeInvalidationInitiatorProperty(
        DEF_HOURS_HEADER_TEXT_SIZE
    ) { onPropertyChanged() }
    var hoursHeaderTextColor: Int by AfterChangeInvalidationInitiatorProperty(
        DEF_HOURS_HEADER_TEXT_COLOR
    ) { onPropertyChanged() }
    var eventMargin: Int by AfterChangeInvalidationInitiatorProperty(DEF_EVENT_MARGIN) { onPropertyChanged() }
    var dateFormat: String by AfterChangeInvalidationInitiatorProperty(context.getString(R.string.str_def_date_format)) { onDateFormatChange() }

    private var dateFormatter =
        SimpleDateFormat(context.getString(R.string.str_def_date_format), Locale.getDefault())
    var startingDate: Calendar = Calendar.getInstance()

    private val paint = Paint()
    private val textPaint = TextPaint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }
    private val path = Path()
    private val dashedPaint = Paint()
    var textRect = RectF()

    private var topPadding = 0f
    private var bottomPadding = 0f
    private var leftPadding = 0f
    private var rightPadding = 0f
    private var contentTop = 0f
    private var contentBottom = 0f
    private var contentHeight = 0f
    private var contentLeft = 0f
    private var contentRight = 0f
    private var contentWidth = 0f
    private var cellWidth = 0f
    private var cellHeight = 0f
    private var xScroll = 0f
    private var yScroll = 0f

    private val gestureListener = SchedulerViewGestureListener()
    private val gestureDetector = GestureDetector(context, gestureListener)
    private val scroller = Scroller(context, DecelerateInterpolator(DEF_DECELERATE_FACTOR))

    private val eventPaint = Paint()
    private var eventRect = RectF()
    private val events = mutableListOf<SchedulerEvent>()
    private val eventRects = mutableListOf<EventRect>()
    private lateinit var eventDrawer: EventDrawer
    private var viewListener: SchedulerViewListener? = null

    private var eventClickListener: SchedulerEventClickListener? = null

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SchedulerView)
        minHour = attributes.getInt(R.styleable.SchedulerView_minHour, minHour)
        maxHour = attributes.getInt(R.styleable.SchedulerView_maxHour, maxHour)
        if (minHour >= maxHour) {
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
        hourFraction = HourFraction.fromInt(
            attributes.getInt(
                R.styleable.SchedulerView_hourFraction,
                DEF_HOUR_FRACTION
            )
        )
        daySepWidth =
            attributes.getDimensionPixelSize(R.styleable.SchedulerView_daySepWidth, daySepWidth)
        hourSepWidth =
            attributes.getDimensionPixelSize(R.styleable.SchedulerView_hourSepWidth, hourSepWidth)
        daysHeaderHeight = attributes.getDimensionPixelSize(
            R.styleable.SchedulerView_daysHeaderHeight,
            daysHeaderHeight
        )
        hoursHeaderWidth = attributes.getDimensionPixelSize(
            R.styleable.SchedulerView_hoursHeaderWidth,
            hoursHeaderWidth
        )
        dateFormat = attributes.getString(R.styleable.SchedulerView_dateFormat)
            ?: context.getString(R.string.str_def_date_format)
        dateFormatter.applyPattern(dateFormat)
        val userStaringDate = attributes.getString(R.styleable.SchedulerView_startingDate)
        userStaringDate?.let {
            val date = dateFormatter.parse(it)
            date?.let { d ->
                startingDate.time = d
            }
        }
        daysHeaderTextSize = attributes.getDimensionPixelSize(
            R.styleable.SchedulerView_daysHeaderTextSize,
            daysHeaderTextSize
        )
        daysHeaderTextColor =
            attributes.getColor(R.styleable.SchedulerView_daysHeaderTextColor, daysHeaderTextColor)
        hoursHeaderTextSize = attributes.getDimensionPixelSize(
            R.styleable.SchedulerView_hoursHeaderTextSize,
            hoursHeaderTextSize
        )
        hoursHeaderTextColor = attributes.getColor(
            R.styleable.SchedulerView_hoursHeaderTextColor,
            hoursHeaderTextColor
        )

        val eventDrawerClassName = attributes.getString(R.styleable.SchedulerView_eventDrawer)
        eventDrawer = if (TextUtils.isEmpty(eventDrawerClassName)) {
            DefaultEventDrawer()
        } else {
            //Это будет порождать exception, если задать некорректное имя класса отрисовщика
            Class.forName(eventDrawerClassName!!).newInstance() as EventDrawer
        }
        eventMargin = attributes.getDimensionPixelSize(
            R.styleable.SchedulerView_hoursHeaderTextSize,
            eventMargin
        )

        yScroll = minHour.toFloat()
        attributes.recycle()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val state = SchedulerViewState(superState)
        state.fitDays = this.fitDays
        state.fitHours = this.fitHours
        state.minHour = this.minHour
        state.maxHour = this.maxHour
        state.colorMon = this.colorMon
        state.colorTue = this.colorTue
        state.colorWed = this.colorWed
        state.colorThu = this.colorThu
        state.colorFri = this.colorFri
        state.colorSat = this.colorSat
        state.colorSun = this.colorSun
        state.daySepColor = this.daySepColor
        state.hourSepColor = this.hourSepColor
        state.hoursHeaderWidth = this.hoursHeaderWidth
        state.daysHeaderHeight = this.daysHeaderHeight
        state.daySepWidth = this.daySepWidth
        state.hourSepWidth = this.hourSepWidth
        state.daysHeaderTextSize = this.daysHeaderTextSize
        state.daysHeaderTextColor = this.daysHeaderTextColor
        state.hoursHeaderTextSize = this.hoursHeaderTextSize
        state.hoursHeaderTextColor = this.hoursHeaderTextColor
        state.eventMargin = this.eventMargin
        state.xScroll = this.xScroll
        state.yScroll = this.yScroll
        state.startingDate = this.startingDate
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as SchedulerViewState
        super.onRestoreInstanceState(savedState.superState)
        fitDays = savedState.fitDays
        fitHours = savedState.fitHours
        minHour = savedState.minHour
        maxHour = savedState.maxHour
        colorMon = savedState.colorMon
        colorTue = savedState.colorTue
        colorWed = savedState.colorWed
        colorThu = savedState.colorThu
        colorFri = savedState.colorFri
        colorSat = savedState.colorSat
        colorSun = savedState.colorSun
        daySepColor = savedState.daySepColor
        hourSepColor = savedState.hourSepColor
        hoursHeaderWidth = savedState.hoursHeaderWidth
        daysHeaderHeight = savedState.daysHeaderHeight
        daySepWidth = savedState.daySepWidth
        hourSepWidth = savedState.hourSepWidth
        daysHeaderTextSize = savedState.daysHeaderTextSize
        daysHeaderTextColor = savedState.daysHeaderTextColor
        hoursHeaderTextSize = savedState.hoursHeaderTextSize
        hoursHeaderTextColor = savedState.hoursHeaderTextColor
        eventMargin = savedState.eventMargin
        xScroll = savedState.xScroll
        yScroll = savedState.yScroll
        startingDate = savedState.startingDate
    }

//  С onMeasure всё как-то очень странно.
//  1. Студия перестает рисовать (вероятно не понимает кто parent view)
//  2. Как быть, если заданы заведомо кривые размеры (см. п. 3)
//  3. При перевороте перестает ресовать вообще. Видимо, иэто логисно, onMeasure случается,
//     когда размеры родительского view еще не известны. Но тогда на что ориентироваться,
//     чтобы исправить заданные криво размеры?
//  Итого: оставляем поведение View по умолчанию, т.к. оно вообще-то полностью устраивает.
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val suggestedW = getSuggestedMaxWidth()
//        val suggestedH = getSuggestedMaxHeight()
//        var w = getDefaultSize(suggestedW, widthMeasureSpec)
//        var h = getDefaultSize(suggestedH, heightMeasureSpec)
//        if (w > suggestedW) {
//            w = suggestedW
//        }
//        if (h > suggestedH) {
//            h = suggestedH
//        }
//        setMeasuredDimension(w, h)
//    }
//
//    private fun getSuggestedMaxWidth() = (parent as View).width
//
//    private fun getSuggestedMaxHeight() = (parent as View).height

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            topPadding = paddingTop.toFloat()
            bottomPadding = paddingBottom.toFloat()
            leftPadding = paddingLeft.toFloat()
            rightPadding = paddingRight.toFloat()

            //нам сказали уместить fitHours часов по высоте
            //вычисляем высоту часа с учетом разделитей, приклеивающихся снизу
            contentTop = topPadding + daysHeaderHeight + hourSepWidth
            contentBottom = height - bottomPadding
            contentHeight = contentBottom - contentTop + 1
            cellHeight = (contentHeight - (hourSepWidth * fitHours)) / fitHours

            //нам сказали уместить fitDays дней по ширине
            //вычисляем ширину дня с учетом разделитей, приклеивающихся справа
            contentLeft = leftPadding + hoursHeaderWidth + daySepWidth
            contentRight = width - rightPadding
            contentWidth = contentRight - contentLeft + 1
            cellWidth = (contentWidth - (daySepWidth * fitDays)) / fitDays
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawDays(canvas)
        drawHours(canvas)
        drawContent(canvas)
    }

    fun getFirstDrawableDateTime(): Calendar {
        val result = today.clone() as Calendar
        with(result) {
            time = startingDate.time
            add(DATE, -ceil(xScroll).toInt())
            set(HOUR_OF_DAY, floor(yScroll).toInt())
            val minutes = floor(60 * (yScroll - floor(yScroll))).toInt()
            set(MINUTE, minutes)
            set(SECOND, 0)
            set(MILLISECOND, 0)
        }
        return result
    }

    fun getLastDrawableDateTime(firstDrawableDate: Calendar): Calendar {
        val result = firstDrawableDate.clone() as Calendar
        with(result) {
            add(DATE, fitDays) //Первый и последний день могут быть видны частично
            add(MINUTE, -1)
            add(HOUR_OF_DAY, fitHours)
            set(MILLISECOND, 999)
        }
        return result
    }

    private fun drawDays(canvas: Canvas) {
        val checkpoint = canvas.save()
        canvas.clipRect(contentLeft, topPadding, contentRight, contentBottom)
        var x = contentLeft - (ceil(xScroll) - xScroll) * cellWidth
        try {
            //рисуем вертикальные прямоугольники для отображения дней и вертикальные же разделители
            val drawingDate = getFirstDrawableDateTime()
            for (i in 0..fitDays) {
                if (i > 0) {
                    drawingDate.add(DATE, 1)
                }
                paint.color = dateToColor(drawingDate)
                canvas.drawRect(x, contentTop, x + cellWidth, contentBottom, paint)
                x += cellWidth
                paint.color = daySepColor
                paint.strokeWidth = daySepWidth.toFloat()

                canvas.drawLine(x, topPadding, x, contentBottom, paint)

                //определяем прямоугольник для рисования текста даты
                textRect = getDayTextRect(i, contentLeft, cellWidth, textRect)
                //рисуем горизонтальный заголовок для дней
                drawDaysHeaderText(
                    canvas, prepareDaysHeaderPaint(textPaint),
                    textRect, dateFormatter.format(drawingDate.time)
                )

                x += daySepWidth
            }
        } finally {
            canvas.restoreToCount(checkpoint)
        }
        //линя под заголовком дней
        paint.color = daySepColor
        paint.strokeWidth = daySepWidth.toFloat()
        val y = topPadding + daysHeaderHeight
        x = leftPadding
        canvas.drawLine(x, y, width - rightPadding, y, paint)
    }

    private fun drawHours(canvas: Canvas) {
        val checkpoint = canvas.save()
        canvas.clipRect(leftPadding, contentTop, contentRight, contentBottom)
        try {
            var y = contentTop - (yScroll - floor(yScroll)) * cellHeight
            //рисуем горизотнальные линии для отображения разделителей часов
            val drawingHour = floor(yScroll).toInt()
            for (i in 0..fitHours) {
                paint.color = hourSepColor
                paint.strokeWidth = hourSepWidth.toFloat()
                canvas.drawLine(leftPadding, y, contentRight, y, paint)

                if (hourFraction != HourFraction.hf1) {
                    val fractions = hourFraction.value - 1
                    val fractionSize = cellHeight / hourFraction.value
                    for (j in 0..fractions) {
                        path.reset()
                        path.moveTo(contentLeft, y + j * fractionSize)
                        path.lineTo(contentRight, y + j * fractionSize)
                        canvas.drawPath(path, dashedPaint)
//                        canvas.drawLine(contentLeft, y + j * fractionSize,
//                            contentRight, y + j * fractionSize, dashedPaint)
                    }
                }

                //определяем прямоугольник для рисования текста часов
                textRect.left = 0f
                textRect.top = y
                textRect.right = hoursHeaderWidth.toFloat()
                textRect.bottom = y + cellHeight
                drawHoursHeaderText(
                    canvas, prepareHoursHeaderPaint(textPaint),
                    textRect, "${drawingHour + i}:00".padStart(5, '0')
                )

                y += (cellHeight + hourSepWidth)
            }
        } finally {
            canvas.restoreToCount(checkpoint)
        }

        //рисуем вертикальный заголовок для часов
        paint.color = hourSepColor
        paint.strokeWidth = hourSepWidth.toFloat()
        val x = leftPadding + hoursHeaderWidth
        canvas.drawLine(x, topPadding, x, height - bottomPadding, paint)
    }

    private fun isEventVisible(
        event: SchedulerEvent,
        firstDrawableDate: Calendar,
        lastDrawableDate: Calendar
    ): Boolean {
        val minTime = firstDrawableDate.get(HOUR_OF_DAY) + firstDrawableDate.get(MINUTE) / 60
        val maxTime = lastDrawableDate.get(HOUR_OF_DAY) + firstDrawableDate.get(MINUTE) / 60
        val windowStart = firstDrawableDate.time
        val windowFinish = lastDrawableDate.time
        val eventStart = event.getDateTimeStart().time
        val eventFinish = event.getDateTimeFinish().time
        return (
                //начало события внутри окна
                (((eventStart == windowStart || eventStart.after(windowStart)) &&
                        (eventStart == windowFinish || eventStart.before(windowFinish))) ||
                        //или конец события внутри окна
                        ((eventFinish == windowStart || eventFinish.after(windowStart)) &&
                                (eventFinish == windowFinish || eventFinish.before(windowFinish))))
                        &&
                        //либо начало в пределах "окна"
                        ((event.getDateTimeStart().get(HOUR_OF_DAY) + event.getDateTimeStart()
                            .get(MINUTE) / 60 in minTime..maxTime) ||
                                //либо конец в пределах "окна"
                                (event.getDateTimeFinish()
                                    .get(HOUR_OF_DAY) + event.getDateTimeFinish().get(
                                    MINUTE
                                ) / 60 in minTime..maxTime) ||
                                //либо и начало и конец за пределами "окна"
                                (event.getDateTimeStart()
                                    .get(HOUR_OF_DAY) + event.getDateTimeStart().get(
                                    MINUTE
                                ) / 60 < minTime &&
                                        event.getDateTimeFinish()
                                            .get(HOUR_OF_DAY) + event.getDateTimeFinish().get(
                                    MINUTE
                                ) / 60 > maxTime))) ||
                //или начало события меньше начала окна и конец события больше конца окна
                (eventStart.before(windowStart) && eventFinish.after(windowFinish))
    }

    private fun toSimpleDate(time: Long) = time - time % DAY_LENGTH

    private fun prepareEventRects() {
        eventRects.clear()
        val firstDrawableDate = getFirstDrawableDateTime()
        val lastDrawableDate = getLastDrawableDateTime(firstDrawableDate)

        var drawableEvents =
            events.filter { isEventVisible(it, firstDrawableDate, lastDrawableDate) }
        if (drawableEvents.isEmpty()) {
            return
        } else {
            //Сортируем так, чтобы более поздние события были поверх более ранних
            drawableEvents =
                drawableEvents.sortedBy { schedulerEvent -> schedulerEvent.getDateTimeStart() }
        }
        //Самый левый край первого дня
        val leftEdge = contentLeft + 1 - (ceil(xScroll) - xScroll) * cellWidth
        var diffDays: Long
        var eventStartHour: Float
        var eventFinishHour: Float
        for (event in drawableEvents) {
            //Опеределим область рисования события
            diffDays = (toSimpleDate(event.getDateTimeStart().time.time) -
                    toSimpleDate(firstDrawableDate.time.time)) / DAY_LENGTH
            eventStartHour =
                event.getDateTimeStart().get(HOUR_OF_DAY) + event.getDateTimeStart()
                    .get(MINUTE) / 60f
            val daysDiff = event.getDateTimeFinish().get(Calendar.DATE) - event.getDateTimeStart().get(Calendar.DATE)
            if (daysDiff == 0) {
                eventFinishHour =
                    event.getDateTimeFinish().get(HOUR_OF_DAY) + event.getDateTimeFinish()
                        .get(MINUTE) / 60f
            }
            else {
                eventFinishHour = 24f
            }
            eventRect.left = leftEdge + diffDays * cellWidth + eventMargin
            eventRect.top = contentTop + (eventStartHour - yScroll) * cellHeight
            eventRect.right = eventRect.left + cellWidth - 2 * eventMargin
            eventRect.bottom = contentTop + (eventFinishHour - yScroll) * cellHeight
            //запоминаем прямоугольники, в которые потом будем тыкать
            eventRects.add(EventRect(event, RectF(eventRect)))
        }
    }

    private fun drawContent(canvas: Canvas) {
        prepareEventRects()
        val checkpoint = canvas.save()
        canvas.clipRect(contentLeft, contentTop, contentRight, contentBottom)
        try {
            for (eventRect in eventRects) {
                eventDrawer.draw(context, eventRect.event, canvas, eventPaint, eventRect.rect)
            }
        } finally {
            canvas.restoreToCount(checkpoint)
        }
    }

    private fun prepareDaysHeaderPaint(paint: Paint): Paint {
        paint.color = daysHeaderTextColor
        paint.textSize = daysHeaderTextSize.toFloat()
        return paint
    }

    private fun prepareHoursHeaderPaint(paint: Paint): Paint {
        paint.color = hoursHeaderTextColor
        paint.textSize = hoursHeaderTextSize.toFloat()
        return paint
    }

    private fun drawDaysHeaderText(canvas: Canvas, paint: Paint, rect: RectF, text: String) {
        val textHeight = textPaint.descent() - textPaint.ascent()
        val textOffset = (textHeight / 2) - textPaint.descent()
        val checkpoint = canvas.save()
        canvas.clipRect(rect)
        try {
            canvas.drawText(text, rect.centerX(), rect.centerY() + textOffset, paint)
        } finally {
            canvas.restoreToCount(checkpoint)
        }
    }

    private fun drawHoursHeaderText(canvas: Canvas, paint: Paint, rect: RectF, text: String) {
        val checkpoint = canvas.save()
        canvas.clipRect(rect)
        try {
            canvas.drawText(text, rect.centerX(), rect.top + paint.textSize, paint)
        } finally {
            canvas.restoreToCount(checkpoint)
        }
    }

    private fun getDayTextRect(index: Int, left: Float, width: Float, rect: RectF?): RectF {
        val result = rect ?: RectF(0f, 0f, 0f, 0f)
        val ceilScroll = ceil(xScroll)
        result.left = left + index * (width + daySepWidth) - (ceilScroll - xScroll) * cellWidth
        result.top = paddingTop.toFloat()
        result.right =
            left + (index + 1) * (width + daySepWidth) - (ceilScroll - xScroll) * cellWidth
        result.bottom = paddingTop.toFloat() + daysHeaderHeight
        return result
    }

    @ColorInt
    private fun dateToColor(date: Calendar): Int {
        return when (date.get(Calendar.DAY_OF_WEEK)) {
            MONDAY -> colorMon
            TUESDAY -> colorTue
            WEDNESDAY -> colorWed
            THURSDAY -> colorThu
            FRIDAY -> colorFri
            SATURDAY -> colorSat
            SUNDAY -> colorSun
            else -> throw IllegalArgumentException("Unknown day of week")
        }
    }

    private fun daysBetween(from: Calendar, to: Calendar): Double {
        val cloneFrom = from.clone() as Calendar
        val cloneTo = to.clone() as Calendar
        cloneFrom.set(Calendar.HOUR_OF_DAY, 0)
        cloneFrom.set(Calendar.MINUTE, 0)
        cloneFrom.set(Calendar.SECOND, 0)
        cloneFrom.set(Calendar.MILLISECOND, 0)
        cloneTo.set(Calendar.HOUR_OF_DAY, 0)
        cloneTo.set(Calendar.MINUTE, 0)
        cloneTo.set(Calendar.SECOND, 0)
        cloneTo.set(Calendar.MILLISECOND, 0)
        return (cloneTo.time.time - cloneFrom.time.time) * 1.0 / (1000 * 60 * 60 * 24)
        //from.get(DATE) - to.get(DATE)
        //TimeUnit.DAYS.convert(to.time.time - from.time.time, TimeUnit.MILLISECONDS).toInt()
    }

    fun scrollTo(to: Calendar) {
        xScroll = -daysBetween(startingDate, to).toFloat()
        yScroll = normalizeYScroll(to.get(HOUR_OF_DAY).toFloat() + to.get(MINUTE).toFloat() / 60)
        postInvalidate()
        onDateRangeChanged()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        if (action == MotionEvent.ACTION_DOWN) {
            //тут прерываем fling
            if (gestureListener.flinging) {
                scroller.forceFinished(true)
                gestureListener.flinging = false
            }
        }
        gestureDetector.onTouchEvent(event)
        return true
    }

    fun normalizeYScroll(value: Float): Float {
        if (value > 24f - fitHours)
            return 24f - fitHours
        else {
            if (value < 0)
                return 0f
        }
        return value
    }

    private fun onDateRangeChanged() {
        if (viewListener != null) {
            val firstDrawableDateTime = getFirstDrawableDateTime()
            val lastDrawableDateTime = getLastDrawableDateTime(firstDrawableDateTime)
            viewListener?.onDateTimeRangeChanged(firstDrawableDateTime, lastDrawableDateTime)
        }
    }

    private inner class SchedulerViewGestureListener() : SimpleOnGestureListener() {
        var flinging = false
            set(value) {
                field = value
                onDateRangeChanged()
            }

        var prevFlingX = 0
        var prevFlingY = 0

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            val listener = eventClickListener
            if (listener != null) {
                var result: EventRect? = null
                //эти прямоугольники лежат в порядке, обратном требуемуму, т.к. рисуются в правильном
                //поэтому придется перебрать все, чтобы найти самый верхний, содержащий точку касания
                for (eventRect in eventRects) {
                    if (eventRect.rect.contains(e.x, e.y)) {
                        result = eventRect
                    }
                }
                if (result != null) {
                    listener.onSchedulerEventClickListener(result.event)
                }
                return true
            }
            //путь, ведущий к onClick?
            return super.onSingleTapConfirmed(e)
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            val prevScrollX = xScroll
            val prevScrollY = yScroll
            //Скролл по вертикали имеет ограничения.
            //Причем, поскольку cellHeight имеет разное значение в разных ориентациях экрана, то
            //значение yScroll должно измеряться в долях часа (или иначе в cellHeight-ах).
            //Вначале мы располагаемся на minHour, что озачает,
            //что мы как будто сделали scroll на minHour часов.
            //Таким образом yScroll не может быть меньше 0 часов.
            //Максимальное значение для часов 24. Следовательно yScroll не должен
            //превышать значение 24 часов.
            if (distanceY != 0f) {
                yScroll += distanceY / cellHeight
                yScroll = normalizeYScroll(yScroll)
            }

            //Скролл по горизонтали не имеет ограничений, поэтому
            if (distanceX != 0f) {
                xScroll -= distanceX / cellWidth
            }

            if (xScroll != prevScrollX || yScroll != prevScrollY) {
                invalidate()
                onDateRangeChanged()
            }
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            scroller.forceFinished(true)
            flinging = true
            prevFlingX = 0
            prevFlingY = (yScroll * cellHeight).toInt()
            scroller.fling(
                0, (yScroll * cellHeight).toInt(),
                -velocityX.toInt(), -velocityY.toInt(),
                Int.MIN_VALUE, Int.MAX_VALUE,
                0, (24 - fitHours) * cellHeight.toInt()
            )
            invalidate()
            return true
        }
    }

    override fun computeScroll() {
        if (gestureListener.flinging) {
            gestureListener.flinging = scroller.computeScrollOffset()
            val currX = scroller.currX
            val currY = scroller.currY
            xScroll += (gestureListener.prevFlingX - currX) / cellWidth
            yScroll += (currY - gestureListener.prevFlingY) / cellHeight
            yScroll = normalizeYScroll(yScroll)
            gestureListener.prevFlingX = currX
            gestureListener.prevFlingY = currY
            postInvalidate()
        }
    }

    fun setEventDrawer(eventDrawer: EventDrawer) {
        this.eventDrawer = eventDrawer
    }

    fun getEventDrawer() = eventDrawer

    fun setSchedulerViewListener(viewListener: SchedulerViewListener?) {
        this.viewListener = viewListener
    }

    fun getSchedulerViewListener(): SchedulerViewListener? = viewListener

    fun setEvents(events: List<SchedulerEvent>) {
        this.events.clear()
        this.events.addAll(events)
        postInvalidate()
    }

    fun getEvents(): List<SchedulerEvent> = events

    fun removeEvent(event: SchedulerEvent) {
        events.remove(event)
        postInvalidate()
    }

    fun clearEvents() {
        events.clear()
        postInvalidate()
    }

    fun addEvents(events: List<SchedulerEvent>) {
        val list = events.toMutableList()
        list.removeAll(this.events)
        this.events.addAll(list)
        postInvalidate()
    }

    fun setEventClickListener(eventClickListener: SchedulerEventClickListener) {
        this.eventClickListener = eventClickListener
    }


    enum class HourFraction(val value: Int) {
        hf1(1), hf2(2), hf3(3), hf4(4), hf5(5), hf6(6), hf10(10), hf12(12), hf15(15), hf20(20), hf30(
            30
        );

        companion object {
            fun fromInt(value: Int): HourFraction {
                return when (value) {
                    2 -> hf2
                    3 -> hf3
                    4 -> hf4
                    5 -> hf5
                    6 -> hf6
                    10 -> hf10
                    12 -> hf12
                    15 -> hf15
                    20 -> hf20
                    30 -> hf30
                    else -> hf1
                }
            }
        }
    }

    private class SchedulerViewState : BaseSavedState {
        var fitDays: Int = DEF_FIT_DAYS
        var fitHours: Int = DEF_FIT_HOURS
        var minHour: Int = DEF_MIN_HOUR
        var maxHour: Int = DEF_MAX_HOUR
        var hourFraction: HourFraction = HourFraction.fromInt(DEF_HOUR_FRACTION)

        @ColorInt
        var colorMon: Int = DEF_COLOR_MON

        @ColorInt
        var colorTue: Int = DEF_COLOR_TUE

        @ColorInt
        var colorWed: Int = DEF_COLOR_WED

        @ColorInt
        var colorThu: Int = DEF_COLOR_THU

        @ColorInt
        var colorFri: Int = DEF_COLOR_FRI

        @ColorInt
        var colorSat: Int = DEF_COLOR_SAT

        @ColorInt
        var colorSun: Int = DEF_COLOR_SUN

        @ColorInt
        var daySepColor: Int = DEF_DAY_SEP_COLOR

        @ColorInt
        var hourSepColor: Int = DEF_HOUR_SEP_COLOR
        var hoursHeaderWidth: Int = DEF_HOURS_HEADER_WIDTH
        var daysHeaderHeight: Int = DEF_DAYS_HEADER_HEIGHT
        var daySepWidth: Int = DEF_DAY_SEP_WIDTH
        var hourSepWidth: Int = DEF_HOUR_SEP_WIDTH
        var startingDate: Calendar = Calendar.getInstance()
        var daysHeaderTextSize: Int = DEF_DAYS_HEADER_TEXT_SIZE
        var daysHeaderTextColor: Int = DEF_DAYS_HEADER_TEXT_COLOR
        var hoursHeaderTextSize: Int = DEF_HOURS_HEADER_TEXT_SIZE
        var hoursHeaderTextColor: Int = DEF_HOURS_HEADER_TEXT_COLOR
        var eventMargin: Int = DEF_EVENT_MARGIN
        var xScroll = 0f
        var yScroll = 0f

        constructor(parcelable: Parcelable?) : super(parcelable)

        constructor(parcel: Parcel) : super(parcel) {
            fitDays = parcel.readInt()
            fitHours = parcel.readInt()
            minHour = parcel.readInt()
            maxHour = parcel.readInt()
            hourFraction = HourFraction.fromInt(parcel.readInt())
            colorMon = parcel.readInt()
            colorTue = parcel.readInt()
            colorWed = parcel.readInt()
            colorThu = parcel.readInt()
            colorFri = parcel.readInt()
            colorSat = parcel.readInt()
            colorSun = parcel.readInt()
            daySepColor = parcel.readInt()
            hourSepColor = parcel.readInt()
            hoursHeaderWidth = parcel.readInt()
            daysHeaderHeight = parcel.readInt()
            daySepWidth = parcel.readInt()
            hourSepWidth = parcel.readInt()
            daysHeaderTextSize = parcel.readInt()
            daysHeaderTextColor = parcel.readInt()
            hoursHeaderTextSize = parcel.readInt()
            hoursHeaderTextColor = parcel.readInt()
            eventMargin = parcel.readInt()
            xScroll = parcel.readFloat()
            yScroll = parcel.readFloat()
            startingDate = parcel.readSerializable() as Calendar
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeInt(fitDays)
            parcel.writeInt(fitHours)
            parcel.writeInt(minHour)
            parcel.writeInt(maxHour)
            parcel.writeInt(hourFraction.value)
            parcel.writeInt(colorMon)
            parcel.writeInt(colorTue)
            parcel.writeInt(colorWed)
            parcel.writeInt(colorThu)
            parcel.writeInt(colorFri)
            parcel.writeInt(colorSat)
            parcel.writeInt(colorSun)
            parcel.writeInt(daySepColor)
            parcel.writeInt(hourSepColor)
            parcel.writeInt(hoursHeaderWidth)
            parcel.writeInt(daysHeaderHeight)
            parcel.writeInt(daySepWidth)
            parcel.writeInt(hourSepWidth)
            parcel.writeInt(daysHeaderTextSize)
            parcel.writeInt(daysHeaderTextColor)
            parcel.writeInt(hoursHeaderTextSize)
            parcel.writeInt(hoursHeaderTextColor)
            parcel.writeInt(eventMargin)
            parcel.writeFloat(xScroll)
            parcel.writeFloat(yScroll)
            parcel.writeSerializable(startingDate)
        }

        companion object CREATOR : Parcelable.Creator<SchedulerViewState> {
            override fun createFromParcel(parcel: Parcel): SchedulerViewState {
                return SchedulerViewState(parcel)
            }

            override fun newArray(size: Int): Array<SchedulerViewState?> {
                return arrayOfNulls(size)
            }
        }
    }

    class BeforeChangeInvalidationInitiatorProperty<T>(
        initialValue: T,
        private val onChange: () -> Unit
    ) : ObservableProperty<T>(initialValue) {
        override fun beforeChange(property: KProperty<*>, oldValue: T, newValue: T): Boolean {
            if (oldValue != newValue) {
                onChange()
            }
            return true
        }
    }

    class AfterChangeInvalidationInitiatorProperty<T>(
        initialValue: T,
        private val onChange: () -> Unit
    ) : ObservableProperty<T>(initialValue) {
        override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
            if (oldValue != newValue) {
                onChange()
            }
        }
    }

    companion object {
        const val DAY_LENGTH = 24 * 60 * 60 * 1000
        const val DEF_FIT_DAYS = 5
        const val DEF_FIT_HOURS = 4
        const val DEF_MIN_HOUR: Int = 8
        const val DEF_MAX_HOUR: Int = 22
        const val DEF_HOUR_FRACTION = 2

        @ColorInt
        const val DEF_COLOR_MON: Int = Color.WHITE

        @ColorInt
        const val DEF_COLOR_TUE: Int = Color.WHITE

        @ColorInt
        const val DEF_COLOR_WED: Int = Color.WHITE

        @ColorInt
        const val DEF_COLOR_THU: Int = Color.WHITE

        @ColorInt
        const val DEF_COLOR_FRI: Int = Color.WHITE

        @ColorInt
        const val DEF_COLOR_SAT: Int =
            0xAAFFEBEE.toInt() //Kotlin Bug https://youtrack.jetbrains.com/issue/KT-2780

        @ColorInt
        const val DEF_COLOR_SUN: Int =
            0xAAFFEBEE.toInt() //Kotlin Bug https://youtrack.jetbrains.com/issue/KT-2780

        @ColorInt
        const val DEF_DAY_SEP_COLOR: Int =
            0xFFEAEAEA.toInt() //Kotlin Bug https://youtrack.jetbrains.com/issue/KT-2780

        @ColorInt
        const val DEF_HOUR_SEP_COLOR: Int =
            0xFFEAEAEA.toInt() //Kotlin Bug https://youtrack.jetbrains.com/issue/KT-2780
        const val DEF_HOURS_HEADER_WIDTH: Int = 40
        const val DEF_DAYS_HEADER_HEIGHT: Int = 40
        const val DEF_DAY_SEP_WIDTH: Int = 1
        const val DEF_HOUR_SEP_WIDTH: Int = 1
        const val DEF_DAYS_HEADER_TEXT_SIZE: Int = 20
        const val DEF_DAYS_HEADER_TEXT_COLOR: Int = Color.BLACK
        const val DEF_HOURS_HEADER_TEXT_SIZE: Int = 14
        const val DEF_HOURS_HEADER_TEXT_COLOR: Int = Color.BLACK
        const val DEF_DECELERATE_FACTOR = 2.5f
        const val DEF_EVENT_MARGIN = 6
        const val DEF_DASH_SIZE = 10f
    }

    init {
        dashedPaint.color = hourSepColor
        dashedPaint.style = Paint.Style.STROKE
        dashedPaint.pathEffect = DashPathEffect(floatArrayOf(DEF_DASH_SIZE, DEF_DASH_SIZE), 0f)
    }
}