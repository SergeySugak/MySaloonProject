package com.app.mscorebase.ui.dialogs.messagedialog

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.app.mscorebase.ui.dialogs.DialogButtonClickListener

interface DialogFragmentPresenter {
    fun display(
        from: Fragment,
        newFragment: DialogFragment,
        fragmentTag: String?
    )

    fun display(
        from: FragmentActivity,
        newFragment: DialogFragment,
        fragmentTag: String?
    )

    fun isDisplayed(
        from: Fragment?,
        fragmentTag: String?
    ): Boolean

    fun isDisplayed(from: FragmentActivity?, fragmentTag: String?): Boolean
    fun getMessageDialogListener(dlgFragment: DialogFragment?): DialogButtonClickListener?

    companion object {
        const val ARGUMENT_FROM_FRAGMENT = "from_fragment"
    }
}