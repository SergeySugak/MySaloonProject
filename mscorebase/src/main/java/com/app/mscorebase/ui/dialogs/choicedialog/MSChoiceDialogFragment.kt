package com.app.mscorebase.ui.dialogs.choicedialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.app.mscorebase.R
import com.app.mscorebase.ui.MSDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.Serializable

abstract class MSChoiceDialogFragment<C : ChoiceItem<out Serializable>, VM : MSChoiceDialogFragmentViewModel<C, P>, P> :
    MSDialogFragment<VM>(), DialogInterface.OnClickListener {
    private lateinit var dialog: AlertDialog

    override val layoutId: Int
        get() = 0

    override fun onViewModelCreated(viewModel: VM, savedInstanceState: Bundle?) {
        viewModel.setChoices(
            if (arguments != null && requireArguments().getParcelableArrayList<C>(ARGUMENT_CHOICES) != null)
                requireArguments().getParcelableArrayList(ARGUMENT_CHOICES)!!
            else emptyList()
        )
        viewModel.resultListener = arguments?.getParcelable(ARGUMENT_RESULT_LISTENER)
        super.onViewModelCreated(viewModel, savedInstanceState)
    }

    override fun onBuildDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireActivity())
        if (arguments != null) {
            builder.setTitle(requireArguments().getString(ARGUMENT_TITLE))
        }
        val vm = getViewModel() ?: throw IllegalStateException(getString(R.string.err_no_viewmodel))

        builder.setAdapter(vm.adapter, null)
        builder.setPositiveButton(R.string.ok, this)
        builder.setNegativeButton(R.string.cancel, this)
        dialog = builder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnShowListener { d: DialogInterface ->
            invalidateOkButtonState(vm)
            onShow(d as AlertDialog)
        }
        dialog.listView.adapter = vm.adapter
        dialog.listView.setOnItemClickListener { _, view, position, _ ->
            vm.setSelected(position)
            invalidateOkButtonState(vm)
            //dialog.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = vm.selectedItems.size > 0
        }
        return dialog
    }

    override fun getDialog(): AlertDialog? {
        return dialog
    }

    private fun onShow(dialog: AlertDialog) {
        if (getViewModel()?.visibleItems?.size == 1 &&
            getViewModel()?.choiceMode == ChoiceMode.cmSingle
        ) {
            selectSingleItem()
        }
    }

    private fun selectSingleItem() {
        if (dialog.listView != null) {
            dialog.listView.setItemChecked(0, true)
        }
        onClick(dialog, 0)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onClick(dialog: DialogInterface, which: Int) {
        val vm = getViewModel()
        if (vm != null)
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    if (arguments != null) {
                        val payLoad = requireArguments().get(ARGUMENT_PAYLOAD)
                        val listener = vm.resultListener
                        if (listener != null) {
                            listener.onChoiceItemsSelected(vm.selectedItems, payLoad as P?)
                        } else {
                            if (targetFragment is OnChoiceItemsSelectedListener<*, *>) {
                                (targetFragment as OnChoiceItemsSelectedListener<C, P?>)
                                    .onChoiceItemsSelected(vm.selectedItems, payLoad as P?)
                            } else {
                                if (activity is OnChoiceItemsSelectedListener<*, *>) {
                                    (activity as OnChoiceItemsSelectedListener<C, P?>)
                                        .onChoiceItemsSelected(vm.selectedItems, payLoad as P?)
                                }
                            }
                        }
                    }
                    dismiss()
                }
                DialogInterface.BUTTON_NEGATIVE, DialogInterface.BUTTON_NEUTRAL -> {
                    if (arguments != null) {
                        if (vm.resultListener != null) {
                            vm.resultListener?.onNoItemSelected(
                                requireArguments().get(
                                    ARGUMENT_PAYLOAD
                                ) as P?
                            )
                        } else {
                            if (targetFragment is OnChoiceItemsSelectedListener<*, *>) {
                                (targetFragment as OnChoiceItemsSelectedListener<C, P?>)
                                    .onNoItemSelected(arguments?.get(ARGUMENT_PAYLOAD) as P?)
                            } else {
                                if (activity is OnChoiceItemsSelectedListener<*, *>) {
                                    (activity as OnChoiceItemsSelectedListener<C, P?>)
                                        .onNoItemSelected(arguments?.get(ARGUMENT_PAYLOAD) as P?)
                                }
                            }
                        }
                    }
                    dismiss()
                }
                else -> if (dialog is AlertDialog) {
                    vm.setSelected(which)
                    invalidateOkButtonState(vm)
                }
            }
    }

    override fun dismiss() {
        getViewModel()?.clearInstanceState()
        super.dismiss()
    }

    override fun onStartObservingViewModel(viewModel: VM) {
        viewModel.choicesUpdated.observe(this, Observer {
            if (viewModel.choiceMode === ChoiceMode.cmSingle) {
                dialog.listView.setSelection(viewModel.singleChoicePosition)
            }
            invalidateOkButtonState(viewModel)
        })

    }

    private fun invalidateOkButtonState(viewModel: VM) {
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled =
            if (viewModel.choiceMode === ChoiceMode.cmSingle) {
                viewModel.selectedItems.size > 0
            } else {
                true
            }
    }

    companion object {
        val TAG = MSChoiceDialogFragment::class.java.name
        const val ARGUMENT_PAYLOAD = "payload"
        const val ARGUMENT_TITLE = "title"
        const val ARGUMENT_CHOICES = "choices"
        const val ARGUMENT_RESULT_LISTENER = "result_listener"
    }
}