package com.tminus1010.budgetvalue._core.dependency_injection

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HelloModule {
    @Provides
    @Singleton
    fun provideHello(): String =
        "HelloFrom${javaClass.simpleName}"
}