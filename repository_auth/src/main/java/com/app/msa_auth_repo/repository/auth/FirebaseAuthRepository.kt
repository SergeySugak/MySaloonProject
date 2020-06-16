package com.app.msa_auth_repo.repository.auth

import com.app.mscorebase.common.Result
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

@Suppress("BlockingMethodInNonBlockingContext")
class FirebaseAuthRepository
@Inject constructor(private val firebaseAuth: FirebaseAuth) :
    AuthRepository {

    override suspend fun isAccountRegistered(userName: String): Result<Boolean> {
        try {
            val taskResult = Tasks.await(firebaseAuth.fetchSignInMethodsForEmail(userName))
            taskResult?.let { return Result.Success(taskResult.signInMethods?.size ?: 0 > 0) }
                ?: return Result.Error(Exception())
        } catch (ex: Exception) {
            return Result.Error(ex)
        }
    }

    override suspend fun createAccount(userName: String, password: String): Result<Boolean> {
        try {
            val taskResult =
                Tasks.await(firebaseAuth.createUserWithEmailAndPassword(userName, password))
            sendVerificationEmail()
            taskResult?.let { return Result.Success(true) } ?: return Result.Error(Exception())
        } catch (ex: Exception) {
            return Result.Error(ex)
        }
    }

    private fun sendVerificationEmail(): Result<Boolean> {
        return try {
            firebaseAuth.currentUser?.sendEmailVerification()
            Result.Success(true)
        } catch (ex: Exception) {
            Result.Error(ex)
        }
    }

    override suspend fun login(userName: String, password: String): Result<String> {
        try {
            val taskResult =
                Tasks.await(firebaseAuth.signInWithEmailAndPassword(userName, password))
            taskResult?.let {
                return it.user?.let { user ->
                    return Result.Success(user.uid)
                } ?: return Result.Error(Exception("User id is null"))
            } ?: return Result.Error(Exception("Can't login to authentication server"))
        } catch (ex: Exception) {
            return Result.Error(ex)
        }
    }

    override fun getUserId() = firebaseAuth.currentUser?.uid ?: ""

    override fun logout() {
        firebaseAuth.signOut()
    }
}