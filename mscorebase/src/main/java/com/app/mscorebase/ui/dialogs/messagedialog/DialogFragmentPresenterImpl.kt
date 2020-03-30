package com.app.mscorebase.ui.dialogs.messagedialog

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.app.mscorebase.ui.dialogs.DialogButtonClickListener

class DialogFragmentPresenterImpl :
    DialogFragmentPresenter {

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