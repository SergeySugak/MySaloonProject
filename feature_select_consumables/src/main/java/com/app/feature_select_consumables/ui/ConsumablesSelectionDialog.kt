package com.app.feature_select_consumables.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.feature_select_consumables.di.DaggerSelectConsumablesFeatureComponent
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.dialogs.choicedialog.MSChoiceDialogFragment
import com.app.mscorebase.ui.dialogs.choicedialog.OnChoiceItemsSelectedListener
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import com.app.mscoremodels.saloon.ChoosableSaloonConsumable
import com.app.mscoremodels.saloon.ChoosableSaloonService
import com.app.mscoremodels.saloon.SaloonConsumable
import com.app.mscoremodels.saloon.SaloonService
import javax.inject.Inject

class ConsumablesSelectionDialog :
    MSChoiceDialogFragment<ChoosableSaloonConsumable, ConsumablesSelectionDialogViewModel, String?>() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    val consumables = mutableListOf<SaloonConsumable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerSelectConsumablesFeatureComponent
            .builder()
            .selectConsumablesFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun createViewModel(savedInstanceState: Bundle?) =ViewModelProvider(
            this,
            providerFactory
        ).get(ConsumablesSelectionDialogViewModel::class.java)

    override fun onViewModelCreated(
        viewModel: ConsumablesSelectionDialogViewModel,
        savedInstanceState: Bundle?
    ) {
        super.onViewModelCreated(viewModel, savedInstanceState)
        viewModel.setConsumables(consumables)
        viewModel.loadConsumables()
    }

    override fun onStartObservingViewModel(viewModel: ConsumablesSelectionDialogViewModel) {
        super.onStartObservingViewModel(viewModel)
        viewModel.error.observe(this, Observer { error ->
            if (!viewModel.error.isHandled) {
                MessageDialogFragment.showError(this, error, false)
                viewModel.error.isHandled = true
            }
        })
    }

    companion object {
        fun newInstance(
            title: String, masterServices: List<SaloonConsumable>,
            resultListener: OnChoiceItemsSelectedListener<ChoosableSaloonConsumable, String?>
        ): ConsumablesSelectionDialog {
            val args = Bundle();
            val fragment =
                ConsumablesSelectionDialog()
            fragment.retainInstance = true
            args.putString(ARGUMENT_TITLE, title)
            args.putParcelable(ARGUMENT_RESULT_LISTENER, resultListener)
            fragment.arguments = args
            fragment.consumables.clear()
            fragment.consumables.addAll(masterServices)
            return fragment;
        }
    }
}