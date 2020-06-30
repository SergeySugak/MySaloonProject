package com.app.msa_auth_repo.repository.auth

import android.content.SharedPreferences
import com.app.mscorebase.common.Result
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Named

@Suppress("BlockingMethodInNonBlockingContext")
class FirebaseAuthRepository
@Inject constructor(private val firebaseAuth: FirebaseAuth,
                    @Named("LOGIN")
                    private val loginSharedPrefs: SharedPreferences
) :
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

    override fun storeUserName(userName: String){
        loginSharedPrefs.edit().putString(SP_LOGIN_USER_NAME, userName).apply()
    }

    override fun reStoreUserName(): String {
        return loginSharedPrefs.getString(SP_LOGIN_USER_NAME, "") ?: ""
    }

    companion object {
        private const val SP_LOGIN_USER_NAME = "SP_LOGIN_USER_NAME"
    }
}