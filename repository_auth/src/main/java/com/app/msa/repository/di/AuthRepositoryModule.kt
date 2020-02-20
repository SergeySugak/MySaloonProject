package com.app.msa.repository.di

import com.app.msa.repository.auth.AuthRepository
import com.app.msa.repository.auth.FirebaseAuthRepository
import com.app.msa.scopes.AuthScope
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides

@Module
object AuthRepositoryModule {

    @Provides
    @AuthScope
    fun provideLoginRepository(firebaseAuth: FirebaseAuth): AuthRepository =
        FirebaseAuthRepository(firebaseAuth)

    @Provides
    @AuthScope
    fun provideFirebaseAuth(): FirebaseAuth {
        val result = FirebaseAuth.getInstance()
        result.useAppLanguage()
        return result
    }
}