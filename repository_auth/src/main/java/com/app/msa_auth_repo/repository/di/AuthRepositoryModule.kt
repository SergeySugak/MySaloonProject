package com.app.msa_auth_repo.repository.di

import android.content.Context
import android.content.SharedPreferences
import com.app.msa_auth_repo.repository.auth.AuthRepository
import com.app.msa_auth_repo.repository.auth.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
object AuthRepositoryModule {
    private val LOGIN_SHARED_PREFS = "MSA_LOGIN_SHARED_PREFS"

    @Provides
    @Singleton
    fun provideLoginRepository(firebaseAuth: FirebaseAuth,
                               @Named("LOGIN") loginSharedPrefs: SharedPreferences): AuthRepository =
        FirebaseAuthRepository(firebaseAuth, loginSharedPrefs)

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance().apply { useAppLanguage() }
    }

    @Provides
    @Singleton
    @Named("LOGIN")
    fun provideLoginSharedPrefs(context: Context) =
        context.getSharedPreferences(LOGIN_SHARED_PREFS, Context.MODE_PRIVATE)
}
