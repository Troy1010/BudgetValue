package com.tminus1010.budgetvalue.dependency_injection

import android.content.Context
import android.content.SharedPreferences
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.SHARED_PREF_FILE_NAME
import com.tminus1010.budgetvalue.layer_data.UserCategoriesDAO
import com.tminus1010.budgetvalue.layer_domain.CategoriesUCWrapper
import com.tminus1010.budgetvalue.layer_data.DB
import com.tminus1010.budgetvalue.layer_data.MiscDAO
import com.tminus1010.budgetvalue.layer_domain.ICategoryParser
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object RepoModule {
    @Provides
    @Singleton
    @JvmStatic
    fun providesCategoryParser(activeCategoryDAOWrapper: CategoriesUCWrapper): ICategoryParser = activeCategoryDAOWrapper

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