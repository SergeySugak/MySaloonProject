package com.app.feature_newservice.ui

import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.ViewModelProvider
import com.app.feature_newservice.di.DaggerNewServiceFeatureComponent
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.dialogs.choicedialog.MSChoiceDialogFragment
import com.app.mscorebase.ui.dialogs.choicedialog.OnChoiceItemsSelectedListener
import com.app.mscoremodels.saloon.ChoosableServiceDuration
import javax.inject.Inject

class ServiceDurationSelectionDialog: MSChoiceDialogFragment<ChoosableServiceDuration, ServiceDurationSelectionDialogViewModel>() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerNewServiceFeatureComponent
            .builder()
            .newServiceFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun createViewModel(savedInstanceState: Bundle?): ServiceDurationSelectionDialogViewModel {
        return ViewModelProvider(this, providerFactory).get(ServiceDurationSelectionDialogViewModel::class.java)
    }

    override fun onStartObservingViewModel(viewModel: ServiceDurationSelectionDialogViewModel) {

    }

    companion object {
        fun newInstance(title: String, payload: Parcelable?,
                        resultListener: OnChoiceItemsSelectedListener<ChoosableServiceDuration, Parcelable?>): ServiceDurationSelectionDialog {
            val args = Bundle();
            val fragment = ServiceDurationSelectionDialog();
            args.putParcelable(ARGUMENT_PAYLOAD, payload);
            args.putString(ARGUMENT_TITLE, title);
            fragment.arguments = args;
            fragment.setResultListener(resultListener);
            return fragment;
        }
    }
}