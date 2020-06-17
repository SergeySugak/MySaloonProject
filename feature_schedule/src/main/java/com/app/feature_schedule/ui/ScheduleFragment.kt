package com.app.feature_schedule.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.feature_schedule.R
import com.app.feature_schedule.di.DaggerScheduleFeatureComponent
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSFragment
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import com.app.view_schedule.api.SchedulerEvent
import com.app.view_schedule.api.SchedulerEventClickListener
import com.app.view_schedule.api.SchedulerViewListener
import com.app.view_schedule.ui.SchedulerView
import java.util.*
import javax.inject.Inject

class ScheduleFragment : MSFragment<ScheduleViewModel>() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set
    private val loading: ProgressBar by lazy { requireActivity().findViewById<ProgressBar>(R.id.loading) }

    override val layoutId = R.layout.schedule_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerScheduleFeatureComponent
            .builder()
            .scheduleFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun createViewModel(savedInstanceState: Bundle?) =
        ViewModelProvider(this, providerFactory).get(ScheduleViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val schedulerView = view.findViewById<SchedulerView>(R.id.schedule_view)
        schedulerView.setSchedulerViewListener(object : SchedulerViewListener {
            override fun onDateTimeRangeChanged(from: Calendar, to: Calendar) {
                getViewModel()?.notifier?.onNext(Pair(from, to))
            }
        })
        val fromDate = schedulerView.getFirstDrawableDateTime()
        val toDate = schedulerView.getLastDrawableDateTime(fromDate)
        getViewModel()?.loadData(fromDate, toDate)
//        schedulerView.setEvents(listOf(object : SchedulerEvent {
//            override val id = "1"
//            override val dateTimeStart =
//                Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 9) }
//            override val dateTimeFinish =
//                (dateTimeStart.clone() as Calendar).apply { add(Calendar.HOUR_OF_DAY, 2) }
//            override val header = "long header text"
//            override val text = "test descr"
//            override val color =
//                getViewModel()?.eventColorizer?.getRandomColor(view.context) ?: Color.WHITE
//        },
//            object : SchedulerEvent {
//                override val id = "2"
//                override val dateTimeStart = Calendar.getInstance().apply {
//                    set(Calendar.HOUR_OF_DAY, 9)
//                    set(Calendar.MINUTE, 30)
//                }
//                override val dateTimeFinish =
//                    (dateTimeStart.clone() as Calendar).apply { add(Calendar.HOUR_OF_DAY, 1) }
//                override val header = "header text"
//                override val text = "test descr 2"
//                override val color =
//                    getViewModel()?.eventColorizer?.getRandomColor(view.context) ?: Color.WHITE
//            },
//            object : SchedulerEvent {
//                override val id = "2"
//                override val dateTimeStart = Calendar.getInstance().apply {
//                    add(Calendar.DATE, 2)
//                    set(Calendar.HOUR_OF_DAY, 11)
//                    set(Calendar.MINUTE, 30)
//                }
//                override val dateTimeFinish =
//                    (dateTimeStart.clone() as Calendar).apply { add(Calendar.HOUR_OF_DAY, 1) }
//                override val header = "header text 3"
//                override val text = "test descr 3"
//                override val color =
//                    getViewModel()?.eventColorizer?.getRandomColor(view.context) ?: Color.WHITE
//            }
//        ))
        schedulerView.setEventClickListener(object : SchedulerEventClickListener {
            override fun onSchedulerEventClickListener(event: SchedulerEvent) {
                MessageDialogFragment.showMessage(
                    this@ScheduleFragment,
                    getString(R.string.title_information), event.header
                )
            }
        })
    }

    override fun onStartObservingViewModel(viewModel: ScheduleViewModel) {
        viewModel.error.observe(this, Observer { error ->
            if (!viewModel.error.isHandled) {
                MessageDialogFragment.showError(this, error, false)
                viewModel.error.isHandled = true
            }
        })

        viewModel.isInProgress.observe(this, Observer {
            loading.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    override fun onHiddenChanged(hidden: Boolean) {

    }

    companion object {
        fun newInstance() = ScheduleFragment()
    }
}
