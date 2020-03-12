package com.app.mscorebase.ui.dialogs.choicedialog

import android.R
import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AlertDialog
import com.app.mscorebase.ui.MSDialogFragment
import java.io.Serializable

abstract class MSChoiceDialogFragment<C : ChoiceItem<out Serializable>, VM : MSChoiceDialogFragmentViewModel<C>> :
    MSDialogFragment<VM>(), DialogInterface.OnClickListener,
    OnMultiChoiceClickListener {
    private lateinit var dialog: AlertDialog
    private var resultListener: OnChoiceItemsSelectedListener<C, out Parcelable>? = null
    fun setResultListener(resultListener: OnChoiceItemsSelectedListener<C, out Parcelable>) {
        this.resultListener = resultListener
        if (getViewModel() != null) {
            getViewModel().resultListener = resultListener
        }
    }

    override val layoutId: Int
        get() = 0

    override fun onViewModelCreated(viewModel: VM, savedInstanceState: Bundle?) {
        viewModel.choiceMode = if (arguments != null) arguments!!.getSerializable(ARGUMENT_MODE) as ChoiceItem.ChoiceMode
                                else ChoiceItem.ChoiceMode.cmSingle
        viewModel.setChoices(if (arguments != null && arguments!!.getParcelableArrayList<C>(ARGUMENT_CHOICES) != null)
                                arguments!!.getParcelableArrayList<C>(ARGUMENT_CHOICES)!!
                            else emptyList())
        viewModel.resultListener = resultListener
        super.onViewModelCreated(viewModel, savedInstanceState)
    }

    override fun onBuildDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        if (arguments != null) {
            builder.setTitle(arguments!!.getString(ARGUMENT_TITLE))
        }
        if (getViewModel().choiceMode === ChoiceItem.ChoiceMode.cmSingle) {
            builder.setSingleChoiceItems(
                getViewModel().adapter,
                getViewModel().selectedPosition,
                this
            )
        } else {
            builder.setMultiChoiceItems(
                getViewModel().choicesArray,
                getViewModel().multiSelectedPositions,
                this
            )
        }
        builder.setPositiveButton(R.string.ok, this)
        builder.setNegativeButton(R.string.cancel, this)
        dialog = builder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnShowListener { d: DialogInterface ->
            (d as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE).isEnabled =
                if (getViewModel().choiceMode === ChoiceItem.ChoiceMode.cmSingle) getViewModel().selectedPosition >= 0 else getViewModel().multiSelectedPositions.isNotEmpty()
            onShow(d)
        }
        return dialog
    }

    override fun getDialog(): AlertDialog? {
        return dialog
    }

    private fun onShow(dialog: AlertDialog) {
        if (getViewModel().visibleItems.size == 1) {
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
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                if (arguments != null) {
                    if (getViewModel().resultListener != null) {
                        getViewModel().resultListener?.onChoiceItemsSelected(
                            if (getViewModel().choiceMode === ChoiceItem.ChoiceMode.cmSingle) getViewModel().selectedItem else getViewModel().multiSelections,
                            arguments!!.getParcelable(ARGUMENT_PAYLOAD)
                        )
                    } else {
                        if (targetFragment is OnChoiceItemsSelectedListener<*, *>) {
                            (targetFragment as OnChoiceItemsSelectedListener<C, Parcelable>)
                                .onChoiceItemsSelected(
                                    if (getViewModel().choiceMode === ChoiceItem.ChoiceMode.cmSingle) getViewModel().selectedItem else getViewModel().multiSelections,
                                    arguments?.getParcelable<Parcelable>(ARGUMENT_PAYLOAD)
                                )
                        } else {
                            if (activity is OnChoiceItemsSelectedListener<*, *>) {
                                (activity as OnChoiceItemsSelectedListener<C, Parcelable>)
                                    .onChoiceItemsSelected(
                                        if (getViewModel().choiceMode === ChoiceItem.ChoiceMode.cmSingle) getViewModel().selectedItem else getViewModel().multiSelections,
                                        arguments!!.getParcelable<Parcelable>(ARGUMENT_PAYLOAD)
                                    )
                            }
                        }
                    }
                }
                dismiss()
            }
            DialogInterface.BUTTON_NEGATIVE, DialogInterface.BUTTON_NEUTRAL -> {
                if (arguments != null) {
                    if (getViewModel().resultListener != null) {
                        getViewModel().resultListener?.onNoItemSelected(arguments!!.getParcelable(ARGUMENT_PAYLOAD) )
                    } else {
                        if (targetFragment is OnChoiceItemsSelectedListener<*, *>) {
                            (targetFragment as OnChoiceItemsSelectedListener<C, Parcelable>)
                                .onNoItemSelected(
                                    arguments?.getParcelable<Parcelable>(ARGUMENT_PAYLOAD)
                                )
                        } else {
                            if (activity is OnChoiceItemsSelectedListener<*, *>) {
                                (activity as OnChoiceItemsSelectedListener<C, Parcelable>)
                                    .onNoItemSelected(
                                        arguments?.getParcelable<Parcelable>(ARGUMENT_PAYLOAD)
                                    )
                            }
                        }
                    }
                }
                dismiss()
            }
            else -> if (dialog is AlertDialog) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = which >= 0
                getViewModel().selectedPosition = which
            }
        }
    }

    override fun dismiss() {
        if (getViewModel() != null) {
            getViewModel().clearInstanceState()
        }
        super.dismiss()
    }

    override fun onClick(
        dialog: DialogInterface,
        which: Int,
        isChecked: Boolean
    ) {
        getViewModel().setSelected(which, isChecked)
    }

    companion object {
        val TAG = MSChoiceDialogFragment::class.java.name
        const val ARGUMENT_MODE = "mode"
        const val ARGUMENT_PAYLOAD = "payload"
        const val ARGUMENT_TITLE = "title"
        const val ARGUMENT_CHOICES = "choices"
    }
}