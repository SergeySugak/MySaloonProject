package com.app.mscorebase.ui.dialogs.choicedialog

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import com.app.mscorebase.R
import com.app.mscorebase.ui.MSDialogFragment
import java.io.Serializable

abstract class MSChoiceDialogFragment<C : ChoiceItem<out Serializable>, VM : MSChoiceDialogFragmentViewModel<C, P>, P> :
    MSDialogFragment<VM>(), DialogInterface.OnClickListener,
    OnMultiChoiceClickListener {
    private lateinit var dialog: AlertDialog
    private var resultListener: OnChoiceItemsSelectedListener<C, P?>? = null
    fun setResultListener(resultListener: OnChoiceItemsSelectedListener<C, P?>?) {
        this.resultListener = resultListener
        getViewModel()?.resultListener = resultListener
    }

    override val layoutId: Int
        get() = 0

    override fun onViewModelCreated(viewModel: VM, savedInstanceState: Bundle?) {
        viewModel.setChoices(if (arguments != null && arguments!!.getParcelableArrayList<C>(ARGUMENT_CHOICES) != null)
                                arguments!!.getParcelableArrayList(ARGUMENT_CHOICES)!!
                            else emptyList())
        viewModel.resultListener = resultListener
        super.onViewModelCreated(viewModel, savedInstanceState)
    }

    override fun onBuildDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        if (arguments != null) {
            builder.setTitle(arguments!!.getString(ARGUMENT_TITLE))
        }
        if (getViewModel()?.choiceMode === ChoiceMode.cmSingle) {
            builder.setSingleChoiceItems(
                getViewModel()?.adapter,
                getViewModel()?.selectedPosition ?: -1,
                this
            )
        } else {
            builder.setAdapter(getViewModel()?.adapter, this)
//            builder.setMultiChoiceItems(
//                getViewModel()?.choicesArray,
//                getViewModel()?.multiSelectedPositions,
//                this
//            )
        }
        builder.setPositiveButton(R.string.ok, this)
        builder.setNegativeButton(R.string.cancel, this)
        dialog = builder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnShowListener { d: DialogInterface ->
            val vm = getViewModel()
            (d as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE).isEnabled =
                if (vm != null && vm.choiceMode == ChoiceMode.cmSingle)
                    vm.selectedPosition >= 0
                else
                    vm?.multiSelectedPositions?.isNotEmpty() ?: false
            onShow(d)
        }
        if (getViewModel()?.choiceMode === ChoiceMode.cmMulti){
            dialog.listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
            //dialog.listView.setOnClickListener(this::onClick)
        }
        return dialog
    }

    override fun getDialog(): AlertDialog? {
        return dialog
    }

    private fun onShow(dialog: AlertDialog) {
        if (getViewModel()?.visibleItems?.size == 1) {
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
                    val payLoad = arguments!!.get(ARGUMENT_PAYLOAD)
                    val listener = vm.resultListener
                    if (listener != null) {
                        listener.onChoiceItemsSelected(
                            if (vm.choiceMode === ChoiceMode.cmSingle) vm.selectedItem else vm.multiSelections,
                            payLoad as P?
                        )
                    } else {
                        if (targetFragment is OnChoiceItemsSelectedListener<*, *>) {
                            (targetFragment as OnChoiceItemsSelectedListener<C, P?>)
                                .onChoiceItemsSelected(
                                    if (vm.choiceMode === ChoiceMode.cmSingle) vm.selectedItem else vm.multiSelections,
                                    payLoad as P?
                                )
                        } else {
                            if (activity is OnChoiceItemsSelectedListener<*, *>) {
                                (activity as OnChoiceItemsSelectedListener<C, P?>)
                                    .onChoiceItemsSelected(
                                        if (vm.choiceMode === ChoiceMode.cmSingle) vm.selectedItem else vm.multiSelections,
                                        payLoad as P?
                                    )
                            }
                        }
                    }
                }
                dismiss()
            }
            DialogInterface.BUTTON_NEGATIVE, DialogInterface.BUTTON_NEUTRAL -> {
                if (arguments != null) {
                    if (vm.resultListener != null) {
                        vm.resultListener?.onNoItemSelected(arguments!!.get(ARGUMENT_PAYLOAD) as P?)
                    } else {
                        if (targetFragment is OnChoiceItemsSelectedListener<*, *>) {
                            (targetFragment as OnChoiceItemsSelectedListener<C, P?>)
                                .onNoItemSelected(
                                    arguments?.get(ARGUMENT_PAYLOAD) as P?
                                )
                        } else {
                            if (activity is OnChoiceItemsSelectedListener<*, *>) {
                                (activity as OnChoiceItemsSelectedListener<C, P?>)
                                    .onNoItemSelected(
                                        arguments?.get(ARGUMENT_PAYLOAD) as P?
                                    )
                            }
                        }
                    }
                }
                dismiss()
            }
            else -> if (dialog is AlertDialog) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = which >= 0
                vm.selectedPosition = which
            }
        }
    }

    override fun dismiss() {
        getViewModel()?.clearInstanceState()
        super.dismiss()
    }

    override fun onClick(dialog: DialogInterface, which: Int, isChecked: Boolean) {
        getViewModel()?.setSelected(which, isChecked)
    }

    companion object {
        val TAG = MSChoiceDialogFragment::class.java.name
        const val ARGUMENT_PAYLOAD = "payload"
        const val ARGUMENT_TITLE = "title"
        const val ARGUMENT_CHOICES = "choices"
    }
}