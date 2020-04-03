package com.app.msa_db_repo.repository.db

import com.app.mscorebase.common.Result
import com.app.mscoremodels.saloon.*

interface DbRepository {
    //Общие
    fun initialize(userId: String)
    suspend fun checkSaloonRoot(userId: String): Result<Boolean>
    suspend fun createSaloonRoot(userId: String, username: String): Result<Boolean>

    //Длительности
    suspend fun getServiceDurations(id: Int? = null): Result<List<ServiceDuration>>

    //Сервисы
    suspend fun saveServiceInfo(service: SaloonService): Result<Boolean>
    suspend fun loadServiceInfo(serviceId: String): Result<SaloonService?>
    suspend fun deleteServiceInfo(serviceId: String): Result<Boolean>

    fun startListenToServices(onInsert: (service: SaloonService)->Unit,
                              onUpdate: (updatedServiceId: String, service: SaloonService)->Unit,
                              onDelete: (deletedServiceId: String)->Unit,
                              onError: (exception: Exception)->Unit): String
    fun stopListeningToServices(listenerId: String)

    //Мастера
    suspend fun saveMasterInfo(master: SaloonMaster): Result<Boolean>
    suspend fun loadMasterInfo(masterId: String): Result<SaloonMaster?>
    suspend fun deleteMasterInfo(masterId: String): Result<Boolean>

    fun startListenToMasters(onInsert: (master: SaloonMaster)->Unit,
                             onUpdate: (updatedMasterId: String, master: SaloonMaster)->Unit,
                             onDelete: (deletedMasterId: String)->Unit,
                             onError: (exception: Exception)->Unit): String
    fun stopListeningToMasters(listenerId: String)

    //Мастер-Сервис
    suspend fun getServices(masterId: String? = null): Result<List<SaloonService>>
    suspend fun saveMasterServicesInfo(masterId: String, services: List<SaloonService>): Result<Boolean>
}