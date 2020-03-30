package com.app.feature_services.ui

import androidx.lifecycle.viewModelScope
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.common.Result
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.MSFragmentViewModel
import com.app.mscoremodels.saloon.SaloonService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ServicesViewModel
    @Inject constructor(
        appState: AppStateManager,
        private val dbRepository: DbRepository) : MSFragmentViewModel(appState) {

    private val _services = StatefulMutableLiveData<MutableList<SaloonService>>(mutableListOf())
    val services: StatefulLiveData<MutableList<SaloonService>> = _services

    private val listenerId: String

    fun deleteService(serviceId: String?) {
        serviceId?.let{
            viewModelScope.launch(Dispatchers.IO) {
                val result = dbRepository.deleteServiceInfo(serviceId)
                if (result is Result.Error){
                    intError.postValue(result.exception)
                }
            }
        }
    }

    override fun restoreState(writer: StateWriter) {

    }

    override fun saveState(writer: StateWriter) {

    }

    override fun onCleared() {
        dbRepository.stopListeningToServices(listenerId)
        super.onCleared()
    }

    private fun onServiceInserted(service: SaloonService){
        var services = _services.value
        if (services == null) {
            services = mutableListOf()
        }
        services.add(service)
        _services.value = services
    }

    private fun onServiceUpdated(changedServiceId: String, service: SaloonService){
        var services = _services.value
        if (services == null) {
            services = mutableListOf()
            services.add(service)
        }
        else {
            for (curService in services){
                if (curService.id == changedServiceId){
                    val pos = services.indexOf(curService)
                    services[pos] = service
                    break
                }
            }
        }
        _services.value = services
    }

    private fun onServiceDeleted(deletedServiceId: String){
        val services = _services.value
        if (services != null) {
            for (curService in services){
                if (curService.id == deletedServiceId){
                    val pos = services.indexOf(curService)
                    services.removeAt(pos)
                    break
                }
            }
        }
        _services.value = services
    }

    private fun onDatabaseError(exception: Exception){
        intError.value = exception
    }

    init {
        listenerId = dbRepository.startListenToServices(
            ::onServiceInserted, ::onServiceUpdated, ::onServiceDeleted, ::onDatabaseError)
    }
}
