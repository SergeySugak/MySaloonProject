package com.app.feature_schedule.ui

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.app.feature_schedule.R
import com.app.feature_schedule.di.DaggerScheduleFeatureComponent
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSFragment
import javax.inject.Inject

class ScheduleFragment :  MSFragment<ScheduleViewModel>()  {

    companion object {
        fun newInstance() = ScheduleFragment()
    }

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

    override fun onStartObservingViewModel(viewModel: ScheduleViewModel) {

    }
}
