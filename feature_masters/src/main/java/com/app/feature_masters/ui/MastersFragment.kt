package com.app.feature_masters.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.feature_masters.R
import com.app.feature_masters.di.DaggerMastersFeatureComponent
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSFragment
import javax.inject.Inject

class MastersFragment : MSFragment<MastersViewModel>() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    override val layoutId = R.layout.masters_fragment

    override fun createViewModel(savedInstanceState: Bundle?): MastersViewModel {
        return ViewModelProvider(this, providerFactory).get(MastersViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerMastersFeatureComponent
            .builder()
            .mastersFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onStartObservingViewModel(viewModel: MastersViewModel) {

    }

    companion object {
        fun newInstance() = MastersFragment()
    }
}
