package com.tminus1010.budgetvalue._core.dependency_injection

import android.content.Context
import android.content.SharedPreferences
import com.tminus1010.budgetvalue._core.App
import com.tminus1010.budgetvalue._core.SHARED_PREF_FILE_NAME
import com.tminus1010.budgetvalue._core.data.UserCategoriesDAO
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue._core.data.DB
import com.tminus1010.budgetvalue._core.data.MiscDAO
import com.tminus1010.budgetvalue.categories.ICategoryParser
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object RepoModule {
    @Provides
    @Singleton
    @JvmStatic
    fun providesCategoryParser(categoriesVM: CategoriesVM): ICategoryParser = categoriesVM

    @Provides
    @Singleton
    @JvmStatic
    fun providesSharedPreferences(app: App): SharedPreferences {
        return app.getSharedPreferences(
            SHARED_PREF_FILE_NAME,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    @JvmStatic
    fun providesMyDao(roomDatabase: DB): MiscDAO = roomDatabase.miscDAO()

    @Provides
    @Singleton
    @JvmStatic
    fun providesActiveCategoryDAO(roomDatabase: DB): UserCategoriesDAO = roomDatabase.activeCategoryDAO()
}