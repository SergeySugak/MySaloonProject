package com.app.msa_db_repo.repository.db
import android.text.TextUtils
import com.app.msa.repository_db.R
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.common.Result
import com.app.mscoremodels.saloon.SaloonFactory
import com.app.mscoremodels.saloon.SaloonMaster
import com.app.mscoremodels.saloon.SaloonService
import com.app.mscoremodels.saloon.ServiceDuration
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

    private val saloonRoot: String = "$TBL_SALOONS/${appState.authManager.getUserId()}"
    private val servicesRoot: String = "$saloonRoot/${TBL_SERVICES}"
    private val mastersRoot: String = "$saloonRoot/${TBL_MASTERS}"
    private val childListenersMap = mutableMapOf<String, ChildEventListener>()
    private val valueListenersMap = mutableMapOf<String, ValueEventListener>()

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

    //region Services
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

    //region Masters start
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

    override suspend fun getServices(masterId: String?): Result<List<SaloonService>> {
        return if (TextUtils.isEmpty(masterId))
            firebaseDb.getReference(servicesRoot).runSuspendGetListQuery()
        else
            Result.Success(emptyList())
//            firebaseDb.getReference(servicesRoot)
//            .child(masterId)
//            .runSuspendGetValueQuery()
    }

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


    companion object {
        const val TBL_SALOONS = "Saloons"
        const val TBL_SERVICES = "Services"
        const val TBL_MASTERS = "Masters"
    }
}