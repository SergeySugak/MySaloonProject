package com.app.feature_services.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.app.feature_services.R
import com.app.feature_services.di.DaggerServicesFeatureComponent
import com.app.feature_services.models.ServicesAdapter
import com.app.msa_nav_api.navigation.AppNavigator
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject

class ServicesFragment : MSFragment<ServicesViewModel>() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    @Inject
    lateinit var appNavigator: AppNavigator
        protected set

    @Inject
    lateinit var servicesAdapter: ServicesAdapter
        protected set

    override val layoutId = R.layout.services_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerServicesFeatureComponent
            .builder()
            .servicesFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val servicesList = view.findViewById<RecyclerView>(R.id.services_list)
        servicesList.adapter = servicesAdapter as RecyclerView.Adapter<*>
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

    override fun onHiddenChanged(hidden: Boolean) {

    }

    companion object {
        fun newInstance() = ServicesFragment()
    }
}
