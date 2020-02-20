package com.app.msa.repository.auth
import com.app.mscorebase.common.Result
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

@Suppress("BlockingMethodInNonBlockingContext")
class FirebaseDbRepository
    @Inject constructor(private val firebaseAuth: FirebaseAuth): DbRepository {

}