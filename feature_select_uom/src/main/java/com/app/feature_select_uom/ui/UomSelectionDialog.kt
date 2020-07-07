package com.app.feature_select_uom.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.feature_select_uom.di.DaggerSelectUomFeatureComponent
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.dialogs.choicedialog.MSChoiceDialogFragment
import com.app.mscorebase.ui.dialogs.choicedialog.OnChoiceItemsSelectedListener
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import com.app.mscoremodels.saloon.ChoosableUom
import javax.inject.Inject

class UomSelectionDialog :
    MSChoiceDialogFragment<ChoosableUom, UomSelectionDialogViewModel, String?>() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerSelectUomFeatureComponent
            .builder()
            .selectUomFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun createViewModel(savedInstanceState: Bundle?) =
        ViewModelProvider(this, providerFactory).get(UomSelectionDialogViewModel::class.java)

    override fun onViewModelCreated(
        viewModel: UomSelectionDialogViewModel,
        savedInstanceState: Bundle?
    ) {
        super.onViewModelCreated(viewModel, savedInstanceState)
        viewModel.getUoms(arguments?.getString(ARGUMENT_PAYLOAD) ?: "")
    }

    override fun onStartObservingViewModel(viewModel: UomSelectionDialogViewModel) {
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
            title: String, uom: String?,
            resultListener: OnChoiceItemsSelectedListener<ChoosableUom, String?>
        ): UomSelectionDialog {
            val args = Bundle();
            val fragment = UomSelectionDialog()
            fragment.retainInstance = true
            args.putString(ARGUMENT_PAYLOAD, uom)
            args.putString(ARGUMENT_TITLE, title)
            args.putParcelable(ARGUMENT_RESULT_LISTENER, resultListener)
            fragment.arguments = args
            return fragment;
        }
    }
}