package main.java.com.app.msa.main

import com.app.mscorebase.appstate.AppState
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.ui.MSActivityViewModel
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(appState: AppState): MSActivityViewModel(appState){

    override fun restoreState(writer: StateWriter) {

    }

    override fun saveState(writer: StateWriter) {

    }
}