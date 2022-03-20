package com.tminus1010.budgetvalue.all_features.all_layers.dependency_injection

import com.tminus1010.budgetvalue.all_features.data.CategoryDatabase
import com.tminus1010.budgetvalue.all_features.data.MiscDAO
import com.tminus1010.budgetvalue.all_features.data.MiscDatabase
import com.tminus1010.budgetvalue.all_features.data.UserCategoriesDAO
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
    fun providesMiscDao(roomDatabase: MiscDatabase): MiscDAO = roomDatabase.miscDAO()

    @Provides
    @Singleton
    fun provideCategoryDatabase(categoryDatabase: CategoryDatabase): UserCategoriesDAO = categoryDatabase.userCategoriesDAO()
}