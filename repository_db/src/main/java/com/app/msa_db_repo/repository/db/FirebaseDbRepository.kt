package com.app.msa_db_repo.repository.db
import android.content.Context
import com.app.msa.repository_db.R
import com.app.mscoremodels.services.ServiceDuration
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

@Suppress("BlockingMethodInNonBlockingContext")
class FirebaseDbRepository
    @Inject constructor(private val context: Context,
                        private val firebaseDb: FirebaseDatabase): DbRepository {
    init{
        firebaseDb.setPersistenceEnabled(true);
    }
    private val db = firebaseDb.reference

    override fun getServiceDurations(): List<ServiceDuration> {
        val names = context.resources.getStringArray(R.array.service_duration_names)
        val durations = context.resources.getIntArray(R.array.service_durations)
        val result = mutableListOf<ServiceDuration>()
        for ((i, name) in names.withIndex()){
            result.add(ServiceDuration(name, durations[i]))
        }
        return result
    }
}