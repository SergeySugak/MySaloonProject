package com.app.mscorebase.ui.dialogs.progressdialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.app.mscorebase.R

class ProgressDialogFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val wnd = dialog!!.window
        if (wnd != null) {
            wnd.setBackgroundDrawableResource(android.R.color.transparent)
            wnd.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
        return inflater.inflate(R.layout.fragment_progress, null)
    }

    companion object {
        val TAG = ProgressDialogFragment::class.java.name
        fun newInstance(): ProgressDialogFragment {
            val args = Bundle()
            val fragment = ProgressDialogFragment()
            fragment.arguments = args
            fragment.isCancelable = false
            fragment.setStyle(STYLE_NO_TITLE, 0)
            return fragment
        }
    }
}