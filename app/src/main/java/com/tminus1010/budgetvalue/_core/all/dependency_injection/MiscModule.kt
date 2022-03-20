package com.tminus1010.budgetvalue._core.all.dependency_injection

import com.tminus1010.budgetvalue._core.data.MiscDAO
import com.tminus1010.budgetvalue._core.data.MiscDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MiscModule {
    @Provides
    @Singleton
    fun provideErrorSubject(): Subject<Throwable> = PublishSubject.create()

    @Provides
    @Singleton
    fun providesMyDao(roomDatabase: MiscDatabase): MiscDAO = roomDatabase.miscDAO()
}