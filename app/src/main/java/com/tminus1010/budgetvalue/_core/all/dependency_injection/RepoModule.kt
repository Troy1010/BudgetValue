package com.tminus1010.budgetvalue._core.all.dependency_injection

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.tminus1010.budgetvalue._core.SHARED_PREF_FILE_NAME
import com.tminus1010.budgetvalue._core.data.MiscDB
import com.tminus1010.budgetvalue._core.data.MiscDAO
import com.tminus1010.budgetvalue._core.data.UserCategoriesDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {
    @Provides
    @Singleton
    fun providesSharedPreferences(application: Application): SharedPreferences =
        application.getSharedPreferences(
            SHARED_PREF_FILE_NAME,
            Context.MODE_PRIVATE
        )

    @Provides
    @Singleton
    fun providesMyDao(roomDatabase: MiscDB): MiscDAO = roomDatabase.miscDAO()

    @Provides
    @Singleton
    fun providesActiveCategoryDAO(miscDB: MiscDB): UserCategoriesDAO = miscDB.userCategoriesDAO()
}