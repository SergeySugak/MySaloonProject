package com.app.feature_newservice.ui

import com.app.feature_newservice.R
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppState
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.ui.MSFragmentViewModel
import com.app.mscoremodels.services.ServiceDuration
import java.lang.Double.valueOf
import javax.inject.Inject

class NewServiceViewModel
    @Inject constructor(val appState: AppState,
                        private val dbRepository: DbRepository): MSFragmentViewModel(appState) {

    var serviceDuration: ServiceDuration? = null

    override fun restoreState(writer: StateWriter) {

    }

    override fun saveState(writer: StateWriter) {

    }

    fun saveServiceInfo(name: String, price: String, descr: String): Boolean {
        val duration = serviceDuration?.id
        return if (duration != null) {
            return dbRepository.saveServiceInfo(name, duration, valueOf(price), descr)
        }
        else {
            _genericError.value = Exception(appState.context.getString(R.string.err_fill_required_before_save))
            false
        }
    }
}
