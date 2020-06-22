package com.app.feature_event_scheduler.ui

import android.os.Bundle
import android.view.View
import android.widget.CalendarView
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
import javax.inject.Inject

class DateSelectionDialog : MSBottomSheetDialogFragment<DateAndTimeSelectionViewModel>() {
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    private val calendarView: CalendarView by lazy { requireView().findViewById<CalendarView>(R.id.calendarView) }
    private val toolBar: Toolbar by lazy { requireView().findViewById<Toolbar>(R.id.toolbar) }

    override val layoutId = R.layout.fragment_date_selection

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
        getViewModel()?.let {
            calendarView.setDate(it.calendar.value!!.timeInMillis, true, true)
        }
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            getViewModel()?.setDate(year, month, dayOfMonth)
        }
        toolBar.inflateMenu(R.menu.date_time_selection_menu)
        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.setNavigationOnClickListener{
            dismiss()
        }
        toolBar.menu.findItem(R.id.menu_ok).setOnMenuItemClickListener { _ ->
            if (targetFragment is EventDateTimeReceiver) {
                (targetFragment as EventDateTimeReceiver)
                    .setEventDate(
                        getViewModel()!!.calendar.value!!.get(Calendar.YEAR),
                        getViewModel()!!.calendar.value!!.get(Calendar.MONTH),
                        getViewModel()!!.calendar.value!!.get(Calendar.DAY_OF_MONTH)
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
        viewModel.setDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    override fun onStartObservingViewModel(viewModel: DateAndTimeSelectionViewModel) {}

    companion object {
        const val CALENDAR = "CALENDAR"

        fun newInstance(calendar: Calendar): DateSelectionDialog {
            val result = DateSelectionDialog()
            result.arguments = bundleOf(Pair(CALENDAR, calendar))
            return result
        }
    }
}