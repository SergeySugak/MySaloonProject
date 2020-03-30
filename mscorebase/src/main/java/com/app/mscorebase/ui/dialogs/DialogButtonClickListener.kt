package com.app.mscorebase.ui.dialogs

import android.content.DialogInterface
import android.os.Bundle
import com.app.mscorebase.ui.dialogs.messagedialog.DialogFragmentPresenter

interface DialogButtonClickListener {
    fun onClickDialogButton(
        dialog: DialogInterface?, @DialogFragmentPresenter.WhichButton whichButton: Int,
        requestCode: Int,
        params: Bundle?
    )
}