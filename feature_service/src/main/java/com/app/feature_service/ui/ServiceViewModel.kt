package com.app.feature_service.ui

import androidx.lifecycle.viewModelScope
import com.app.feature_service.R
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.common.Result
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.MSFragmentViewModel
import com.app.mscoremodels.saloon.ChoosableServiceDuration
import com.app.mscoremodels.saloon.SaloonFactory
import com.app.mscoremodels.saloon.SaloonService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Double.valueOf
import javax.inject.Inject

class ServiceViewModel
@Inject constructor(
    val appState: AppStateManager,
    private val saloonFactory: SaloonFactory,
    private val dbRepository: DbRepository
) : MSFragmentViewModel(appState) {

    val intServiceInfoSaveState = StatefulMutableLiveData<Boolean>()
    val serviceInfoSaveState: StatefulLiveData<Boolean> = intServiceInfoSaveState
    var serviceDuration: ChoosableServiceDuration? = null
    var serviceId: String = ""
    val intServiceInfo = StatefulMutableLiveData<SaloonService>()
    val serviceInfo: StatefulLiveData<SaloonService> = intServiceInfo

    override fun restoreState(writer: StateWriter) {

    }

    override fun saveState(writer: StateWriter) {

    }

    fun saveServiceInfo(
        name: String,
        duration: ChoosableServiceDuration?,
        price: String,
        description: String
    ) {
        if (duration != null) {
            viewModelScope.launch(Dispatchers.IO) {
                intServiceInfoSaveState.postValue(
                    run {
                        val result = dbRepository.saveServiceInfo(
                            saloonFactory.createSaloonService(
                                serviceId, name, valueOf(price),
                                saloonFactory.createServiceDuration(duration),
                                description
                            )
                        )
                        if (result is Result.Success) {
                            result.data
                        } else {
                            intError.postValue((result as Result.Error).exception)
                            false
                        }
                    }
                )
            }
        } else {
            intError.postValue(Exception(appState.context.getString(R.string.err_fill_required_before_save)))
        }
    }

    fun loadData(serviceId: String) {
        this.serviceId = serviceId
        viewModelScope.launch(Dispatchers.IO) {
            val serviceResult = dbRepository.loadServiceInfo(serviceId)
            if (serviceResult is Result.Success) {
                intServiceInfo.postValue(serviceResult.data)
                serviceDuration =
                    saloonFactory.createChoosableServiceDuration(serviceResult.data?.duration)
            } else {
                intError.postValue((serviceResult as Result.Error).exception)
            }
        }
    }
}
