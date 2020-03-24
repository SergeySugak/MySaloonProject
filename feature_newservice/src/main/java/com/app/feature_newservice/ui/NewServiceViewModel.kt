package com.app.feature_newservice.ui

import androidx.lifecycle.viewModelScope
import com.app.feature_newservice.R
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.common.Result
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.MSFragmentViewModel
import com.app.mscoremodels.saloon.SaloonFactory
import com.app.mscoremodels.saloon.ServiceDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Double.valueOf
import javax.inject.Inject

class NewServiceViewModel
    @Inject constructor(val appState: AppStateManager,
                        private val saloonFactory: SaloonFactory,
                        private val dbRepository: DbRepository): MSFragmentViewModel(appState) {

    val _serviceInfoSaveState = StatefulMutableLiveData<Boolean>()
    val serviceInfoSaveState: StatefulLiveData<Boolean> = _serviceInfoSaveState
    var serviceDuration: ServiceDuration? = null
    var serviceId: String = ""

    override fun restoreState(writer: StateWriter) {

    }

    override fun saveState(writer: StateWriter) {

    }

    fun saveServiceInfo(name: String, price: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val duration = serviceDuration?.id
            _serviceInfoSaveState.postValue(
                if (duration != null) {
                    val result = dbRepository.saveServiceInfo(
                        saloonFactory.createSaloonService(appState.authManager.getUserId(),
                            serviceId, name, valueOf(price), duration, description))
                    if (result is Result.Success){
                        result.data
                    }
                    else {
                        intError.postValue((result as Result.Error).exception)
                        false
                    }
                }
                else {
                    intError.postValue(Exception(appState.context.getString(R.string.err_fill_required_before_save)))
                    false
                })
        }
    }
}
