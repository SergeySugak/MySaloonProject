package com.app.feature_service.ui

import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.feature_service.di.DaggerServiceFeatureComponent
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.dialogs.choicedialog.ChoiceMode
import com.app.mscorebase.ui.dialogs.choicedialog.MSChoiceDialogFragment
import com.app.mscorebase.ui.dialogs.choicedialog.MSChoiceDialogFragment.Companion.ARGUMENT_PAYLOAD
import com.app.mscorebase.ui.dialogs.choicedialog.MSChoiceDialogFragment.Companion.ARGUMENT_TITLE
import com.app.mscorebase.ui.dialogs.choicedialog.OnChoiceItemsSelectedListener
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import com.app.mscoremodels.saloon.ChoosableServiceDuration
import javax.inject.Inject

class ServiceDurationSelectionDialog: MSChoiceDialogFragment<ChoosableServiceDuration, ServiceDurationSelectionDialogViewModel, String?>() {

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

    override fun onStartObservingViewModel(viewModel: ServiceDurationSelectionDialogViewModel) {
        viewModel.error.observe(this, Observer { error ->
            if (!viewModel.error.isHandled){
                MessageDialogFragment.showError(this, error, false)
                viewModel.error.isHandled = true
            }
        })
    }

    companion object {
        fun newInstance(title: String, payload: String?,
                        resultListener: OnChoiceItemsSelectedListener<ChoosableServiceDuration, String?>): ServiceDurationSelectionDialog {
            val args = Bundle();
            val fragment = ServiceDurationSelectionDialog();
            args.putString(ARGUMENT_PAYLOAD, payload);
            args.putString(ARGUMENT_TITLE, title);
            fragment.arguments = args;
            fragment.setResultListener(resultListener);
            return fragment;
        }
    }
}