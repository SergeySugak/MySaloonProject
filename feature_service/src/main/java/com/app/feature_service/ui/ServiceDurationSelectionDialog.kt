package com.app.feature_service.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.feature_service.di.DaggerServiceFeatureComponent
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.dialogs.choicedialog.MSChoiceDialogFragment
import com.app.mscorebase.ui.dialogs.choicedialog.OnChoiceItemsSelectedListener
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import com.app.mscoremodels.saloon.ChoosableServiceDuration
import javax.inject.Inject

class ServiceDurationSelectionDialog: MSChoiceDialogFragment<ChoosableServiceDuration, ServiceDurationSelectionDialogViewModel, Int?>() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerServiceFeatureComponent
            .builder()
            .serviceFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun createViewModel(savedInstanceState: Bundle?): ServiceDurationSelectionDialogViewModel {
        return ViewModelProvider(this, providerFactory).get(ServiceDurationSelectionDialogViewModel::class.java)
    }

    override fun onViewModelCreated(
        viewModel: ServiceDurationSelectionDialogViewModel,
        savedInstanceState: Bundle?
    ) {
        super.onViewModelCreated(viewModel, savedInstanceState)
        viewModel.getServiceDurations(arguments?.getInt(ARGUMENT_PAYLOAD) ?: -1)
    }

    override fun onStartObservingViewModel(viewModel: ServiceDurationSelectionDialogViewModel) {
        super.onStartObservingViewModel(viewModel)
        viewModel.error.observe(this, Observer { error ->
            if (!viewModel.error.isHandled){
                MessageDialogFragment.showError(this, error, false)
                viewModel.error.isHandled = true
            }
        })
    }

    companion object {
        fun newInstance(title: String, durationId: Int?,
                        resultListener: OnChoiceItemsSelectedListener<ChoosableServiceDuration, Int?>): ServiceDurationSelectionDialog {
            val args = Bundle();
            val fragment = ServiceDurationSelectionDialog()
            fragment.retainInstance = true
            if (durationId != null)
                args.putInt(ARGUMENT_PAYLOAD, durationId)
            else
                args.putInt(ARGUMENT_PAYLOAD, -1)
            args.putString(ARGUMENT_TITLE, title)
            fragment.arguments = args
            fragment.setResultListener(resultListener)
            return fragment
        }
    }
}