package com.app.feature_master.ui

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.text.TextUtils
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.feature_master.R
import com.app.feature_master.di.DaggerMasterFeatureComponent
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSDialogFragment
import com.app.mscorebase.ui.dialogs.choicedialog.OnChoiceItemsSelectedListener
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import com.app.mscoremodels.saloon.ChoosableSaloonService
import com.app.mscoremodels.saloon.SaloonService
import javax.inject.Inject

class MasterFragment : MSDialogFragment<MasterFragmentViewModel>() {
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    private lateinit var masterServices: EditText

    override val layoutId = R.layout.fragment_master

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerMasterFeatureComponent
            .builder()
            .masterFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onBuildDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(layoutId, null)
        val builder = AlertDialog.Builder(activity!!)
        builder.setView(view)
            .setTitle(R.string.title_fragment_edit_master)
            .setPositiveButton(getString(R.string.ok)) { _, _ -> }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                dialog?.dismiss()
            }
        masterServices = view.findViewById(R.id.master_services)
        masterServices.setOnClickListener{
            val fragment = MasterServicesSelectionDialog.newInstance(getString(R.string.str_master_services),
                getViewModel()?.masterId,
                getViewModel()?.masterServices?.value ?: emptyList(),
                object: OnChoiceItemsSelectedListener<ChoosableSaloonService, String?> {
                    override fun onChoiceItemsSelected(selections: List<ChoosableSaloonService>, payload: String?){
                        getViewModel()?.setMasterServices(selections)
                    }
                    override fun onNoItemSelected(payload: String?) {}
                    override fun writeToParcel(dest: Parcel?, flags: Int) {}
                    override fun describeContents(): Int {return 0}
                })
            showDialogFragment(fragment, "")
        }
        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        val dlg = dialog as AlertDialog?
        if (dlg != null){
            val ok = dlg.getButton(Dialog.BUTTON_POSITIVE)
            ok.setOnClickListener{ _ ->
                getViewModel()?.saveMasterInfo(
                    dlg.findViewById<EditText>(R.id.master_name)?.text.toString(),
                    dlg.findViewById<EditText>(R.id.master_description)?.text.toString(),
                    dlg.findViewById<EditText>(R.id.master_portfolio_url)?.text.toString())
            }
        }
    }

    override fun createViewModel(savedInstanceState: Bundle?): MasterFragmentViewModel {
        return ViewModelProvider(this, providerFactory).get(MasterFragmentViewModel::class.java)
    }

    override fun onViewModelCreated(viewModel: MasterFragmentViewModel, savedInstanceState: Bundle?) {
        super.onViewModelCreated(viewModel, savedInstanceState)
        val serviceId = arguments?.get(ARG_EDIT_MASTER_ID)?.toString()
        if (!TextUtils.isEmpty(serviceId)) {
            viewModel.loadData(serviceId!!)
        }
    }

    override fun onStartObservingViewModel(viewModel: MasterFragmentViewModel) {
        viewModel.error.observe(this, Observer { error ->
            if (!viewModel.error.isHandled){
                MessageDialogFragment.showError(this, error, false)
                viewModel.error.isHandled = true
            }
        })

        viewModel.masterInfoSaveState.observe(this, Observer {
            if (!viewModel.masterInfoSaveState.isHandled) {
                if (it) {
                    val intent = Intent()
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    dismiss()
                }
                viewModel.masterInfoSaveState.isHandled = true
            }
        })

        viewModel.masterInfo.observe(this, Observer { service ->
            if (!viewModel.masterInfo.isHandled){
                dialog?.findViewById<EditText>(R.id.master_name)?.setText(service.name)
                dialog?.findViewById<EditText>(R.id.master_description)?.setText(service.description)
                dialog?.findViewById<EditText>(R.id.master_portfolio_url)?.setText(service.portfolioUrl)
                viewModel.masterInfo.isHandled = true
            }
        })

        viewModel.masterServices.observe(this, Observer { services ->
            if (!viewModel.masterServices.isHandled){
                if (::masterServices.isInitialized){
                    masterServices.setText(services.joinToString())
                    viewModel.masterServices.isHandled = true
                }
            }
        })
    }

    companion object {
        fun newInstance() = MasterFragment()

        const val ARG_EDIT_MASTER_ID = "ARG_EDIT_MASTER_ID"
    }
}
