package com.app.feature_event_scheduler.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_NEUTRAL
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Fade
import androidx.transition.Scene
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.app.feature_event_scheduler.R
import com.app.feature_event_scheduler.di.DaggerEventSchedulerFeatureComponent
import com.app.feature_event_scheduler.ui.DateTimeSelectionViewModel.Companion.MODE_DATE
import com.app.feature_event_scheduler.ui.DateTimeSelectionViewModel.Companion.MODE_TIME
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DateTimeSelectionFragment : MSDialogFragment<DateTimeSelectionViewModel>() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    private lateinit var dateFormatter: SimpleDateFormat
    private val calendarView: CalendarView by lazy { dialog!!.findViewById<CalendarView>(R.id.calendarView) }
    private val timePicker: TimePicker by lazy { dialog!!.findViewById<TimePicker>(R.id.timePicker) }
    private val dateAndTime: TextView by lazy { dialog!!.findViewById<TextView>(R.id.date_and_time) }

    private val buttonTitleList = listOf(R.string.str_time, R.string.str_date)

    override val layoutId = R.layout.fragment_date_time_selection

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerEventSchedulerFeatureComponent
            .builder()
            .eventSchedulerFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun createViewModel(savedInstanceState: Bundle?) =
        ViewModelProvider(this, providerFactory).get(DateTimeSelectionViewModel::class.java)

    override fun onBuildDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(layoutId, null)
        val builder = MaterialAlertDialogBuilder(requireActivity())
        builder.setView(view)
            .setTitle(getString(R.string.title_edit_scheduler_event))
            .setPositiveButton(getString(R.string.ok), null)
            .setNegativeButton(getString(R.string.cancel)) { _, _ -> dialog?.dismiss() }
            .setNeutralButton(getString(R.string.str_time), null)
        return builder.create()
    }

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

    override fun onStart() {
        super.onStart()
        val dlg = dialog as AlertDialog?
        if (dlg != null){
            timePicker.setIs24HourView(true)
            var button = dlg.getButton(BUTTON_POSITIVE)
            button.setOnClickListener{ _ ->
//                getViewModel()?.saveEventInfo(
//                    dlg.findViewById<EditText>(R.id.master_name)?.text.toString(),
//                    dlg.findViewById<EditText>(R.id.master_description)?.text.toString(),
//                    dlg.findViewById<EditText>(R.id.master_portfolio_url)?.text.toString())
            }
            button = dlg.getButton(BUTTON_NEUTRAL)
            button.setOnClickListener {
                getViewModel()?.updateMode()
            }
            calendarView.setOnDateChangeListener{ view, year, month, dayOfMonth ->
                getViewModel()?.setDate(year, month, dayOfMonth)
            }
            timePicker.setOnTimeChangedListener{ view, hourOfDay, minute ->
                getViewModel()?.setTime(hourOfDay, minute)
            }
        }
    }

    override fun onStartObservingViewModel(viewModel: DateTimeSelectionViewModel) {
        viewModel.mode.observe(this, Observer { mode ->
            if (!viewModel.mode.isHandled){
                (dialog as AlertDialog)
                    .getButton(BUTTON_NEUTRAL)
                    .setText(buttonTitleList[mode])
                runTransition(mode)
                viewModel.mode.isHandled = true
            }
        })
        viewModel.calendar.observe(this, Observer { calendar ->
            if (!viewModel.calendar.isHandled){
                if (!::dateFormatter.isInitialized){
                    dateFormatter = SimpleDateFormat(requireContext().getString(R.string.str_date_format), Locale.getDefault())
                }
                dateAndTime.text = dateFormatter.format(calendar.time)
                viewModel.calendar.isHandled = true
            }
        })

    }

    companion object {
        private const val ANIMATION_DURATION = 350L

        @JvmStatic
        fun newInstance() = DateTimeSelectionFragment()
    }
}
