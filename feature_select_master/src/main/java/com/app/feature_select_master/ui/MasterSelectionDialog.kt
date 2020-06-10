package com.app.feature_select_master.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.feature_select_master.di.DaggerSelectMasterFeatureComponent
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.dialogs.choicedialog.MSChoiceDialogFragment
import com.app.mscorebase.ui.dialogs.choicedialog.OnChoiceItemsSelectedListener
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import com.app.mscoremodels.saloon.ChoosableSaloonMaster
import com.app.mscoremodels.saloon.SaloonService
import javax.inject.Inject

class MasterSelectionDialog: MSChoiceDialogFragment<ChoosableSaloonMaster, MasterSelectionDialogViewModel, String?>() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerSelectMasterFeatureComponent
            .builder()
            .selectMasterFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun createViewModel(savedInstanceState: Bundle?): MasterSelectionDialogViewModel {
        return ViewModelProvider(this, providerFactory).get(MasterSelectionDialogViewModel::class.java)
    }

    override fun onViewModelCreated(
        viewModel: MasterSelectionDialogViewModel,
        savedInstanceState: Bundle?
    ) {
        super.onViewModelCreated(viewModel, savedInstanceState)
        if (arguments != null) {
            viewModel.setSelectedMasterId(requireArguments().getString(ARGUMENT_PAYLOAD))
            viewModel.setRequiredServices(requireArguments().getParcelableArrayList<SaloonService>(
                ARGUMENT_REQUIRED_SERVICES)?.toList().orEmpty())
        }
        viewModel.loadMasters()
    }

    override fun onStartObservingViewModel(viewModel: MasterSelectionDialogViewModel) {
        super.onStartObservingViewModel(viewModel)
        viewModel.error.observe(this, Observer { error ->
            if (!viewModel.error.isHandled){
                MessageDialogFragment.showError(this, error, false)
                viewModel.error.isHandled = true
            }
        })
    }

    companion object {
        const val ARGUMENT_REQUIRED_SERVICES = "required_services"

        fun newInstance(title: String, payload: String?, requiredServices: List<SaloonService>,
                        resultListener: OnChoiceItemsSelectedListener<ChoosableSaloonMaster, String?>): MasterSelectionDialog {
            val args = Bundle();
            val fragment =
                MasterSelectionDialog()
            fragment.retainInstance = true
            args.putString(ARGUMENT_PAYLOAD, payload)
            args.putString(ARGUMENT_TITLE, title)
            args.putParcelable(ARGUMENT_RESULT_LISTENER, resultListener)
            args.putParcelableArrayList(ARGUMENT_REQUIRED_SERVICES, ArrayList(requiredServices))
            fragment.arguments = args
            return fragment;
        }
    }
}