package com.app.msa_auth_repo.repository.di

import com.app.msa_auth_repo.repository.auth.AuthRepository
import com.app.msa_auth_repo.repository.auth.FirebaseAuthRepository
import com.app.msa_scopes.scopes.FeatureScope
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides

@Module
object AuthRepositoryModule {

    @Provides
    @FeatureScope
    fun provideLoginRepository(firebaseAuth: FirebaseAuth): AuthRepository =
        FirebaseAuthRepository(firebaseAuth)

    @Provides
    @FeatureScope
    fun provideFirebaseAuth(): FirebaseAuth {
        val result = FirebaseAuth.getInstance()
        result.useAppLanguage()
        return result
    }
}