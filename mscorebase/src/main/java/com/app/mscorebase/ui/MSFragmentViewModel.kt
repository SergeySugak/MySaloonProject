package com.app.mscorebase.ui

import com.app.mscorebase.appstate.AppState

abstract class MSFragmentViewModel(appState: AppState) : MSViewModel(appState){
    protected fun setTitle(
        title: String,
        subtitle: String?
    ) {
        this.title.value = title
        this.subtitle.value = subtitle
    }
}