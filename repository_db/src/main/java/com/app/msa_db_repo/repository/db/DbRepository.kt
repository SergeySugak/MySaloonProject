package com.app.msa_db_repo.repository.db

import com.app.mscorebase.common.Result
import com.app.mscoremodels.saloon.SaloonService
import com.app.mscoremodels.saloon.ChoosableServiceDuration

interface DbRepository {
    suspend fun checkUserRoot(userId: String): Result<Boolean>
    suspend fun createUserRoot(userId: String, username: String): Result<Boolean>
    suspend fun getServiceDurations(id: Int? = null): Result<List<ChoosableServiceDuration>>
    suspend fun saveServiceInfo(service: SaloonService): Result<Boolean>
    suspend fun loadServiceInfo(serviceId: String): Result<SaloonService>
    suspend fun deleteServiceInfo(serviceId: String): Result<Boolean>

    fun startListenToServices(onInsert: (service: SaloonService)->Unit,
                              onUpdate: (updatedServiceId: String, service: SaloonService)->Unit,
                              onDelete: (deletedServiceId: String)->Unit,
                              onError: (exception: Exception)->Unit): String
    fun stopListeningToServices(listenerId: String)
}