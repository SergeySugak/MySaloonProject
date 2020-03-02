package com.app.feature_services.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView

import com.app.feature_services.R
import com.app.feature_services.di.DaggerServicesFeatureComponent
import com.app.feature_services.models.ServicesAdapter
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSFragment
import kotlinx.android.synthetic.main.services_fragment.*
import javax.inject.Inject

class ServicesFragment : MSFragment<ServicesViewModel>() {

    companion object {
        fun newInstance() = ServicesFragment()
    }

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    @Inject
    lateinit var servicesAdapter: ServicesAdapter

    override val layoutId = R.layout.services_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerServicesFeatureComponent
            .builder()
            .servicesFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
        services_list.adapter = servicesAdapter as RecyclerView.Adapter<*>
    }

    override fun createViewModel(savedInstanceState: Bundle?): ServicesViewModel {
        return ViewModelProvider(this, providerFactory).get(ServicesViewModel::class.java)
    }

    override fun onViewModelCreated(viewModel: ServicesViewModel, savedInstanceState: Bundle?) {
        super.onViewModelCreated(viewModel, savedInstanceState)
        viewModel.loadData()
    }

    override fun onStartObservingViewModel(viewModel: ServicesViewModel) {

    }
}
