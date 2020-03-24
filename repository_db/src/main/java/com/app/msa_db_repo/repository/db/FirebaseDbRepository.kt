package com.app.msa_db_repo.repository.db
import android.content.Context
import com.app.msa.repository_db.R
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.common.Result
import com.app.mscoremodels.saloon.SaloonFactory
import com.app.mscoremodels.saloon.SaloonService
import com.app.mscoremodels.saloon.ServiceDuration
import com.google.firebase.database.*
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("BlockingMethodInNonBlockingContext")
class FirebaseDbRepository
    @Inject constructor(private val context: Context,
                        private val appState: AppStateManager,
                        private val firebaseDb: FirebaseDatabase,
                        private val saloonFactory: SaloonFactory): DbRepository {
    init{
        //firebaseDb.setPersistenceEnabled(true)
    }

    private val childListenersMap = mutableMapOf<String, ChildEventListener>()
    private val valueListenersMap = mutableMapOf<String, ValueEventListener>()

    override suspend fun checkUserRoot(userId: String): Result<Boolean> {
        return firebaseDb
            .getReference(TBL_SALOONS)
            .child(userId)
            .runSuspendQuery()
    }

    private suspend fun Query.runSuspendQuery(): Result<Boolean> = suspendCoroutine { continuation ->
        this.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                continuation.resume(Result.Success(p0.exists()))
            }

            override fun onCancelled(p0: DatabaseError) {
                continuation.resume(Result.Error(p0.toException()))
            }
        })
    }

    override suspend fun createUserRoot(userId: String, username: String): Result<Boolean> {
        val saloons = firebaseDb
            .getReference(TBL_SALOONS)
        try {
            saloons.child(userId).setValue(username)
        }
        catch (ex: Exception){
            return Result.Error(Exception(context.getString(R.string.err_cant_create_new_key) + " $TBL_SALOONS\n" + ex.message))
        }
        return Result.Success(true)
    }

    override suspend fun getServiceDurations(): Result<List<ServiceDuration>> {
        return try {
            val names = context.resources.getStringArray(R.array.service_duration_names)
            val durations = context.resources.getIntArray(R.array.service_durations)
            val result = mutableListOf<ServiceDuration>()
            for ((i, name) in names.withIndex()){
                result.add(ServiceDuration(durations[i], name))
            }
            Result.Success(result)
        } catch (ex: Exception){
            Result.Error(ex)
        }
    }

    override suspend fun saveServiceInfo(service: SaloonService): Result<Boolean> {
        val services = firebaseDb
            .getReference(TBL_SERVICES)
        if (service.id == ""){
            try {
                service.id = services.push().key!!
            }
            catch (ex: Exception){
                return Result.Error(Exception(context.getString(R.string.err_cant_create_new_key) + " $TBL_SERVICES\n" + ex.message))
            }
        }
        try {
            services.child(service.id).setValue(service)
        }
        catch (ex: Exception){
            return Result.Error(Exception(context.getString(R.string.err_cant_update_data) + " $TBL_SERVICES\n" + ex.message))
        }
        return Result.Success(true)
    }

    override fun startListenToServices(onInsert: (service: SaloonService)->Unit,
                                       onUpdate: (updatedServiceId: String, service: SaloonService)->Unit,
                                       onDelete: (deletedServiceId: String)->Unit,
                                       onError: (exception: Exception)->Unit): String {
        return startListenToUpdates(TBL_SERVICES, onInsert, onUpdate, onDelete, onError)
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
                item?.let{onInsert(it)}
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

    override fun stopListeningToServices(listenerId: String) {
        stopListenToUpdates(TBL_SERVICES, listenerId)
    }

    private fun stopListenToUpdates(path: String, listenerId: String) {
        val data = firebaseDb.getReference(path)
        childListenersMap[listenerId]?.let { data.removeEventListener(it) } ?:
        valueListenersMap[listenerId]?.let { data.removeEventListener(it) }
    }

    companion object {
        const val TBL_SALOONS = "Saloons"
        const val TBL_SERVICES = "Services"
    }
}