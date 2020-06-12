package com.app.msa_db_repo.repository.db
import android.text.TextUtils
import com.app.msa.repository_db.R
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.common.Result
import com.app.mscoremodels.saloon.*
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.*
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("BlockingMethodInNonBlockingContext")
class FirebaseDbRepository
    @Inject constructor(private val appState: AppStateManager,
                        private val firebaseDb: FirebaseDatabase,
                        private val saloonFactory: SaloonFactory): DbRepository {
    init{
        //firebaseDb.setPersistenceEnabled(true)
    }

    private lateinit var saloonRoot: String
    private lateinit var servicesRoot: String
    private lateinit var mastersRoot: String
    private lateinit var masterServicesRoot: String
    private lateinit var eventsRoot: String
    private lateinit var dateEventsRoot: String
    private val childListenersMap = mutableMapOf<String, ChildEventListener>()
    private val valueListenersMap = mutableMapOf<String, ValueEventListener>()

    //region general
    override fun initialize(userId: String) {
        saloonRoot = "$TBL_SALOONS/${appState.authManager.getUserId()}"
        servicesRoot = "$saloonRoot/${TBL_SERVICES}"
        mastersRoot = "$saloonRoot/${TBL_MASTERS}"
        eventsRoot = "$saloonRoot/${TBL_EVENTS}"
        dateEventsRoot = "$saloonRoot/${TBL_DATE_EVENTS}"
        masterServicesRoot = "$saloonRoot/${TBL_MASTER_SERVICES}"
    }

    override suspend fun checkSaloonRoot(userId: String): Result<Boolean> {
        return firebaseDb
            .getReference(TBL_SALOONS)
            .child(userId)
            .runSuspendExistenceCheckQuery()
    }

    override suspend fun createSaloonRoot(userId: String, username: String): Result<Boolean> {
        val saloons = firebaseDb
            .getReference(TBL_SALOONS)
        try {
            saloons.child(userId).setValue(username)
        }
        catch (ex: Exception){
            return Result.Error(Exception(appState.context.getString(R.string.err_cant_create_new_key) + " $TBL_SALOONS\n" + ex.message))
        }
        return Result.Success(true)
    }
    //endregion

    //region durations
    override suspend fun getServiceDurations(id: Int?): Result<List<ServiceDuration>> {
        return try {
            val names = appState.context.resources.getStringArray(R.array.service_duration_names)
            val durations = appState.context.resources.getIntArray(R.array.service_durations)
            val result = mutableListOf<ServiceDuration>()
            for ((i, name) in names.withIndex()){
                if (id == null || id == durations[i]) {
                    val duration = saloonFactory.createServiceDuration(durations[i], name)
                    result.add(duration)
                }
            }
            Result.Success(result)
        } catch (ex: Exception){
            Result.Error(ex)
        }
    }
    //endregion

    //region services
    override suspend fun saveServiceInfo(service: SaloonService): Result<Boolean> {
        val services = firebaseDb.getReference(servicesRoot)
        if (service.id == ""){
            try {
                service.id = services.push().key!!
            }
            catch (ex: Exception){
                return Result.Error(Exception(appState.context.getString(R.string.err_cant_create_new_key) + " $TBL_SERVICES\n" + ex.message))
            }
        }
        try {
            Tasks.await(services.child(service.id).setValue(service))
        }
        catch (ex: Exception){
            return Result.Error(Exception(appState.context.getString(R.string.err_cant_update_data) + " $TBL_SERVICES\n" + ex.message))
        }
        return Result.Success(true)
    }

    override suspend fun loadServiceInfo(serviceId: String): Result<SaloonService?> {
        return firebaseDb
            .getReference(servicesRoot)
            .child(serviceId)
            .runSuspendGetValueQuery()
    }

    override suspend fun deleteServiceInfo(serviceId: String): Result<Boolean> {
        return try {
            Tasks.await(firebaseDb
                .getReference(servicesRoot)
                .child(serviceId)
                .setValue(null))
            Result.Success(true)
        } catch (ex: Exception){
            Result.Error(ex)
        }
    }

    override fun startListenToServices(onInsert: (service: SaloonService)->Unit,
                                       onUpdate: (updatedServiceId: String, service: SaloonService)->Unit,
                                       onDelete: (deletedServiceId: String)->Unit,
                                       onError: (exception: Exception)->Unit): String {
        return startListenToUpdates(servicesRoot, onInsert, onUpdate, onDelete, onError)
    }

    override fun stopListeningToServices(listenerId: String) {
        stopListenToUpdates(servicesRoot, listenerId)
    }
    //endregion Services

    //region masters
    override suspend fun saveMasterInfo(master: SaloonMaster): Result<Boolean> {
        val services = firebaseDb
            .getReference(mastersRoot)
        if (master.id == ""){
            try {
                master.id = services.push().key!!
            }
            catch (ex: Exception){
                return Result.Error(Exception(appState.context.getString(R.string.err_cant_create_new_key) + " $TBL_MASTERS\n" + ex.message))
            }
        }
        try {
            Tasks.await(services.child(master.id).setValue(master))
        }
        catch (ex: Exception){
            return Result.Error(Exception(appState.context.getString(R.string.err_cant_update_data) + " $TBL_MASTERS\n" + ex.message))
        }
        return Result.Success(true)
    }

    override suspend fun loadMasterInfo(masterId: String): Result<SaloonMaster?> {
        return firebaseDb
            .getReference(mastersRoot)
            .child(masterId)
            .runSuspendGetValueQuery()
    }

    override suspend fun deleteMasterInfo(masterId: String): Result<Boolean> {
        return try {
            Tasks.await(firebaseDb
                .getReference(mastersRoot)
                .child(masterId)
                .setValue(null))
            Result.Success(true)
        } catch (ex: Exception){
            Result.Error(ex)
        }
    }

    override fun startListenToMasters(onInsert: (master: SaloonMaster)->Unit,
                                      onUpdate: (updatedMasterId: String, master: SaloonMaster)->Unit,
                                      onDelete: (deletedMasterId: String)->Unit,
                                      onError: (exception: Exception)->Unit): String {
        return startListenToUpdates(mastersRoot, onInsert, onUpdate, onDelete, onError)
    }

    override fun stopListeningToMasters(listenerId: String) {
        stopListenToUpdates(mastersRoot, listenerId)
    }
    //endregion Masters

    //region Events
    override suspend fun loadEventInfo(eventId: String): Result<SaloonEvent?> {
        val queryResult: Result<RepositoryEvent?> = firebaseDb
            .getReference(eventsRoot)
            .child(eventId)
            .runSuspendGetValueQuery()
        if (queryResult is Result.Error){
            return Result.Error(queryResult.exception)
        }
        else {
            val rEvent = (queryResult as Result.Success).data
            val masterId = rEvent?.masterId ?: ""
            val masterResult = loadMasterInfo(masterId)
            val master: SaloonMaster
            if (masterResult is Result.Error){
                return Result.Error(masterResult.exception)
            }
            else {
                master = (masterResult as Result.Success).data ?:
                        saloonFactory.createSaloonMaster(masterId, "",  "")
            }
            val services = mutableListOf<SaloonService>()
            rEvent?.serviceIds?.forEach { id ->
                val serviceResult = loadServiceInfo(id)
                if (serviceResult is Result.Success && serviceResult.data != null){
                    services.add(serviceResult.data!!)
                }
            }
            val client = rEvent?.client ?: saloonFactory.createSaloonClient("", "", "")
            val whenStart = rEvent?.whenStart ?: Calendar.getInstance()
            val whenFinish = rEvent?.whenFinish ?: Calendar.getInstance()
            val description = rEvent?.description ?: ""
            val state = rEvent?.state ?: SaloonEventState.esError
            val event = saloonFactory.createSaloonEvent(rEvent?.id ?: "", master, services,
                client, whenStart, whenFinish, description, state)
            return Result.Success(event)
        }
    }

    private fun indexEvent(event: SaloonEvent): Result<Boolean> {
        try {
            val dateEvents = firebaseDb
                .getReference(dateEventsRoot)
            val dateRoot = getDateRoot(event.whenStart)
            val key = dateEvents.child(dateRoot).push().key!!
            Tasks.await(dateEvents.child(dateRoot).child(key).setValue(event.id))
            return Result.Success(true)
        }
        catch (ex: Exception){
            return Result.Error(Exception(appState.context.getString(R.string.err_cant_update_data) + " ${TBL_DATE_EVENTS}\n" + ex.message))
        }
    }

    override suspend fun saveEventInfo(event: SaloonEvent): Result<Boolean>{
        val events = firebaseDb
            .getReference(eventsRoot)
        if (event.id == ""){
            try {
                event.id = events.push().key!!
            }
            catch (ex: Exception){
                return Result.Error(Exception(appState.context.getString(R.string.err_cant_create_new_key) + " $TBL_EVENTS\n" + ex.message))
            }
        }
        try {
            Tasks.await(events.child(event.id).setValue(RepositoryEvent(event)))
        }
        catch (ex: Exception){
            return Result.Error(Exception(appState.context.getString(R.string.err_cant_update_data) + " $TBL_EVENTS\n" + ex.message))
        }
        return indexEvent(event)
    }

    override suspend fun deleteEventInfo(eventId: String): Result<Boolean>{
        return try {
            Tasks.await(firebaseDb
                .getReference(eventsRoot)
                .child(eventId)
                .setValue(null))
            Result.Success(true)
        } catch (ex: Exception){
            Result.Error(ex)
        }
    }

    private suspend fun loadDateEventIds(date: Calendar): Result<List<String>> {
        val dateRoot = getDateRoot(date)
        return firebaseDb
            .getReference(dateEventsRoot)
            .child(dateRoot)
            .runSuspendGetListQuery()
    }

    private fun getDateRoot(date: Calendar) = "${date.get(Calendar.YEAR)}${date.get(Calendar.MONTH)}${date.get(Calendar.DATE)}"

    override suspend fun getEvents(date: Calendar): Result<List<SaloonEvent>>{
        val idsResult = loadDateEventIds(date)
        if (idsResult is Result.Success){
            val result = mutableListOf<SaloonEvent>()
            var eventLoadResult: Result<SaloonEvent?>
            var event: SaloonEvent?
            idsResult.data.forEach{
                eventLoadResult = loadEventInfo(it)
                if (eventLoadResult is Result.Success){
                    event = (eventLoadResult as Result.Success).data
                    event?.let{ e -> result.add(e) }
                }
                else {
                    return Result.Error((eventLoadResult as Result.Error).exception)
                }
            }
            return Result.Success(result)
        }
        else {
            idsResult as Result.Error
            return Result.Error(idsResult.exception)
        }
    }



    override fun startListenToEvents(onInsert: (event: SaloonEvent)->Unit,
                            onUpdate: (updatedEventId: String, event: SaloonEvent)->Unit,
                            onDelete: (deletedMasterId: String)->Unit,
                            onError: (exception: Exception)->Unit): String{
        return startListenToUpdates(eventsRoot, onInsert, onUpdate, onDelete, onError)
    }

    override fun stopListeningToEvents(listenerId: String){
        stopListenToUpdates(eventsRoot, listenerId)
    }
    //endregion Events

    //region master services
    override suspend fun getServices(masterId: String?): Result<List<SaloonService>> {
        return if (TextUtils.isEmpty(masterId))
            firebaseDb.getReference(servicesRoot).runSuspendGetListQuery()
        else {
            val masterServicesResult = firebaseDb.getReference(masterServicesRoot)
                .child(masterId!!)
                .runSuspendGetListQuery<MasterService>()
            if (masterServicesResult is Result.Success){
                convertMasterServicesToSaloonServices(masterServicesResult.data)
            }
            else {
                masterServicesResult as Result.Error
                Result.Error(masterServicesResult.exception)
            }
        }
    }

    override suspend fun getMasters(requiredServices: List<SaloonService>?): Result<List<SaloonMaster>> {
        var result: Result<List<SaloonMaster>> = firebaseDb.getReference(mastersRoot).runSuspendGetListQuery()
        if (result is Result.Success && requiredServices != null){
            val masters = result.data.filter { master ->
                val servicesQueryResult = getServices(master.id)
                if (servicesQueryResult is Result.Error){
                    return@getMasters servicesQueryResult
                }
                servicesQueryResult as Result.Success
                servicesQueryResult.data.containsAll(requiredServices)
            }
            result = Result.Success(masters)
        }
        return result
    }

    override suspend fun saveMasterServicesInfo(masterId: String, services: List<SaloonService>): Result<Boolean> {
        return try {
            val masterServices = convertSaloonServicesToMasterServices(masterId, services)
            Tasks.await(firebaseDb.getReference(masterServicesRoot).child(masterId).setValue(masterServices))
            Result.Success(true)
        } catch (ex: Exception){
            Result.Error(Exception(appState.context.getString(R.string.err_cant_update_data) + " $TBL_MASTERS\n" + ex.message))
        }
    }

    private fun convertSaloonServicesToMasterServices(masterId: String, saloonServices: List<SaloonService>?): List<MasterService>{
        val result = mutableListOf<MasterService>()
        saloonServices?.forEach{ saloonService ->
            result.add(MasterService(masterId, saloonService.id))
        }
        return result
    }

    private suspend fun convertMasterServicesToSaloonServices(masterServices: List<MasterService>?): Result<List<SaloonService>>{
        val result = mutableListOf<SaloonService>()
        masterServices?.forEach{ masterService ->
            val serviceResult = loadServiceInfo(masterService.serviceId)
            if (serviceResult is Result.Success) {
                val saloonService = serviceResult.data
                if (saloonService != null) {
                    result.add(SaloonService(masterService.serviceId, saloonService.name,
                        saloonService.price, saloonService.duration, saloonService.description,
                        saloonService.imageUrl))
                }
            }
            else {
                serviceResult as Result.Error
                return Result.Error(serviceResult.exception)
            }
        }
        return Result.Success(result)
    }
    //endregion

    //region helpers
    private inline fun <reified T: Any>
            startListenToUpdates(path: String,
                                 crossinline onInsert: (item: T)->Unit,
                                 crossinline onUpdate: (updatedItemId: String, item: T)->Unit,
                                 crossinline onDelete: (deletedItemId: String)->Unit,
                                 crossinline onError: (exception: Exception)->Unit): String {
        val data = firebaseDb.getReference(path)
        val listenerId = UUID.randomUUID().toString()
        childListenersMap[listenerId] = data.addChildEventListener(object: ChildEventListener {
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val item = dataSnapshot.getValue(T::class.java)
                item?.let { onInsert(it) }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val item = dataSnapshot.getValue(T::class.java)
                dataSnapshot.key?.let { key ->
                    item?.let { item ->
                        onUpdate(key, item) }
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                dataSnapshot.key?.let {
                    onDelete(it)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                onError(p0.toException())
            }
        })
        return listenerId
    }

    private fun stopListenToUpdates(path: String, listenerId: String) {
        val data = firebaseDb.getReference(path)
        childListenersMap[listenerId]?.let { data.removeEventListener(it) } ?:
        valueListenersMap[listenerId]?.let { data.removeEventListener(it) }
    }


    private suspend fun Query.runSuspendExistenceCheckQuery(): Result<Boolean> =
        suspendCoroutine { continuation ->
        this.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                continuation.resume(Result.Success(p0.exists()))
            }

            override fun onCancelled(p0: DatabaseError) {
                continuation.resume(Result.Error(p0.toException()))
            }
        })
    }

    private suspend inline fun <reified T: Any?>
            Query.runSuspendGetValueQuery(): Result<T?> =
        suspendCoroutine { continuation ->
        this.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                continuation.resume(Result.Success(p0.getValue(T::class.java)))
            }

            override fun onCancelled(p0: DatabaseError) {
                continuation.resume(Result.Error(p0.toException()))
            }
        })
    }

    private suspend inline fun <reified T: Any>
            Query.runSuspendGetListQuery(): Result<List<T>> =
        suspendCoroutine { continuation ->
            this.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val result = mutableListOf<T>()
                    for (data in p0.children){
                        val value = data.getValue(T::class.java)
                        if (value != null) {
                            result.add(value)
                        }
                    }
                    continuation.resume(Result.Success(result))
                }

                override fun onCancelled(p0: DatabaseError) {
                    continuation.resume(Result.Error(p0.toException()))
                }
            })
        }

    private suspend inline fun <T>
            Query.runSuspendGetValueQuery(gti: GenericTypeIndicator<T>): Result<T?> =
        suspendCoroutine { continuation ->
        this.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                continuation.resume(Result.Success(p0.getValue(gti)!!))
            }

            override fun onCancelled(p0: DatabaseError) {
                continuation.resume(Result.Error(p0.toException()))
            }
        })
    }
    //endregion

    data class MasterService(val masterId: String, val serviceId: String){
        constructor():this("", "")
    }

    companion object {
        const val TBL_SALOONS = "Saloons"
        const val TBL_SERVICES = "Services"
        const val TBL_MASTERS = "Masters"
        const val TBL_EVENTS = "Events"
        const val TBL_DATE_EVENTS = "DateEvents"
        const val TBL_MASTER_SERVICES = "MasterServices"
    }
}