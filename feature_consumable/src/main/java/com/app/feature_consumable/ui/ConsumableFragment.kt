package com.app.feature_consumable.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.text.TextUtils
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.feature_consumable.R
import com.app.feature_consumable.di.DaggerConsumableFeatureComponent
import com.app.msa_nav_api.navigation.AppNavigator
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSDialogFragment
import com.app.mscorebase.ui.dialogs.choicedialog.OnChoiceItemsSelectedListener
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import com.app.mscoremodels.saloon.ChoosableUom
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject

class ConsumableFragment : MSDialogFragment<ConsumableViewModel>() {
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    @Inject
    lateinit var appNavigator: AppNavigator
        protected set

    override val layoutId = R.layout.fragment_consumable

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerConsumableFeatureComponent
            .builder()
            .consumableFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onBuildDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(layoutId, null)
        val uom = view.findViewById<EditText>(R.id.consumable_uom)
        uom.setOnClickListener {
            appNavigator.navigateToSelectUom(this,
                getString(R.string.str_select_uom), uom.text.toString(),
                object: OnChoiceItemsSelectedListener<ChoosableUom, String?> {
                    override fun onChoiceItemsSelected(
                        selections: List<ChoosableUom>,
                        payload: String?
                    ) {
                        uom.setText(selections[0].name ?: "")
                        getViewModel()?.setUom(selections[0].id ?: "")
                    }
                    override fun onNoItemSelected(payload: String?) {}
                    override fun writeToParcel(dest: Parcel?, flags: Int) {}
                    override fun describeContents() = 0
                }
            )
        }

        val builder = MaterialAlertDialogBuilder(requireActivity())
        builder.setView(view)
            .setTitle(R.string.title_fragment_edit_consumable)
            .setPositiveButton(getString(R.string.ok)) { _, _ -> }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                dialog?.dismiss()
            }
        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        val dlg = dialog as AlertDialog?
        if (dlg != null) {
            val ok = dlg.getButton(Dialog.BUTTON_POSITIVE)
            ok.setOnClickListener { _ ->
                getViewModel()?.saveConsumableInfo(
                    dlg.findViewById<EditText>(R.id.consumable_name)?.text.toString(),
                    dlg.findViewById<EditText>(R.id.consumable_price)?.text.toString()
                )
            }
        }
    }

    override fun createViewModel(savedInstanceState: Bundle?) =
        ViewModelProvider(this, providerFactory).get(ConsumableViewModel::class.java)


    override fun onViewModelCreated(viewModel: ConsumableViewModel, savedInstanceState: Bundle?) {
        super.onViewModelCreated(viewModel, savedInstanceState)
        val consumableId = arguments?.get(ARG_EDIT_CONSUMABLE_ID)?.toString()
        if (!TextUtils.isEmpty(consumableId)) {
            viewModel.loadData(consumableId!!)
        }
    }

    override fun onStartObservingViewModel(viewModel: ConsumableViewModel) {
        viewModel.error.observe(this, Observer { error ->
            if (!viewModel.error.isHandled) {
                MessageDialogFragment.showError(this, error, false)
                viewModel.error.isHandled = true
            }
        })

        viewModel.consumableInfoSaveState.observe(this, Observer {
            if (it) {
                val intent = Intent()
                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                dismiss()
            }
        })

        viewModel.consumableInfo.observe(this, Observer { consumable ->
            dialog?.findViewById<EditText>(R.id.consumable_name)?.setText(consumable.name)
            dialog?.findViewById<EditText>(R.id.consumable_price)?.setText(consumable.price.toString())
            dialog?.findViewById<EditText>(R.id.consumable_uom)?.setText(consumable.uom)
        })
    }

    companion object {
        fun newInstance() = ConsumableFragment()

        const val ARG_EDIT_CONSUMABLE_ID = "ARG_EDIT_CONSUMABLE_ID"
    }
}
