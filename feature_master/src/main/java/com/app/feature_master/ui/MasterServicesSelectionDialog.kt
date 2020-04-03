package com.app.feature_master.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.feature_master.di.DaggerMasterFeatureComponent
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.dialogs.choicedialog.MSChoiceDialogFragment
import com.app.mscorebase.ui.dialogs.choicedialog.OnChoiceItemsSelectedListener
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import com.app.mscoremodels.saloon.ChoosableSaloonService
import com.app.mscoremodels.saloon.SaloonService
import javax.inject.Inject

class MasterServicesSelectionDialog: MSChoiceDialogFragment<ChoosableSaloonService, MasterServicesSelectionDialogViewModel, String?>() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    val masterServices = mutableListOf<SaloonService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerMasterFeatureComponent
            .builder()
            .masterFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun createViewModel(savedInstanceState: Bundle?): MasterServicesSelectionDialogViewModel {
        return ViewModelProvider(this, providerFactory).get(MasterServicesSelectionDialogViewModel::class.java)
    }

    override fun onViewModelCreated(
        viewModel: MasterServicesSelectionDialogViewModel,
        savedInstanceState: Bundle?
    ) {
        super.onViewModelCreated(viewModel, savedInstanceState)
        viewModel.setMasterServices(masterServices)
        viewModel.loadServices()
    }

    override fun onStartObservingViewModel(viewModel: MasterServicesSelectionDialogViewModel) {
        super.onStartObservingViewModel(viewModel)
        viewModel.error.observe(this, Observer { error ->
            if (!viewModel.error.isHandled){
                MessageDialogFragment.showError(this, error, false)
                viewModel.error.isHandled = true
            }
        })
    }

    companion object {
        fun newInstance(title: String, masterId: String?, masterServices: List<SaloonService>,
                        resultListener: OnChoiceItemsSelectedListener<ChoosableSaloonService, String?>): MasterServicesSelectionDialog {
            val args = Bundle();
            val fragment = MasterServicesSelectionDialog()
            fragment.retainInstance = true
            args.putString(ARGUMENT_PAYLOAD, masterId)
            args.putString(ARGUMENT_TITLE, title)
            args.putParcelable(ARGUMENT_RESULT_LISTENER, resultListener)
            fragment.arguments = args
            fragment.masterServices.clear()
            fragment.masterServices.addAll(masterServices)
            return fragment;
        }
    }
}