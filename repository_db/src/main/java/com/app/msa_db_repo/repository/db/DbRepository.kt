package com.app.msa_db_repo.repository.db

import com.app.mscorebase.common.Result
import com.app.mscoremodels.saloon.SaloonService
import com.app.mscoremodels.saloon.ServiceDuration

interface DbRepository {
    suspend fun checkUserRoot(userId: String): Result<Boolean>
    suspend fun createUserRoot(userId: String, username: String): Result<Boolean>
    fun getServiceDurations(): Result<List<ServiceDuration>>
    suspend fun saveServiceInfo(service: SaloonService): Result<Boolean>
}