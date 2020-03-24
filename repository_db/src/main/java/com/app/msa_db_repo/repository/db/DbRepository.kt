package com.app.msa_db_repo.repository.db

import com.app.mscorebase.common.Result
import com.app.mscoremodels.saloon.SaloonService
import com.app.mscoremodels.saloon.ServiceDuration

interface DbRepository {
    suspend fun checkUserRoot(userId: String): Result<Boolean>
    suspend fun createUserRoot(userId: String, username: String): Result<Boolean>
    suspend fun getServiceDurations(): Result<List<ServiceDuration>>
    suspend fun saveServiceInfo(service: SaloonService): Result<Boolean>

    fun startListenToServices(onInsert: (service: SaloonService)->Unit,
                              onUpdate: (updatedServiceId: String, service: SaloonService)->Unit,
                              onDelete: (deletedServiceId: String)->Unit,
                              onError: (exception: Exception)->Unit): String
    fun stopListeningToServices(listenerId: String)
}