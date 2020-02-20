package com.app.msa.repository.di

import dagger.Component

@Component(modules = [DbRepositoryModule::class])
interface DbRepositoryComponent {
    @Component.Builder
    interface Builder {
        fun build(): DbRepositoryComponent
    }
}