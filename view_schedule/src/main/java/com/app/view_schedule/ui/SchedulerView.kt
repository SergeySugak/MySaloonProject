package com.app.view_schedule.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Parcel
import android.os.Parcelable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import androidx.annotation.ColorInt
import com.app.view_schedule.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.math.ceil
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty


class SchedulerView(context: Context, attrs: AttributeSet?, defStyleAttr: Int):
    View(context, attrs, defStyleAttr) {

    init {
        isSaveEnabled = true
        if (id == NO_ID){
            id = generateViewId()
        }
    }

    fun onPropertyChanged(){
        if (!delayInvalidation) {
            invalidate()
        }
    }
    var delayInvalidation = false
    var fitDays: Int by InvalidationInitiatorProperty(DEF_FIT_DAYS){onPropertyChanged()}
    var fitHours: Int by InvalidationInitiatorProperty(DEF_FIT_HOURS){onPropertyChanged()}
    var minHour: Int by InvalidationInitiatorProperty(DEF_MIN_HOUR){onPropertyChanged()}
    var maxHour: Int by InvalidationInitiatorProperty(DEF_MAX_HOUR){onPropertyChanged()}
    var hourFraction: HourFraction by InvalidationInitiatorProperty(HourFraction.fromInt(DEF_HOUR_FRACTION)){onPropertyChanged()}
    var colorMon: Int by InvalidationInitiatorProperty(DEF_COLOR_MON){onPropertyChanged()}
    var colorTue: Int by InvalidationInitiatorProperty(DEF_COLOR_TUE){onPropertyChanged()}
    var colorWed: Int by InvalidationInitiatorProperty(DEF_COLOR_WED){onPropertyChanged()}
    var colorThu: Int by InvalidationInitiatorProperty(DEF_COLOR_THU){onPropertyChanged()}
    var colorFri: Int by InvalidationInitiatorProperty(DEF_COLOR_FRI){onPropertyChanged()}
    var colorSat: Int by InvalidationInitiatorProperty(DEF_COLOR_SAT){onPropertyChanged()}
    var colorSun: Int by InvalidationInitiatorProperty(DEF_COLOR_SUN){onPropertyChanged()}
    var daySepColor: Int by InvalidationInitiatorProperty(DEF_DAY_SEP_COLOR){onPropertyChanged()}
    var hourSepColor: Int by InvalidationInitiatorProperty(DEF_HOUR_SEP_COLOR){onPropertyChanged()}
    var hoursHeaderWidth: Int by InvalidationInitiatorProperty(DEF_HOURS_HEADER_WIDTH){onPropertyChanged()}
    var daysHeaderHeight: Int by InvalidationInitiatorProperty(DEF_DAYS_HEADER_HEIGHT){onPropertyChanged()}
    var daySepWidth: Int by InvalidationInitiatorProperty(DEF_DAY_SEP_WIDTH){onPropertyChanged()}
    var hourSepWidth: Int by InvalidationInitiatorProperty(DEF_HOUR_SEP_WIDTH){onPropertyChanged()}
    var daysHeaderTextSize: Int by InvalidationInitiatorProperty(DEF_DAYS_HEADER_TEXT_SIZE){onPropertyChanged()}
    var daysHeaderTextColor: Int by InvalidationInitiatorProperty(DEF_DAYS_HEADER_TEXT_COLOR){onPropertyChanged()}
    var hoursHeaderTextSize: Int by InvalidationInitiatorProperty(DEF_HOURS_HEADER_TEXT_SIZE){onPropertyChanged()}
    var hoursHeaderTextColor: Int by InvalidationInitiatorProperty(DEF_HOURS_HEADER_TEXT_COLOR){onPropertyChanged()}

    private var dateFormatter = SimpleDateFormat(context.getString(R.string.str_def_date_format), Locale.getDefault())
    var startingDate: Calendar = Calendar.getInstance()

    private val paint = Paint()
    private val textPaint = TextPaint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }
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
            dateFormatter.applyPattern(it)
        }
        val userStaringDate = attributes.getString(R.styleable.SchedulerView_startingDate)
        userStaringDate?.let{
            val date = dateFormatter.parse(it)
            date?.let {
                startingDate.time = it
            }
        }
        daysHeaderTextSize = attributes.getDimensionPixelSize(R.styleable.SchedulerView_daysHeaderTextSize, daysHeaderTextSize)
        daysHeaderTextColor = attributes.getColor(R.styleable.SchedulerView_daysHeaderTextColor, daysHeaderTextColor)
        hoursHeaderTextSize = attributes.getDimensionPixelSize(R.styleable.SchedulerView_hoursHeaderTextSize, hoursHeaderTextSize)
        hoursHeaderTextColor = attributes.getColor(R.styleable.SchedulerView_hoursHeaderTextColor, hoursHeaderTextColor)

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
        xScroll = savedState.xScroll
        yScroll = savedState.yScroll
        startingDate = savedState.startingDate
    }

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

    private fun drawDays(canvas: Canvas){
        val checkpoint = canvas.save()
        canvas.clipRect(contentLeft, topPadding, contentRight, contentBottom)
        var x = contentLeft - (ceil(xScroll) - xScroll) * cellWidth
        try {
            //рисуем вертикальные прямоугольники для отображения дней и вертикальные же разделители
            val drawingDate = Calendar.getInstance()
            drawingDate.time = startingDate.time
            drawingDate.add(Calendar.DATE, -ceil(xScroll).toInt())
            for (i in 0 .. fitDays){
                if (i > 0) {
                    drawingDate.add(Calendar.DATE, 1)
                }
                paint.color = dateToColor(drawingDate)
                canvas.drawRect(x, contentTop, x + cellWidth, contentBottom, paint)
                x += cellWidth
                paint.color = daySepColor
                paint.strokeWidth = daySepWidth.toFloat()

                canvas.drawLine(x, topPadding, x, contentBottom, paint)

                //определяем прямоугольник для рисования текста даты
                textRect = getDayTextRect (i, contentLeft, cellWidth, textRect)
                //рисуем горизонтальный заголовок для дней
                drawDaysHeaderText(canvas, prepareDaysHeaderPaint(textPaint),
                    textRect, dateFormatter.format(drawingDate.time))

                x += daySepWidth
            }
        }
        finally {
            canvas.restoreToCount(checkpoint)
        }
        //линя под заголовком дней
        paint.color = daySepColor
        paint.strokeWidth = daySepWidth.toFloat()
        val y = topPadding + daysHeaderHeight
        x = leftPadding
        canvas.drawLine(x, y, width - rightPadding, y, paint)
    }

    private fun drawHours(canvas: Canvas){
        val checkpoint = canvas.save()
        canvas.clipRect(leftPadding, contentTop, contentRight, contentBottom)
        try {
            var y = contentTop + cellHeight + daySepWidth - (ceil(yScroll)- yScroll) * cellHeight
            //рисуем горизотнальные линии для отображения разделителей часов
            val drawingHour = minHour - ceil(yScroll).toInt()
            for (i in 0 .. fitHours){
                paint.color = hourSepColor
                paint.strokeWidth = hourSepWidth.toFloat()
                canvas.drawLine(leftPadding, y, contentRight, y, paint)

                //определяем прямоугольник для рисования текста часов
                textRect = getHourTextRect(i, contentTop, cellHeight, textRect)
                drawHoursHeaderText(canvas, prepareHoursHeaderPaint(textPaint),
                    textRect, "${drawingHour + i}:00")

                y += (cellHeight + daySepWidth)
            }
        }
        finally {
            canvas.restoreToCount(checkpoint)
        }

        //рисуем вертикальный заголовок для часов
        paint.color = hourSepColor
        paint.strokeWidth = hourSepWidth.toFloat()
        val x = leftPadding + hoursHeaderWidth
        canvas.drawLine(x, topPadding, x, height - bottomPadding, paint)
    }

    private fun drawContent(canvas: Canvas){

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

    private fun drawDaysHeaderText(canvas: Canvas, paint: Paint, rect: RectF, text: String){
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

    private fun drawHoursHeaderText(canvas: Canvas, paint: Paint, rect: RectF, text: String){
        val checkpoint = canvas.save()
        canvas.clipRect(rect)
        try {
            canvas.drawText(text, rect.centerX(), rect.top + paint.textSize, paint)
        } finally {
            canvas.restoreToCount(checkpoint)
        }
    }

    private fun getHourTextRect(index: Int, top: Float, height: Float, rect: RectF?): RectF {
        val result = rect ?: RectF(0f, 0f, 0f, 0f)
        val ceilScroll = ceil(yScroll)
        result.left = paddingLeft.toFloat()
        result.top = top + index * (height + hourSepWidth) - (ceilScroll - yScroll) * cellHeight
        result.right = paddingLeft.toFloat() + hoursHeaderWidth
        result.bottom = top + (index + 1) * (height + hourSepWidth) - (ceilScroll - yScroll) * cellHeight
        return result
    }

    private fun getDayTextRect(index: Int, left: Float, width: Float, rect: RectF?): RectF {
        val result = rect ?: RectF(0f, 0f, 0f, 0f)
        val ceilScroll = ceil(xScroll)
        result.left = left + index * (width + daySepWidth) - (ceilScroll - xScroll) * cellWidth
        result.top = paddingTop.toFloat()
        result.right = left + (index + 1) * (width + daySepWidth) - (ceilScroll - xScroll) * cellWidth
        result.bottom = paddingTop.toFloat() + daysHeaderHeight
        return result
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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        if (action == MotionEvent.ACTION_DOWN) {
            //тут прерываем fling
            if (gestureListener.flinging){
                scroller.forceFinished(true)
                gestureListener.flinging = false
            }
        }
        gestureDetector.onTouchEvent(event)
        return true
    }

    private inner class SchedulerViewGestureListener(): SimpleOnGestureListener() {
        var flinging = false
        var prevFlingX = 0
        var prevFlingY = 0

        fun normalizeYScroll(value: Float): Float {
            if (value > (25 - minHour - fitHours))
                return (25 - minHour - fitHours).toFloat()
            else {
                if (value < -minHour)
                    return -minHour.toFloat()
            }
            return value
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            val prevScrollX = xScroll
            val prevScrollY = yScroll
            //Скролл по вертикали имеет ограничения.
            //Причем, поскольку cellHeight имеет разное значение в разных ориентациях экрана, то
            //значение yScroll должно измеряться в долях часа (или иначе в cellHeight-ах).
            //Вначале мы располагаемся на minHour, что озачает,
            //что мы как будто сделали scroll наверх на minHour часов.
            //Таким образом yScroll не может быть меньше -minHour часов.
            //Максимальное значение для часов 24. Следовательно yScroll не должен
            //превышать значение 24 - minHour часов.
            if (distanceY != 0f){
                val newYScroll = distanceY / cellHeight - yScroll
                yScroll = -normalizeYScroll (newYScroll)
            }

            //Скролл по горизонтали не имеет ограничений, поэтому
            if (distanceX != 0f){
                xScroll -= distanceX / cellWidth
            }

            if (xScroll != prevScrollX || yScroll != prevScrollY){
                invalidate()
            }
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            scroller.forceFinished(true)
            flinging = true
            prevFlingX = 0
            prevFlingY = 0
            scroller.fling(0, (-yScroll * cellHeight).toInt(),
                -velocityX.toInt(), -velocityY.toInt(),
                Int.MIN_VALUE, Int.MAX_VALUE,
                -minHour * cellHeight.toInt(),  (25 - minHour - fitHours) * cellHeight.toInt())
            invalidate()
            return true
        }
    }

    override fun computeScroll() {
        if (gestureListener.flinging){
            gestureListener.flinging = scroller.computeScrollOffset()
            val currX = scroller.currX
            val currY = scroller.currY
            xScroll += (gestureListener.prevFlingX - currX) / cellWidth
            yScroll = (currY - gestureListener.prevFlingY ) / cellHeight - yScroll
            yScroll = -gestureListener.normalizeYScroll(yScroll)
            gestureListener.prevFlingX = currX
            gestureListener.prevFlingY = currY
            postInvalidate()
        }
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

    private class SchedulerViewState: BaseSavedState {
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
        var xScroll = 0f
        var yScroll = 0f

        constructor(parcelable: Parcelable?): super(parcelable)

        constructor(parcel: Parcel): super (parcel){
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

    class InvalidationInitiatorProperty<T>(initialValue: T,
                                           private val onChange: ()->Unit): ObservableProperty<T>(initialValue) {
        override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
            onChange()
        }
    }

    companion object {
        const val DEF_FIT_DAYS = 5
        const val DEF_FIT_HOURS = 4
        const val DEF_MIN_HOUR: Int = 8
        const val DEF_MAX_HOUR: Int = 22
        const val DEF_HOUR_FRACTION = 1
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
        const val DEF_COLOR_SAT: Int = 0xAAFFEBEE.toInt() //Kotlin Bug https://youtrack.jetbrains.com/issue/KT-2780
        @ColorInt
        const val DEF_COLOR_SUN: Int = 0xAAFFEBEE.toInt() //Kotlin Bug https://youtrack.jetbrains.com/issue/KT-2780
        @ColorInt
        const val DEF_DAY_SEP_COLOR: Int = 0xFFEAEAEA.toInt() //Kotlin Bug https://youtrack.jetbrains.com/issue/KT-2780
        @ColorInt
        const val DEF_HOUR_SEP_COLOR: Int = 0xFFEAEAEA.toInt() //Kotlin Bug https://youtrack.jetbrains.com/issue/KT-2780
        const val DEF_HOURS_HEADER_WIDTH: Int = 40
        const val DEF_DAYS_HEADER_HEIGHT: Int = 40
        const val DEF_DAY_SEP_WIDTH: Int = 1
        const val DEF_HOUR_SEP_WIDTH: Int = 1
        const val DEF_DAYS_HEADER_TEXT_SIZE: Int = 20
        const val DEF_DAYS_HEADER_TEXT_COLOR: Int = Color.BLACK
        const val DEF_HOURS_HEADER_TEXT_SIZE: Int = 14
        const val DEF_HOURS_HEADER_TEXT_COLOR: Int = Color.BLACK
        const val DEF_DECELERATE_FACTOR = 2.5f
    }
}