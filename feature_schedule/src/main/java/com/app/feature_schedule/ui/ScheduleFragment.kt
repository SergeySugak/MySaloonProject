package com.app.feature_schedule.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.app.feature_schedule.R
import com.app.feature_schedule.di.DaggerScheduleFeatureComponent
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSFragment
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import com.app.view_schedule.api.SchedulerEvent
import com.app.view_schedule.ui.SchedulerView
import java.util.*
import javax.inject.Inject

class ScheduleFragment :  MSFragment<ScheduleViewModel>()  {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    override val layoutId = R.layout.schedule_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerScheduleFeatureComponent
            .builder()
            .scheduleFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun createViewModel(savedInstanceState: Bundle?): ScheduleViewModel {
        return ViewModelProvider(this, providerFactory).get(ScheduleViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val schedulerView = view.findViewById<SchedulerView>(R.id.schedule_view)
        schedulerView.setEvents(listOf(object : SchedulerEvent{
            override val id = "1"
            override val dateTimeStart = Calendar.getInstance().apply{set(Calendar.HOUR_OF_DAY, 9)}
            override val dateTimeFinish = (dateTimeStart.clone() as Calendar).apply{add(Calendar.HOUR_OF_DAY, 2)}
            override val header = "test"
            override val text = "test descr"
        }))
    }

    override fun onStartObservingViewModel(viewModel: ScheduleViewModel) {

    }

    override fun onHiddenChanged(hidden: Boolean) {

    }

    companion object {
        fun newInstance() = ScheduleFragment()
    }
}
