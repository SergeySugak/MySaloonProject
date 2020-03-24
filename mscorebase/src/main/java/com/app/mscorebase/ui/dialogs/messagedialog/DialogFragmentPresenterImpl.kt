package com.app.mscorebase.ui.dialogs.messagedialog

import android.content.DialogInterface
import androidx.annotation.IntDef
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.app.mscorebase.ui.dialogs.DialogButtonClickListener

class DialogFragmentPresenterImpl :
    DialogFragmentPresenter {
    @IntDef(
        NO_ICON,
        ICON_INFO,
        ICON_QUESTION,
        ICON_WARINING,
        ICON_ERROR
    )
    annotation class Icon

    @IntDef(
        NO_BUTTONS,
        ONE_BUTTON,
        TWO_BUTTONS,
        THREE_BUTTONS,
        ONE_BUTTON_Y,
        TWO_BUTTONS_YN,
        THREE_BUTTONS_YNC
    )
    annotation class Buttons

    @IntDef(
        DialogInterface.BUTTON_POSITIVE,
        DialogInterface.BUTTON_NEGATIVE,
        DialogInterface.BUTTON_NEUTRAL
    )
    annotation class WhichButton

    override fun display(
        from: Fragment,
        newFragment: DialogFragment,
        fragmentTag: String?
    ) {
        newFragment.arguments?.putBoolean(DialogFragmentPresenter.ARGUMENT_FROM_FRAGMENT, true)
        newFragment.setTargetFragment(from, 0)
        val fm = from.parentFragmentManager
        val ft = fm.beginTransaction()
        val prev = fm.findFragmentByTag(fragmentTag)
        if (prev != null) {
            ft.remove(prev)
        }
        //ft.addToBackStack(null);
//show the dialog.
        newFragment.show(ft, fragmentTag)
    }

    override fun display(
        from: FragmentActivity,
        newFragment: DialogFragment,
        fragmentTag: String?
    ) {
        newFragment.arguments?.putBoolean(DialogFragmentPresenter.ARGUMENT_FROM_FRAGMENT, false)
        val fm = from.supportFragmentManager
        val ft = fm.beginTransaction()
        val prev = fm.findFragmentByTag(fragmentTag)
        if (prev != null) {
            ft.remove(prev)
        }
        //ft.addToBackStack(null);
//show the dialog.
        newFragment.show(ft, fragmentTag)
    }

    override fun isDisplayed(
        from: Fragment?,
        fragmentTag: String?
    ): Boolean {
        val fm = from!!.parentFragmentManager
        val prev = fm.findFragmentByTag(fragmentTag)
        return prev != null
    }

    override fun isDisplayed(
        from: FragmentActivity?,
        fragmentTag: String?
    ): Boolean {
        return from!!.supportFragmentManager.findFragmentByTag(fragmentTag) != null
    }

    override fun getMessageDialogListener(dlgFragment: DialogFragment?): DialogButtonClickListener? {
        if (dlgFragment!!.arguments!!.getBoolean(
                DialogFragmentPresenter.ARGUMENT_FROM_FRAGMENT,
                false
            )
        ) {
            val target = dlgFragment.targetFragment
            if (target is DialogButtonClickListener) {
                return target
            }
            //			try {
        } else {
            val activity = dlgFragment.activity
            if (activity is DialogButtonClickListener) {
                return activity
            }
        }
        return null
    }

    companion object {
        const val NO_ICON = -1
        const val ICON_INFO = -2 //R.drawable.ic_info;
        const val ICON_QUESTION = -3 //R.drawable.ic_question;
        const val ICON_WARINING = -4 //R.drawable.ic_warning;
        const val ICON_ERROR = -5 //R.drawable.ic_error;
        const val NO_BUTTONS = -1
        const val ONE_BUTTON = 0
        const val TWO_BUTTONS = 1
        const val THREE_BUTTONS = 2
        const val ONE_BUTTON_Y = 3
        const val TWO_BUTTONS_YN = 4
        const val THREE_BUTTONS_YNC = 5

        fun showDialogFragment(
            curFragment: Fragment,
            newFragment: DialogFragment,
            tag: String?
        ) {
            newFragment.setTargetFragment(curFragment, 0)
            val fm = curFragment.parentFragmentManager
            val ft = fm.beginTransaction()
            val prev = fm.findFragmentByTag(tag)
            if (prev != null) {
                ft.remove(prev)
            }
            newFragment.show(ft, tag)
        }
    }
}