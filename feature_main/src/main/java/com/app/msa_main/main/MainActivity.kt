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
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject

class MainActivity : MSActivity<MSActivityViewModel>(), HasComponentDependencies {

    val navView: BottomNavigationView by lazy { findViewById<BottomNavigationView>(R.id.nav_view) }

    private val injector: DaggerMainFeatureComponent by lazy {
        DaggerMainFeatureComponent
        .builder()
        .mainFeatureDependencies(findComponentDependencies())
        .build() as DaggerMainFeatureComponent //Без as DaggerMainFeatureComponent - орет
    }

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
    private lateinit var activeFragment: Fragment
    private val fab: FloatingActionButton by lazy {findViewById<FloatingActionButton>(R.id.fab)}

    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
        super.onCreate(savedInstanceState)
        //Предотвращаем Fragment already added
        if (savedInstanceState == null) {
            installFragments()
        }

        navView.setOnNavigationItemSelectedListener{ item ->
            when (item.itemId){
                R.id.navigation_masters  -> changeActiveFragment(mastersFragment)
                R.id.navigation_services -> changeActiveFragment(servicesFragment)
                R.id.navigation_schedule ->  changeActiveFragment (scheduleFragment)
                else -> false
            }
        }
        changeActiveFragment(mastersFragment)
        navView.selectedItemId = savedInstanceState?.getInt(ID_SELECTED_ITEM_ID, R.id.navigation_masters) ?: R.id.navigation_masters
        setupActionBar()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(ID_SELECTED_ITEM_ID, navView.selectedItemId)
        super.onSaveInstanceState(outState)
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

    private fun changeActiveFragment(fragment: Fragment): Boolean{
        return try {
            val tran = supportFragmentManager.beginTransaction()
            if (::activeFragment.isInitialized) {
                tran.hide(activeFragment)
            }
            tran.show(fragment).commitNow()
            activeFragment = fragment
            setFabBehaviour()
            true
        }
        catch (ex: Exception) {
            MessageDialogFragment.showError(this, ex)
            false
        }
    }

    private fun setFabBehaviour() {
        fab.setOnClickListener {
            when (activeFragment){
                mastersFragment -> mastersFragmentFabAction()
                servicesFragment -> servicesFragmentFabAction()
                scheduleFragment -> scheduleFragmentFabAction()
            }
        }
    }


    private fun mastersFragmentFabAction() {
        appNavigator.navigateToNewMasterFragment(servicesFragment, REQ_NEW_MASTER, NEW_MASTER_FRAGMENT_TAG)
    }

    private fun servicesFragmentFabAction() {
        appNavigator.navigateToNewServiceFragment(servicesFragment, REQ_NEW_SERVICE, NEW_SERVICE_FRAGMENT_TAG)
    }

    private fun scheduleFragmentFabAction() {

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
        getViewModel()?.logout()
        appNavigator.navigateToAuthActivity(this)
        finish()
    }

    companion object {
        const val NEW_MASTER_FRAGMENT_TAG = "NewMasterDialogFragment"
        const val NEW_SERVICE_FRAGMENT_TAG = "NewServiceDialogFragment"
        const val REQ_NEW_MASTER = 10001
        const val REQ_NEW_SERVICE = 10002
        const val REQ_NEW_SCHEDULE = 10003
        val ID_SELECTED_ITEM_ID = MainActivity::class.java.simpleName + "_ID_SELECTED_ITEM_ID"
    }
}
