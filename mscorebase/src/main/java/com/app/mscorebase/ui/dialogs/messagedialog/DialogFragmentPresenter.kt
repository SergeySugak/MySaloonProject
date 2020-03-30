package com.app.mscorebase.ui.dialogs.messagedialog

import android.content.DialogInterface
import androidx.annotation.IntDef
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.app.mscorebase.ui.dialogs.DialogButtonClickListener

interface DialogFragmentPresenter {

    @IntDef(
        NO_ICON,
        ICON_INFO,
        ICON_QUESTION,
        ICON_WARNING,
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
        const val NO_ICON = -1
        const val ICON_INFO = -2 //R.drawable.ic_info;
        const val ICON_QUESTION = -3 //R.drawable.ic_question;
        const val ICON_WARNING = -4 //R.drawable.ic_warning;
        const val ICON_ERROR = -5 //R.drawable.ic_error;
        const val NO_BUTTONS = -1
        const val ONE_BUTTON = 0
        const val TWO_BUTTONS = 1
        const val THREE_BUTTONS = 2
        const val ONE_BUTTON_Y = 3
        const val TWO_BUTTONS_YN = 4
        const val THREE_BUTTONS_YNC = 5
    }
}