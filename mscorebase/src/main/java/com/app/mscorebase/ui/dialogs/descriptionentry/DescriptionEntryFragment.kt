package com.app.mscorebase.ui.dialogs.descriptionentry

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.app.mscorebase.R
import com.app.mscorebase.common.ActionResult
import com.app.mscorebase.common.Validator
import com.app.mscorebase.utils.hideKeyboard
import java.io.Serializable

class DescriptionEntryFragment : DialogFragment() {
    protected var descriptionEditText: EditText? = null
    private var validator: Validator<String?>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        retainInstance = true
        val inflater = requireActivity().layoutInflater

        @SuppressLint("InflateParams")
        val view = inflater.inflate(R.layout.fragment_description_entry, null, false)
        descriptionEditText = view.findViewById(R.id.description)
        assert(arguments != null)
        val descrText = requireArguments().getString(ARGUMENTS_DESCRIPTION)
        if (savedInstanceState == null && !TextUtils.isEmpty(descrText)) {
            descriptionEditText?.setText(descrText)
            descriptionEditText?.setSelection(descrText!!.length)
        }
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)
        if (arguments!!.getInt(ARGUMENTS_TITLE_RES_ID) != 0) {
            builder.setTitle(arguments!!.getInt(ARGUMENTS_TITLE_RES_ID))
        }
        builder.setPositiveButton(android.R.string.ok, null)
        builder.setNegativeButton(android.R.string.cancel, null)
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        setUpUi(dialog)
        dialog.setOnShowListener { dialog1: DialogInterface? ->
            var button: Button? = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            button?.setOnClickListener { v: View? -> onPositiveClick(v) }
            button = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            button?.setOnClickListener { v: View? -> onNegativeClick(v) }
            button = dialog.getButton(DialogInterface.BUTTON_NEUTRAL)
            button?.setOnClickListener { v: View? -> onNeutralClick(v) }
        }
        return dialog
    }

    override fun onDestroyView() {
        val dialog = dialog
        // Work around bug: http://code.google.com/p/android/issues/detail?id=17423
        if (dialog != null && retainInstance) {
            dialog.setDismissMessage(null)
        }
        super.onDestroyView()
    }

    protected fun setUpUi(dialog: Dialog?) {
        descriptionEditText!!.setBackgroundResource(android.R.color.transparent)
        val defGravity = Gravity.TOP or Gravity.START
        val defLines = 5
        if (arguments != null) {
            val stylingMap =
                arguments!!.getSerializable(ARGUMENT_STYLING_MAP) as Map<String, Int>?
            if (stylingMap != null) {
                var v =
                    stylingMap[STYLING_MAP_INPUT_TYPE]
                if (v != null) {
                    descriptionEditText!!.inputType = v
                }
                v = stylingMap[STYLING_MAP_GRAVITY]
                if (v != null) {
                    descriptionEditText!!.gravity = v
                } else {
                    descriptionEditText!!.gravity = defGravity
                }
                v = stylingMap[STYLING_MAP_HINT]
                if (v != null) {
                    descriptionEditText!!.setHint(v)
                }
                v = stylingMap[STYLING_MAP_MAX_LINES]
                if (v != null) {
                    descriptionEditText!!.minLines = v
                } else {
                    descriptionEditText!!.minLines = defLines
                }
                v = stylingMap[STYLING_MAP_MIN_LINES]
                if (v != null) {
                    descriptionEditText!!.maxLines = v
                } else {
                    descriptionEditText!!.maxLines = defLines
                }
            }
        } else {
            descriptionEditText!!.minLines = defLines
            descriptionEditText!!.maxLines = defLines
            descriptionEditText!!.gravity = defGravity
        }
    }

    protected fun onPositiveClick(v: View?) {
        var descr: String? = descriptionEditText!!.text.toString()
        if (validator != null) {
            val validationResult = validator!!.validate(descr)
            if (validationResult.status == ActionResult.Status.ERROR) {
                val errorMessage = validationResult.message
                if (!TextUtils.isEmpty(errorMessage)) {
                    descriptionEditText!!.error = errorMessage
                }
                return
            }
        }
        if (descr!!.isEmpty()) descr = null
        hideKeyboard(descriptionEditText)
        dismiss()
        assert(arguments != null)
        if (targetFragment is OnDescriptionEnteredListener) {
            if (arguments!!.getBoolean(ARGUMENTS_PAYLOAD_INST_OF_SERIALIZABLE, false))
                (targetFragment as OnDescriptionEnteredListener).onDescriptionEntered(
                    descr,
                    if (arguments!!.getBoolean(ARGUMENTS_PAYLOAD_INST_OF_SERIALIZABLE, false))
                        arguments!!.getSerializable(ARGUMENTS_PAYLOAD)
                    else
                        arguments!!.getParcelable<Parcelable>(ARGUMENTS_PAYLOAD)
                )
        } else {
            if (activity is OnDescriptionEnteredListener) {
                (activity as OnDescriptionEnteredListener).onDescriptionEntered(
                    descr,
                    if (arguments!!.getBoolean(ARGUMENTS_PAYLOAD_INST_OF_SERIALIZABLE, false))
                        arguments!!.getSerializable(ARGUMENTS_PAYLOAD)
                    else
                        arguments!!.getParcelable<Parcelable>(ARGUMENTS_PAYLOAD)
                )
            }
        }
    }

    protected fun onNegativeClick(v: View?) {
        hideKeyboard(descriptionEditText)
        dismiss()
        assert(arguments != null)
        if (targetFragment is OnDescriptionEnteredListener) {
            (targetFragment as OnDescriptionEnteredListener)
                .onDescriptionNotEntered(
                    if (arguments!!.getBoolean(ARGUMENTS_PAYLOAD_INST_OF_SERIALIZABLE, false))
                        arguments!!.getSerializable(ARGUMENTS_PAYLOAD)
                    else
                        arguments!!.getParcelable<Parcelable>(ARGUMENTS_PAYLOAD)
                )
        } else {
            if (activity is OnDescriptionEnteredListener) {
                (activity as OnDescriptionEnteredListener).onDescriptionNotEntered(
                    if (arguments!!.getBoolean(ARGUMENTS_PAYLOAD_INST_OF_SERIALIZABLE, false))
                        arguments!!.getSerializable(ARGUMENTS_PAYLOAD)
                    else
                        arguments!!.getParcelable<Parcelable>(ARGUMENTS_PAYLOAD)
                )
            }
        }
    }

    protected fun onNeutralClick(v: View?) {
        hideKeyboard(descriptionEditText)
        dismiss()
    }

    interface OnDescriptionEnteredListener {
        fun onDescriptionEntered(descr: String?, payload: Any?)
        fun onDescriptionNotEntered(payload: Any?)
    }

    companion object {
        val TAG = DescriptionEntryFragment::class.java.simpleName
        protected const val ARGUMENTS_TITLE_RES_ID = "titleResId"
        protected const val ARGUMENTS_DESCRIPTION = "description"
        protected const val ARGUMENTS_PAYLOAD = "payload"
        protected const val ARGUMENT_STYLING_MAP = "stylingMap"
        const val STYLING_MAP_INPUT_TYPE = "inputType"
        const val STYLING_MAP_GRAVITY = "gravity"
        const val STYLING_MAP_HINT = "hint"
        const val STYLING_MAP_MAX_LINES = "maxLines"
        const val STYLING_MAP_MIN_LINES = "minLines"
        private const val ARGUMENTS_PAYLOAD_INST_OF_SERIALIZABLE =
            "payloadInstOfParcelable"

        fun <T> newInstance(
            @StringRes titleId: Int, description: String?,
            payload: T
        ): DescriptionEntryFragment {
            return newInstance(
                titleId,
                description,
                payload,
                emptyMap<String?, Int>(),
                null
            )
        }

        fun <T> newInstance(
            @StringRes titleId: Int, description: String?, payload: T?,
            stylingMap: Map<String?, Int?>,
            validator: Validator<String?>?
        ): DescriptionEntryFragment {
            if (stylingMap !is Serializable) {
                throw RuntimeException("stylingMap parameter must be instance of Serializable")
            }
            val args = Bundle()
            args.putInt(ARGUMENTS_TITLE_RES_ID, titleId)
            args.putString(ARGUMENTS_DESCRIPTION, description)
            //Serializable и null
            require(!(payload != null && payload !is Serializable && payload !is Parcelable)) { "DescriptionEntryFragment payload agrument is not instance of Serializable or Parcelable!" }
            if (payload is Parcelable) {
                args.putParcelable(
                    ARGUMENTS_PAYLOAD,
                    payload as Parcelable?
                )
            } else { //Serializable и null
                args.putSerializable(
                    ARGUMENTS_PAYLOAD,
                    payload as Serializable?
                )
                args.putBoolean(
                    ARGUMENTS_PAYLOAD_INST_OF_SERIALIZABLE,
                    true
                )
            }
            args.putSerializable(
                ARGUMENT_STYLING_MAP,
                stylingMap as Serializable
            )
            val fragment =
                DescriptionEntryFragment()
            fragment.arguments = args
            fragment.validator = validator
            return fragment
        }
    }
}