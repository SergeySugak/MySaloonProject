package com.app.mscorebase.ui.dialogs

import android.content.DialogInterface
import android.os.Bundle
import com.app.mscorebase.ui.dialogs.messagedialog.DialogFragmentPresenterImpl.WhichButton

interface DialogButtonClickListener {
    fun onClickDialogButton(
        dialog: DialogInterface?, @WhichButton whichButton: Int,
        requestCode: Int,
        params: Bundle?
    )
}