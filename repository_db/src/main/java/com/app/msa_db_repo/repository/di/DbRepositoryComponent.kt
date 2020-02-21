package com.app.msa_db_repo.repository.di

import dagger.Component

@Component(modules = [DbRepositoryModule::class])
interface DbRepositoryComponent {
    @Component.Builder
    interface Builder {
        fun build(): DbRepositoryComponent
    }
}