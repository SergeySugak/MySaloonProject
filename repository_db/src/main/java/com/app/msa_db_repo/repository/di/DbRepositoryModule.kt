package com.app.msa_db_repo.repository.di

import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DbRepositoryModule {

    @Provides
    @Singleton
    internal fun provideFirebaseDatabase() = FirebaseDatabase.getInstance()
}