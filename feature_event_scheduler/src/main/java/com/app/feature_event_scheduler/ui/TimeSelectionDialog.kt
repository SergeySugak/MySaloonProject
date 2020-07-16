package com.app.feature_event_scheduler.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.app.feature_event_scheduler.R
import com.app.feature_event_scheduler.api.EventDateTimeReceiver
import com.app.feature_event_scheduler.di.DaggerEventSchedulerFeatureComponent
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSBottomSheetDialogFragment
import java.util.*
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE
import javax.inject.Inject

class TimeSelectionDialog : MSBottomSheetDialogFragment<DateAndTimeSelectionViewModel>() {
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    override val layoutId = R.layout.fragment_time_selection

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerEventSchedulerFeatureComponent
            .builder()
            .eventSchedulerFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val timePicker = view.findViewById<TimePicker>(R.id.timePicker)
        val toolBar = view.findViewById<Toolbar>(R.id.toolbar)
        timePicker.setIs24HourView(true)
        getViewModel()?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.hour = it.calendar.value!!.get(Calendar.HOUR_OF_DAY)
                timePicker.minute = it.calendar.value!!.get(Calendar.MINUTE)
            } else {
                timePicker.currentHour = it.calendar.value!!.get(Calendar.HOUR_OF_DAY)
                timePicker.currentMinute = it.calendar.value!!.get(Calendar.MINUTE)
            }
        }
        timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            getViewModel()?.setTime(hourOfDay, minute)
        }
        toolBar.inflateMenu(R.menu.date_time_selection_menu)
        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.setNavigationOnClickListener {
            dismiss()
        }
        toolBar.menu.findItem(R.id.menu_ok).setOnMenuItemClickListener { _ ->
            if (targetFragment is EventDateTimeReceiver) {
                (targetFragment as EventDateTimeReceiver)
                    .setEventTime(
                        getViewModel()!!.calendar.value!!.get(HOUR_OF_DAY),
                        getViewModel()!!.calendar.value!!.get(MINUTE)
                    )
            }
            dismiss()
            true
        }
    }

    override fun createViewModel(savedInstanceState: Bundle?) =
        ViewModelProvider(
            requireActivity(),
            providerFactory
        ).get(DateAndTimeSelectionViewModel::class.java)

    override fun onViewModelCreated(
        viewModel: DateAndTimeSelectionViewModel,
        savedInstanceState: Bundle?
    ) {
        val calendar = requireArguments()[CALENDAR] as Calendar
        viewModel.setTime(calendar.get(HOUR_OF_DAY), calendar.get(MINUTE))
    }

    override fun onStartObservingViewModel(viewModel: DateAndTimeSelectionViewModel) {}

    companion object {
        const val CALENDAR = "CALENDAR"

        fun newInstance(calendar: Calendar): TimeSelectionDialog {
            val result = TimeSelectionDialog()
            result.arguments = bundleOf(Pair(CALENDAR, calendar))
            return result
        }
    }
}