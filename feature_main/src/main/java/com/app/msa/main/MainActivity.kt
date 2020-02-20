package com.app.msa.main

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.main.R
import com.app.msa.di.DaggerMainFeatureComponent
import com.app.mscorebase.di.ComponentDependenciesProvider
import com.app.mscorebase.di.HasComponentDependencies
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSActivity
import com.app.mscorebase.ui.MSActivityViewModel
import main.java.com.app.msa.main.MainActivityViewModel
import javax.inject.Inject

class MainActivity : MSActivity<MainActivity, MSActivityViewModel>(), HasComponentDependencies {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set
    @Inject
    override lateinit var dependencies: ComponentDependenciesProvider
        protected set

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerMainFeatureComponent
            .builder()
            .mainFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)

if (savedInstanceState == null) {
    supportFragmentManager.beginTransaction()
        .replace(R.id.container, com.app.msa.masters.MastersFragment.newInstance())
        .commitNow()
}
    }

    override fun createViewModel(savedInstanceState: Bundle?): MSActivityViewModel {
        return ViewModelProvider(this, providerFactory).get(MainActivityViewModel::class.java)
    }

    override fun getLayoutId() = R.layout.main_activity

    override fun getThis() = this

    override fun onStartObservingViewModel(viewModel: MSActivityViewModel) {

    }
}
