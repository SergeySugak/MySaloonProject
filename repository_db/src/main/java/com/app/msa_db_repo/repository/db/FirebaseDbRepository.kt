package com.app.msa.repository.auth
import com.app.msa_db_repo.repository.db.DbRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

@Suppress("BlockingMethodInNonBlockingContext")
class FirebaseDbRepository
    @Inject constructor(private val firebaseDb: FirebaseDatabase): DbRepository {

    private val db = firebaseDb.reference

    init{
        firebaseDb.setPersistenceEnabled(true);
    }
}