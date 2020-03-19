package com.app.msa_db_repo.repository.db
import android.content.Context
import com.app.msa.repository_db.R
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.common.Result
import com.app.mscoremodels.saloon.SaloonFactory
import com.app.mscoremodels.saloon.SaloonService
import com.app.mscoremodels.saloon.ServiceDuration
import com.google.firebase.database.*
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

    override fun getServiceDurations(): Result<List<ServiceDuration>> {
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

        return Result.Success(true)
    }

    companion object {
        const val TBL_SALOONS = "Saloons"
    }
}