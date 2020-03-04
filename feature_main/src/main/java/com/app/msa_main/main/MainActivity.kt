package com.app.msa_main.main

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.feature_masters.ui.MastersFragment
import com.app.feature_schedule.ui.ScheduleFragment
import com.app.feature_services.ui.ServicesFragment
import com.app.msa.main.R
import com.app.msa_main.di.DaggerMainFeatureComponent
import com.app.msa_nav_api.navigation.AppNavigator
import com.app.mscorebase.di.ComponentDependenciesProvider
import com.app.mscorebase.di.HasComponentDependencies
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSActivity
import com.app.mscorebase.ui.MSActivityViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject

class MainActivity : MSActivity<MSActivityViewModel>(), HasComponentDependencies {

    //Зависимсоти, которые будут запрашивать фрагменты
    @Inject
    override lateinit var dependencies: ComponentDependenciesProvider
        protected set

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set
    @Inject
    lateinit var appNavigator: AppNavigator
    @Inject
    lateinit var mastersFragment: MastersFragment
    @Inject
    lateinit var servicesFragment: ServicesFragment
    @Inject
    lateinit var scheduleFragment: ScheduleFragment
    lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerMainFeatureComponent
            .builder()
            .mainFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
        //Предотвращаем Fragment already added
        if (savedInstanceState == null) {
            installFragments()
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener{ item ->
            when (item.itemId){
                R.id.navigation_masters -> {
                    changeActiveFragment(mastersFragment)
                    true
                }
                R.id.navigation_services -> {
                    changeActiveFragment(servicesFragment)
                    true
                }
                R.id.navigation_schedule -> {
                    changeActiveFragment (scheduleFragment)
                    true
                }
                else ->
                    false
            }
        }
        activeFragment = mastersFragment
        navView.selectedItemId = R.id.navigation_masters
        setupActionBar()
    }

    private fun installFragments() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, scheduleFragment)
            .hide(scheduleFragment)
            .commit();
        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, servicesFragment)
            .hide(servicesFragment)
            .commit();
        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, mastersFragment)
            .commit();
    }

    private fun changeActiveFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(fragment)
            .commitNow()
        activeFragment = fragment
    }

    override fun createViewModel(savedInstanceState: Bundle?): MSActivityViewModel {
        return ViewModelProvider(this, providerFactory).get(MainActivityViewModel::class.java)
    }

    override fun getLayoutId() = R.layout.main_activity

    override fun onStartObservingViewModel(viewModel: MSActivityViewModel) {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        getViewModel().logout()
        appNavigator.navigateToAuthActivity(this)
        finish()
    }

}
