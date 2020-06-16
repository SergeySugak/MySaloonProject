package com.app.mscorebase.ui

import com.app.mscorebase.appstate.AppStateManager

abstract class MSFragmentViewModel(appState: AppStateManager) : MSViewModel(appState) {
    protected fun setTitle(
        title: String,
        subtitle: String?
    ) {
        _title.value = title
        _subtitle.value = subtitle
    }
}