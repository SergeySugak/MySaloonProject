package com.app.msa_auth_repo.repository.di

import com.app.msa_auth_repo.repository.auth.AuthRepository
import com.app.msa_auth_repo.repository.auth.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AuthRepositoryModule {

    @Provides
    @Singleton
    fun provideLoginRepository(firebaseAuth: FirebaseAuth): AuthRepository =
        FirebaseAuthRepository(firebaseAuth)

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance().apply { useAppLanguage() }
    }
}
