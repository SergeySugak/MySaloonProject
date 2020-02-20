package com.app.mscorebase.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun hideKeyboard(view: View?) {
    if (view == null) {
        return
    }
    val inputManager = view
        .context
        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val binder = view.windowToken
    inputManager.hideSoftInputFromWindow(binder, 0)
}
