package com.app.msa_db_repo.repository.db

import com.app.mscoremodels.services.ServiceDuration

interface DbRepository {
    fun getServiceDurations(): List<ServiceDuration>
    fun saveServiceInfo(name: String, duration: Int, price: Double, description: String): Boolean
}