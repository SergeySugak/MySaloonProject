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
import com.app.mscoremodels.saloon.*
import javax.inject.Inject

class ConsumablesSelectionDialog :
    MSChoiceDialogFragment<ChoosableSaloonConsumable, ConsumablesSelectionDialogViewModel, String?>() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    val usedConsumables = mutableListOf<SaloonUsedConsumable>()

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
        viewModel.setConsumables(usedConsumables)
        viewModel.loadChoosableConsumables()
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
        fun newInstance(title: String, consumables: List<SaloonUsedConsumable>,
            resultListener: OnChoiceItemsSelectedListener<ChoosableSaloonConsumable, String?>
        ): ConsumablesSelectionDialog {
            val args = Bundle()
            val fragment = ConsumablesSelectionDialog()
            fragment.retainInstance = true
            args.putString(ARGUMENT_TITLE, title)
            args.putParcelable(ARGUMENT_RESULT_LISTENER, resultListener)
            fragment.arguments = args
            fragment.usedConsumables.clear()
            fragment.usedConsumables.addAll(consumables)
            return fragment;
        }
    }
}