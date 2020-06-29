package com.app.feature_event_scheduler.ui

import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcel
import android.view.View
import android.view.View.VISIBLE
import android.widget.CheckedTextView
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.feature_event_scheduler.R
import com.app.feature_event_scheduler.api.EventDateTimeReceiver
import com.app.feature_event_scheduler.di.DaggerEventSchedulerFeatureComponent
import com.app.msa_nav_api.navigation.AppNavigator
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSBottomSheetDialogFragment
import com.app.mscorebase.ui.dialogs.choicedialog.OnChoiceItemsSelectedListener
import com.app.mscorebase.ui.dialogs.messagedialog.DialogFragmentPresenter
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import com.app.mscorebase.utils.hideKeyboard
import com.app.mscoremodels.saloon.ActionType
import com.app.mscoremodels.saloon.ChoosableSaloonMaster
import com.app.mscoremodels.saloon.ChoosableSaloonService
import com.app.mscoremodels.saloon.SaloonEvent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class EventSchedulerFragment : MSBottomSheetDialogFragment<EventSchedulerViewModel>(),
    EventDateTimeReceiver {
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    @Inject
    lateinit var appNavigator: AppNavigator
        protected set

    private val date: EditText by lazy { requireView().findViewById<EditText>(R.id.date) }
    private val time: EditText by lazy { requireView().findViewById<EditText>(R.id.time) }
    private val services: EditText by lazy { requireView().findViewById<EditText>(R.id.services) }
    private val planDuration: EditText by lazy { requireView().findViewById<EditText>(R.id.plan_time) }
    private val planAmount: EditText by lazy { requireView().findViewById<EditText>(R.id.plan_amount) }
    private val factDuration: EditText by lazy { requireView().findViewById<EditText>(R.id.fact_time) }
    private val factAmount: EditText by lazy { requireView().findViewById<EditText>(R.id.fact_amount) }
    private val master: EditText by lazy { requireView().findViewById<EditText>(R.id.master) }
    private val toolBar: Toolbar by lazy { requireView().findViewById<Toolbar>(R.id.toolbar) }

    override val layoutId = R.layout.fragment_event_scheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerEventSchedulerFeatureComponent
            .builder()
            .eventSchedulerFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupServices(services)
        setupMaster(master)
        setupDate(date)
        setupTime(time)
        setupMenu()
        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
        if (savedInstanceState == null){
            hideKeyboard(view.findViewById(R.id.client_name))
        }
    }

    private fun setupMenu() {
        val event = requireArguments()[ARG_EDIT_EVENT] as SaloonEvent?
        toolBar.inflateMenu(R.menu.event_edit_menu)
        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.setNavigationOnClickListener {
            dismiss()
        }
        toolBar.menu.findItem(R.id.menu_ok).setOnMenuItemClickListener { _ ->
            saveEvent(event)
            true
        }
        toolBar.menu.findItem(R.id.menu_delete).isVisible = event != null
        toolBar.menu.findItem(R.id.menu_delete).setOnMenuItemClickListener {
            MessageDialogFragment.showMessage(
                this, R.string.title_warning, R.string.str_confirm_delete_event,
                DialogFragmentPresenter.ICON_WARNING, REQ_DELETE_EVENT,
                DialogFragmentPresenter.TWO_BUTTONS_YN
            )
            true
        }
    }

    private fun setupServices(services: EditText) {
        //val services = view.findViewById<EditText>(R.id.services)
        services.setOnClickListener {
            appNavigator.navigateToSelectServicesFragment(
                this,
                getString(R.string.str_required_services),
                getViewModel()?.masterId,
                getViewModel()?.services?.value ?: emptyList(),
                object : OnChoiceItemsSelectedListener<ChoosableSaloonService, String?> {
                    override fun onChoiceItemsSelected(
                        selections: List<ChoosableSaloonService>,
                        payload: String?
                    ) {
                        getViewModel()?.setServices(selections)
                    }

                    override fun onNoItemSelected(payload: String?) {}
                    override fun writeToParcel(dest: Parcel?, flags: Int) {}
                    override fun describeContents(): Int {
                        return 0
                    }
                }
            )
        }
    }

    private fun setupMaster(master: EditText) {
        master.setOnClickListener {
            appNavigator.navigateToSelectMasterFragment(
                this,
                getString(R.string.str_master_selection),
                getViewModel()?.masterId,
                getViewModel()?.services?.value ?: emptyList(),
                object : OnChoiceItemsSelectedListener<ChoosableSaloonMaster, String?> {
                    override fun onChoiceItemsSelected(
                        selections: List<ChoosableSaloonMaster>,
                        payload: String?
                    ) {
                        getViewModel()?.setMaster(selections)
                    }

                    override fun onNoItemSelected(payload: String?) {}
                    override fun writeToParcel(dest: Parcel?, flags: Int) {}
                    override fun describeContents(): Int {
                        return 0
                    }
                }
            )
        }
    }

    private fun setupDate(date: EditText) {
        date.setOnClickListener {
            val dialog = DateSelectionDialog.newInstance(getViewModel()!!.calendar.value!!)
            dialog.setTargetFragment(this, REQ_SET_DATE)
            dialog.show(parentFragmentManager, dialog.javaClass.simpleName)
        }
    }

    private fun setupTime(time: EditText) {
        time.setOnClickListener {
            val dialog = TimeSelectionDialog.newInstance(getViewModel()!!.calendar.value!!)
            dialog.setTargetFragment(this, REQ_SET_TIME)
            dialog.show(parentFragmentManager, dialog.javaClass.simpleName)
        }
    }

    private fun saveEvent(event: SaloonEvent?) {
        val clientName = requireView().findViewById<EditText>(R.id.client_name)
        val clientPhone = requireView().findViewById<EditText>(R.id.client_phone)
        val clientEmail = requireView().findViewById<EditText>(R.id.client_email)
        getViewModel()?.setClientInfo(
            clientName?.text.toString() ?: "",
            clientPhone?.text.toString() ?: "", clientEmail?.text.toString() ?: ""
        )
        val action = if (event != null) ActionType.EDIT else ActionType.ADD
        getViewModel()?.setDescription(requireView().findViewById<EditText>(R.id.services)?.text.toString())
        getViewModel()?.setNotes(requireView().findViewById<EditText>(R.id.notes).text.toString())
        getViewModel()?.setDone(requireView().findViewById<CheckedTextView>(R.id.done).isChecked)
        getViewModel()?.saveEventInfo(action)
    }

    override fun createViewModel(savedInstanceState: Bundle?) =
        ViewModelProvider(this, providerFactory).get(EventSchedulerViewModel::class.java)

    override fun onViewModelCreated(
        viewModel: EventSchedulerViewModel,
        savedInstanceState: Bundle?
    ) {
        super.onViewModelCreated(viewModel, savedInstanceState)
        viewModel.setEvent(requireArguments()[ARG_EDIT_EVENT] as SaloonEvent?)
    }

    override fun onStartObservingViewModel(viewModel: EventSchedulerViewModel) {
        viewModel.error.observe(this, Observer { error ->
            if (!viewModel.error.isHandled) {
                MessageDialogFragment.showError(this, error, false)
                viewModel.error.isHandled = true
            }
        })

        viewModel.calendar.observe(this, Observer { calendar ->
            formatDateTime(calendar)
        })

        viewModel.services.observe(this, Observer { items ->
            if (!viewModel.services.isHandled) {
                services.setText(items.joinToString())
                var minutes = viewModel.getTotalServicesPlanDuration(items)
                var format = getString(R.string.str_hm_format)
                val hours = minutes / 60
                minutes -= hours * 60
                if (hours == 0 && minutes > 0){
                    format = getString(R.string.str_m_format)
                    planDuration.setText(String.format(format, minutes))
                }
                else {
                    if (hours > 0 && minutes == 0){
                        format = getString(R.string.str_h_format)
                        planDuration.setText(String.format(format, hours))
                    }
                    else {
                        planDuration.setText(String.format(format, hours, minutes))
                    }
                }
                planAmount.setText(String.format("%.2f", viewModel.getTotalServicesPlanAmount(items)))
                viewModel.services.isHandled = true
            }
        })

        viewModel.master.observe(this, Observer { item ->
            if (!viewModel.master.isHandled) {
                master.setText(item.name)
                viewModel.master.isHandled = true
            }
        })

        viewModel.eventInfoSaveState.observe(this, Observer {
            if (!viewModel.eventInfoSaveState.isHandled) {
                val listener =
                    requireArguments()[ARG_EVENT_LISTENER] as AppNavigator.EventSchedulerListener?
                val event = viewModel.eventInfo.value
                if (listener != null && event != null) {
                    when (it) {
                        ActionType.ADD -> listener.onAdded(event)
                        ActionType.EDIT -> listener.onUpdated(event)
                        ActionType.DELETE -> listener.onDeleted(event)
                        else -> {
                        }
                    }
                    dismiss()
                }
                viewModel.eventInfoSaveState.isHandled = true
            }
        })

        viewModel.eventInfo.observe(this, Observer { event ->
            if (!viewModel.eventInfo.isHandled) {
                if (event != null) {
                    dialog?.findViewById<EditText>(R.id.client_name)?.setText(event.client.name)
                    dialog?.findViewById<EditText>(R.id.client_phone)?.setText(event.client.phone)
                    dialog?.findViewById<EditText>(R.id.client_email)?.setText(event.client.email)
                    dialog?.findViewById<EditText>(R.id.notes)?.setText(event.notes)
                    val done = dialog?.findViewById<CheckedTextView>(R.id.done)
                    if (done != null) {
                        done.visibility = VISIBLE
                        done.isChecked = viewModel.getDone()
                        done.setOnClickListener {
                            done.isChecked = !done.isChecked
                            viewModel.setDone(done.isChecked)
                        }
                    }
                }
                viewModel.eventInfo.isHandled = true
            }
        })
    }

    private fun formatDateTime(calendar: Calendar) {
        val formatter = SimpleDateFormat(getString(R.string.str_date_format), Locale.getDefault())
        date.setText(formatter.format(calendar.time))
        formatter.applyPattern(getString(R.string.str_time_format))
        time.setText(formatter.format(calendar.time))
    }

    override fun setEventDate(year: Int, month: Int, day: Int) {
        getViewModel()?.setEventDate(year, month, day)
    }

    override fun setEventTime(hour: Int, minute: Int) {
        getViewModel()?.setEventTime(hour, minute)
    }

    override fun onClickDialogButton(
        dialog: DialogInterface?,
        whichButton: Int,
        requestCode: Int,
        params: Bundle?
    ) {
        when (requestCode) {
            REQ_DELETE_EVENT -> if (whichButton == DialogInterface.BUTTON_POSITIVE) {
                getViewModel()?.saveEventInfo(ActionType.DELETE)
            }
        }
        super.onClickDialogButton(dialog, whichButton, requestCode, params)
    }

    companion object {
        const val ARG_EDIT_EVENT = "ARG_EDIT_EVENT"
        const val ARG_EVENT_LISTENER = "ARG_EVENT_LISTENER"
        private const val REQ_SET_DATE = 10001
        private const val REQ_SET_TIME = 10002
        private const val REQ_DELETE_EVENT = 10002

        fun newInstance(
            event: SaloonEvent?,
            eventListener: AppNavigator.EventSchedulerListener?
        ): EventSchedulerFragment {
            val result = EventSchedulerFragment()
            result.arguments = bundleOf(
                Pair(ARG_EDIT_EVENT, event),
                Pair(ARG_EVENT_LISTENER, eventListener)
            )
            return result
        }
    }
}
