package com.app.feature_event_scheduler.ui

import android.app.Dialog
import android.os.Bundle
import android.os.Parcel
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.app.feature_event_scheduler.R
import com.app.feature_event_scheduler.di.DaggerEventSchedulerFeatureComponent
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSDialogFragment
import com.app.mscorebase.ui.MSFragment
import com.app.mscorebase.ui.dialogs.choicedialog.OnChoiceItemsSelectedListener
import com.app.mscoremodels.saloon.ChoosableSaloonService
import javax.inject.Inject

class EventSchedulerFragment : MSDialogFragment<EventSchedulerViewModel>() {
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    override val layoutId =
        R.layout.fragment_event_scheduler

    private lateinit var viewModel: EventSchedulerViewModel

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
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)
            .setTitle(getString(R.string.title_edit_scheduler_event))
            .setPositiveButton(getString(R.string.ok)) { _, _ -> }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                dialog?.dismiss()
            }
//        masterServices = view.findViewById(R.id.master_services)
//        masterServices.setOnClickListener{
//            val fragment = MasterServicesSelectionDialog.newInstance(getString(R.string.str_master_services),
//                getViewModel()?.masterId,
//                getViewModel()?.masterServices?.value ?: emptyList(),
//                object: OnChoiceItemsSelectedListener<ChoosableSaloonService, String?> {
//                    override fun onChoiceItemsSelected(selections: List<ChoosableSaloonService>, payload: String?){
//                        getViewModel()?.setMasterServices(selections)
//                    }
//                    override fun onNoItemSelected(payload: String?) {}
//                    override fun writeToParcel(dest: Parcel?, flags: Int) {}
//                    override fun describeContents(): Int {return 0}
//                })
//            showDialogFragment(fragment, "")
//        }
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

    override fun createViewModel(savedInstanceState: Bundle?): EventSchedulerViewModel {
        return ViewModelProvider(this, providerFactory).get(EventSchedulerViewModel::class.java)
    }

    override fun onStartObservingViewModel(viewModel: EventSchedulerViewModel) {

    }

    companion object {
        fun newInstance() =
            EventSchedulerFragment()

        const val ARG_EDIT_EVENT_ID = "ARG_EDIT_EVENT_ID"
    }
}
