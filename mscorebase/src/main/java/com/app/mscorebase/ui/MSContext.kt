package com.app.mscorebase.ui

import android.content.Context
import android.content.Intent
import androidx.fragment.app.DialogFragment
import com.app.mscorebase.ui.dialogs.DialogButtonClickListener

interface MSContext<VM : MSViewModel> :
    DialogButtonClickListener {
    fun showDialogFragment(newFragment: DialogFragment?, tag: String?)
    fun hideDialogFragment(tag: String?)
    fun startActivity(intent: Intent?)
    fun startActivityForResult(intent: Intent?, requestCode: Int)
    fun setInProgress(inProgress: Boolean)
    fun getContext(): Context?
}