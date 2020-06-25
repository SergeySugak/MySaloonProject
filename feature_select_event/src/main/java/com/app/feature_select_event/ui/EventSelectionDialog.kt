package com.app.feature_select_event.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.feature_select_event.di.DaggerSelectEventFeatureComponent
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.dialogs.choicedialog.MSChoiceDialogFragment
import com.app.mscorebase.ui.dialogs.choicedialog.OnChoiceItemsSelectedListener
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import com.app.mscoremodels.saloon.ChoosableSaloonEvent
import javax.inject.Inject

class EventSelectionDialog :
    MSChoiceDialogFragment<ChoosableSaloonEvent, EventSelectionDialogViewModel, String?>() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerSelectEventFeatureComponent
            .builder()
            .selectEventFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun createViewModel(savedInstanceState: Bundle?) =
        ViewModelProvider(this, providerFactory).get(EventSelectionDialogViewModel::class.java)

    override fun onViewModelCreated(
        viewModel: EventSelectionDialogViewModel,
        savedInstanceState: Bundle?
    ) {
        super.onViewModelCreated(viewModel, savedInstanceState)
        viewModel.loadEvents(requireArguments().getString(ARGUMENT_FILTER)!!)
    }

    override fun onStartObservingViewModel(viewModel: EventSelectionDialogViewModel) {
        super.onStartObservingViewModel(viewModel)
        viewModel.error.observe(this, Observer { error ->
            if (!viewModel.error.isHandled) {
                MessageDialogFragment.showError(this, error, false)
                viewModel.error.isHandled = true
            }
        })
    }

    companion object {
        private const val ARGUMENT_FILTER = "filter"

        fun newInstance(
            title: String, filter: String, payload: String?,
            resultListener: OnChoiceItemsSelectedListener<ChoosableSaloonEvent, String?>
        ): EventSelectionDialog {
            val args = Bundle();
            val fragment = EventSelectionDialog()
            fragment.retainInstance = true
            args.putString(ARGUMENT_PAYLOAD, payload)
            args.putString(ARGUMENT_TITLE, title)
            args.putString(ARGUMENT_FILTER, filter)
            args.putParcelable(ARGUMENT_RESULT_LISTENER, resultListener)
            fragment.arguments = args
            return fragment;
        }
    }
}