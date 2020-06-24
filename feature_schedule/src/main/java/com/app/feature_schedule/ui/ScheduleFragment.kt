package com.app.feature_schedule.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.MenuRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.feature_schedule.R
import com.app.feature_schedule.di.DaggerScheduleFeatureComponent
import com.app.msa_nav_api.navigation.AppNavigator
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSFragment
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import com.app.mscorebase.utils.isTablet
import com.app.mscoremodels.saloon.SaloonEvent
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
    @Inject
    lateinit var appNavigator: AppNavigator

    @get:MenuRes
    override val menu = R.menu.schedule_menu

    private val loading: ProgressBar by lazy { requireView().findViewById<ProgressBar>(R.id.loading) }
    private val schedulerView: SchedulerView by lazy { requireView().findViewById<SchedulerView>(R.id.schedule_view) }

    val eventSchedulerListener = object : AppNavigator.EventSchedulerListener{
        override fun onAdded(event: SaloonEvent) {
            getViewModel()?.onEventInserted(event)
        }

        override fun onUpdated(event: SaloonEvent) {
            getViewModel()?.onEventUpdated(event)
        }

        override fun onDeleted(event: SaloonEvent) {
            getViewModel()?.onEventDeleted(event)
        }
    }

    override val layoutId = R.layout.schedule_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerScheduleFeatureComponent
            .builder()
            .scheduleFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun createViewModel(savedInstanceState: Bundle?) =
        ViewModelProvider(this, providerFactory).get(ScheduleViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isTablet(requireContext())){
            schedulerView.fitDays = 1
        }
        if (savedInstanceState == null){
            schedulerView.scrollTo(Calendar.getInstance())
        }
        schedulerView.setSchedulerViewListener(object : SchedulerViewListener {
            override fun onDateTimeRangeChanged(from: Calendar, to: Calendar) {
                getViewModel()?.notifier?.onNext(Pair(from, to))
            }
        })
        val fromDate = schedulerView.getFirstDrawableDateTime()
        val toDate = schedulerView.getLastDrawableDateTime(fromDate)
        getViewModel()?.loadData(fromDate, toDate)
        schedulerView.setEventClickListener(object : SchedulerEventClickListener {
            override fun onSchedulerEventClickListener(event: SchedulerEvent) {
                appNavigator.navigateToEditEventFragment(this@ScheduleFragment, event as SaloonEvent,
                    eventSchedulerListener)
            }
        })
    }

    private fun scrollToNow(){
        val now = Calendar.getInstance()
        val minutes = now.get(Calendar.MINUTE)
        val fraction = resources.getInteger(R.integer.time_fraction)
        now.set(Calendar.MINUTE, (minutes / fraction) * fraction)
        now.set(Calendar.SECOND, 0)
        now.set(Calendar.MILLISECOND, 0)
        schedulerView.scrollTo(now)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_search -> {}
            R.id.action_now -> scrollToNow()
        }
        return super.onOptionsItemSelected(item)
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

        viewModel.newEventsLoaded.observe(this, Observer { events ->
            if (!viewModel.newEventsLoaded.isHandled){
                schedulerView.addEvents(events)
                viewModel.newEventsLoaded.isHandled = true
            }
        })

        viewModel.eventDeleted.observe(this, Observer { event ->
            if (!viewModel.eventDeleted.isHandled){
                schedulerView.removeEvent(event)
                viewModel.eventDeleted.isHandled = true
            }
        })
    }

    override fun onCreateOptionsMenu(optMenu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(optMenu, inflater)
        val searchMenuItem = optMenu.findItem(R.id.action_search)
        val searchView = searchMenuItem.actionView as androidx.appcompat.widget.SearchView
        setupSearch(searchView, searchMenuItem)
    }

    private fun setupSearch(searchView: androidx.appcompat.widget.SearchView,
                            searchMenuItem: MenuItem) {
        searchView.setIconifiedByDefault(true)
        searchView.queryHint = getString(R.string.str_search_for_event)
        searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                queryWithFilter()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                getViewModel()?.setFilter(newText)
                return false
            }
        })
        searchView.setOnCloseListener {
            queryWithFilter()
            false
        }

        val savedFilter = getViewModel()?.getFilter()
        if (savedFilter != null && savedFilter.isNotEmpty()){
            searchMenuItem.expandActionView()
            searchView.setQuery(savedFilter, false)
        }
    }

    private fun queryWithFilter(){
        val fromDate = schedulerView.getFirstDrawableDateTime()
        val toDate = schedulerView.getLastDrawableDateTime(fromDate)
        schedulerView.clearEvents()
        getViewModel()?.applyFilter(fromDate, toDate)
    }

    override fun onHiddenChanged(hidden: Boolean) {

    }

    companion object {
        fun newInstance() = ScheduleFragment()
    }
}
