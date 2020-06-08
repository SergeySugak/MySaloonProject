package com.app.feature_event_scheduler.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import android.widget.TimePicker
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.feature_event_scheduler.R
import com.app.feature_event_scheduler.di.DaggerEventSchedulerFeatureComponent
import com.app.feature_event_scheduler.ui.DateTimeSelectionViewModel.Companion.MODE_DATE
import com.app.feature_event_scheduler.ui.DateTimeSelectionViewModel.Companion.MODE_TIME
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSActivity
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DateTimeSelectionActivity : MSActivity<DateTimeSelectionViewModel>() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    private lateinit var dateFormatter: SimpleDateFormat
    private val calendarView: CalendarView by lazy { findViewById<CalendarView>(R.id.calendarView) }
    private val timePicker: TimePicker by lazy { findViewById<TimePicker>(R.id.timePicker) }
    private val dateAndTime: TextView by lazy { findViewById<TextView>(R.id.date_and_time) }
    private val viewModeSwitcher: Button by lazy { findViewById<Button>(R.id.switcher) }

    private val buttonTitleList = listOf(R.string.str_time, R.string.str_date)

    override val layoutId = R.layout.activity_date_time_selection

    override fun getMenu() = R.menu.date_time_selection_menu

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerEventSchedulerFeatureComponent
            .builder()
            .eventSchedulerFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
        setupActionBar()
        timePicker.setIs24HourView(true)
        calendarView.setOnDateChangeListener{ view, year, month, dayOfMonth ->
            getViewModel()?.setDate(year, month, dayOfMonth)
        }
        timePicker.setOnTimeChangedListener{ view, hourOfDay, minute ->
            getViewModel()?.setTime(hourOfDay, minute)
        }
        viewModeSwitcher.setOnClickListener {
            getViewModel()?.updateMode()
        }

//        button.setOnClickListener{ _ ->
//                getViewModel()?.saveEventInfo(
//                    dlg.findViewById<EditText>(R.id.master_name)?.text.toString(),
//                    dlg.findViewById<EditText>(R.id.master_description)?.text.toString(),
//                    dlg.findViewById<EditText>(R.id.master_portfolio_url)?.text.toString())
//        }
    }

    override fun createViewModel(savedInstanceState: Bundle?) =
        ViewModelProvider(this, providerFactory).get(DateTimeSelectionViewModel::class.java)

    private fun runTransition(mode: Int) {
        when (mode) {
            MODE_TIME -> fromTo(calendarView, timePicker)
            MODE_DATE -> fromTo(timePicker, calendarView)
        }
    }

    private fun fromTo(v1: View, v2: View) {
        v1.animate()
            .alpha(0f)
            .setDuration(ANIMATION_DURATION)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    v1.visibility = INVISIBLE
                }
            })
        v2.apply {
            alpha = 0f
            visibility = VISIBLE
            animate()
                .alpha(1f)
                .setDuration(ANIMATION_DURATION)
                .setListener(null)
        }
    }

    override fun onStartObservingViewModel(viewModel: DateTimeSelectionViewModel) {
        viewModel.mode.observe(this, Observer { mode ->
            if (!viewModel.mode.isHandled){
//                (dialog as AlertDialog)
//                    .getButton(BUTTON_NEUTRAL)
//                    .setText(buttonTitleList[mode])
                runTransition(mode)
                viewModel.mode.isHandled = true
            }
        })
        viewModel.calendar.observe(this, Observer { calendar ->
            if (!viewModel.calendar.isHandled){
                if (!::dateFormatter.isInitialized){
                    dateFormatter = SimpleDateFormat(getString(R.string.str_date_format), Locale.getDefault())
                }
                dateAndTime.text = dateFormatter.format(calendar.time)
                viewModel.calendar.isHandled = true
            }
        })
    }

    companion object {
        private const val ANIMATION_DURATION = 350L
    }
}
