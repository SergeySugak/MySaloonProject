package com.app.feature_masters.ui

import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppState
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.ui.MSFragmentViewModel
import javax.inject.Inject

class MastersViewModel
    @Inject constructor(appState: AppState,
                        val dbRepository: DbRepository) : MSFragmentViewModel(appState) {

    override fun restoreState(writer: StateWriter) {

    }

    override fun saveState(writer: StateWriter) {

    }
}
