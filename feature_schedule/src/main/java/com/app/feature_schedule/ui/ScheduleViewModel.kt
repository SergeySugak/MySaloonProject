package com.app.feature_schedule.ui

import com.app.mscorebase.appstate.AppState
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.ui.MSFragmentViewModel
import com.app.msa_db_repo.repository.db.DbRepository
import javax.inject.Inject

class ScheduleViewModel
    @Inject constructor(appState: AppState,
                        val dbRepository: DbRepository) : MSFragmentViewModel(appState) {
    override fun restoreState(writer: StateWriter) {

    }

    override fun saveState(writer: StateWriter) {

    }
}
