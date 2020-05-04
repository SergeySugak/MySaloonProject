package com.app.feature_schedule.ui

import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.ui.Colorizer
import com.app.mscorebase.ui.MSFragmentViewModel
import javax.inject.Inject

class ScheduleViewModel
    @Inject constructor(private val appState: AppStateManager,
                        val eventColorizer: Colorizer,
                        private val dbRepository: DbRepository) : MSFragmentViewModel(appState) {
    override fun restoreState(writer: StateWriter) {

    }

    override fun saveState(writer: StateWriter) {

    }
}
