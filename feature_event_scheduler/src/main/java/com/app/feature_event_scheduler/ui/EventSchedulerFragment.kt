package com.app.feature_event_scheduler.ui

import android.app.Dialog
import android.os.Bundle
import android.os.Parcel
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.feature_event_scheduler.R
import com.app.feature_event_scheduler.api.EventDateTimeReceiver
import com.app.feature_event_scheduler.di.DaggerEventSchedulerFeatureComponent
import com.app.msa_nav_api.navigation.AppNavigator
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSDialogFragment
import com.app.mscorebase.ui.dialogs.choicedialog.OnChoiceItemsSelectedListener
import com.app.mscoremodels.saloon.ChoosableSaloonMaster
import com.app.mscoremodels.saloon.ChoosableSaloonService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class EventSchedulerFragment : MSDialogFragment<EventSchedulerViewModel>(), EventDateTimeReceiver {
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set
    @Inject
    lateinit var appNavigator: AppNavigator
        protected set

    private lateinit var date: EditText
    private lateinit var time: EditText
    private lateinit var services: EditText
    private lateinit var master: EditText

    override val layoutId = R.layout.fragment_event_scheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerEventSchedulerFeatureComponent
            .builder()
            .eventSchedulerFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onBuildDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(layoutId, null)
        val builder = MaterialAlertDialogBuilder(requireActivity())
        builder.setView(view)
            .setTitle(getString(R.string.title_edit_scheduler_event))
            .setPositiveButton(getString(R.string.ok)) { _, _ -> }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                dialog?.dismiss()
            }
        services = view.findViewById(R.id.services)
        services.setOnClickListener{
            appNavigator.navigateToSelectServicesFragment(
                this,
                getString(R.string.str_required_services),
                getViewModel()?.masterId,
                getViewModel()?.masterServices?.value ?: emptyList(),
                object: OnChoiceItemsSelectedListener<ChoosableSaloonService, String?> {
                    override fun onChoiceItemsSelected(selections: List<ChoosableSaloonService>, payload: String?){
                        getViewModel()?.setMasterServices(selections)
                    }
                    override fun onNoItemSelected(payload: String?) {}
                    override fun writeToParcel(dest: Parcel?, flags: Int) {}
                    override fun describeContents(): Int {return 0}
                }
            )
        }
        master = view.findViewById(R.id.master)
        master.setOnClickListener {
            appNavigator.navigateToSelectMasterFragment(
                this,
                getString(R.string.str_master_selection),
                getViewModel()?.masterId,
                getViewModel()?.masterServices?.value ?: emptyList(),
                object: OnChoiceItemsSelectedListener<ChoosableSaloonMaster, String?> {
                    override fun onChoiceItemsSelected(selections: List<ChoosableSaloonMaster>, payload: String?){
                        getViewModel()?.setMaster(selections)
                    }
                    override fun onNoItemSelected(payload: String?) {}
                    override fun writeToParcel(dest: Parcel?, flags: Int) {}
                    override fun describeContents(): Int {return 0}
                }
            )
        }

        date = view.findViewById(R.id.date)
        date.setOnClickListener {
            val dialog = DateSelectionDialog.newInstance(getViewModel()!!.calendar.value!!)
            dialog.setTargetFragment(this, REQ_SET_DATE)
            dialog.show(parentFragmentManager, dialog.javaClass.simpleName)
        }
        time = view.findViewById(R.id.time)
        time.setOnClickListener {
            val dialog = TimeSelectionDialog.newInstance(getViewModel()!!.calendar.value!!)
            dialog.setTargetFragment(this, REQ_SET_TIME)
            dialog.show(parentFragmentManager, dialog.javaClass.simpleName)
        }
        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        val dlg = dialog as AlertDialog?
        if (dlg != null){
            val ok = dlg.getButton(Dialog.BUTTON_POSITIVE)
            ok.setOnClickListener{ _ ->
//                getViewModel()?.saveEventInfo(
//                    dlg.findViewById<EditText>(R.id.master_name)?.text.toString(),
//                    dlg.findViewById<EditText>(R.id.master_description)?.text.toString(),
//                    dlg.findViewById<EditText>(R.id.master_portfolio_url)?.text.toString())
            }
        }
    }

    override fun createViewModel(savedInstanceState: Bundle?) =
        ViewModelProvider(this, providerFactory).get(EventSchedulerViewModel::class.java)

    override fun onStartObservingViewModel(viewModel: EventSchedulerViewModel) {
        viewModel.calendar.observe(this, Observer { calendar ->
            val formatter = SimpleDateFormat(getString(R.string.str_date_format), Locale.getDefault())
            date.setText(formatter.format(calendar.time))
            formatter.applyPattern(getString(R.string.str_time_format))
            time.setText(formatter.format(calendar.time))
        })

        viewModel.masterServices.observe(this, Observer { items ->
            if (!viewModel.masterServices.isHandled){
                if (::services.isInitialized){
                    services.setText(items.joinToString())
                    viewModel.masterServices.isHandled = true
                }
            }
        })

        viewModel.master.observe(this, Observer { item ->
            if (!viewModel.master.isHandled){
                if (::master.isInitialized){
                    master.setText(item.name)
                    viewModel.master.isHandled = true
                }
            }
        })
    }

    override fun setEventDate(year: Int, month: Int, day: Int) {
        getViewModel()?.setEventDate(year, month, day)
    }

    override fun setEventTime(hour: Int, minute: Int) {
        getViewModel()?.setEventTime(hour, minute)
    }

    companion object {
        fun newInstance() =
            EventSchedulerFragment()

        const val ARG_EDIT_EVENT_ID = "ARG_EDIT_EVENT_ID"
        private const val REQ_SET_DATE = 10001
        private const val REQ_SET_TIME = 10002
    }
}
