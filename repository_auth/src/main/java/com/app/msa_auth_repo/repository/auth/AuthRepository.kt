package com.app.msa_auth_repo.repository.auth

import com.app.mscorebase.common.Result

interface AuthRepository {
    fun getUserId(): String
    suspend fun isAccountRegistered(userName: String): Result<Boolean>
    suspend fun createAccount(userName: String, password: String): Result<Boolean>
    suspend fun login(userName: String, password: String): Result<String>
    fun logout()
    fun storeUserName(userName: String)
    fun reStoreUserName(): String
}