package com.app.msa_db_repo.repository.db

import com.app.mscorebase.common.Result
import com.app.mscoremodels.saloon.*
import java.util.*

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

    fun startListenToServices(
        onInsert: (service: SaloonService) -> Unit,
        onUpdate: (updatedServiceId: String, service: SaloonService) -> Unit,
        onDelete: (deletedServiceId: String) -> Unit,
        onError: (exception: Exception) -> Unit
    ): String

    fun stopListeningToServices(listenerId: String)

    //Мастера
    suspend fun saveMasterInfo(master: SaloonMaster): Result<Boolean>
    suspend fun loadMasterInfo(masterId: String): Result<SaloonMaster?>
    suspend fun deleteMasterInfo(masterId: String): Result<Boolean>
    suspend fun getMasters(requiredServices: List<SaloonService>?): Result<List<SaloonMaster>>

    fun startListenToMasters(
        onInsert: (master: SaloonMaster) -> Unit,
        onUpdate: (updatedMasterId: String, master: SaloonMaster) -> Unit,
        onDelete: (deletedMasterId: String) -> Unit,
        onError: (exception: Exception) -> Unit
    ): String

    fun stopListeningToMasters(listenerId: String)

    //Расходники
    suspend fun saveConsumableInfo(consumable: SaloonConsumable): Result<Boolean>
    suspend fun loadConsumableInfo(consumableId: String): Result<SaloonConsumable?>
    suspend fun deleteConsumableInfo(consumableId: String): Result<Boolean>
    fun startListenToConsumables(
        onInsert: (consumable: SaloonConsumable) -> Unit,
        onUpdate: (updatedConsumableId: String, consumable: SaloonConsumable) -> Unit,
        onDelete: (deletedConsumableId: String) -> Unit,
        onError: (exception: Exception) -> Unit
    ): String
    fun stopListeningToConsumables(listenerId: String)

    //Мастер-Сервис
    suspend fun getServices(masterId: String? = null): Result<List<SaloonService>>
    suspend fun saveMasterServicesInfo(
        masterId: String,
        services: List<SaloonService>
    ): Result<Boolean>

    //события
    suspend fun loadEventInfo(eventId: String): Result<SaloonEvent?>
    suspend fun saveEventInfo(event: SaloonEvent): Result<Boolean>
    suspend fun deleteEventInfo(event: SaloonEvent): Result<Boolean>
    suspend fun getEvents(date: Calendar): Result<List<SaloonEvent>>
    suspend fun getAllEvents(): Result<List<SaloonEvent>>

    fun startListenToEvents(
        onInsert: (event: SaloonEvent) -> Unit,
        onUpdate: (updatedEventId: String, event: SaloonEvent) -> Unit,
        onDelete: (deletedMasterId: String) -> Unit,
        onError: (exception: Exception) -> Unit
    ): String

    fun stopListeningToEvents(listenerId: String)

    suspend fun serviceHasRelatedEvent(serviceId: String): Result<Boolean>
    suspend fun masterHasRelatedEvent(masterId: String): Result<Boolean>
    suspend fun consumableHasRelatedEvent(consumableId: String): Result<Boolean>
    suspend fun getUoms(): Result<List<String>>
    suspend fun getConsumablesAsUsed(): Result<List<SaloonUsedConsumable>>
}