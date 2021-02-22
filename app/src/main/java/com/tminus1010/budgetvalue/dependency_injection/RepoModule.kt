package com.tminus1010.budgetvalue.dependency_injection

import android.content.Context
import android.content.SharedPreferences
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.SHARED_PREF_FILE_NAME
import com.tminus1010.budgetvalue.layer_data.ActiveCategoryDAO
import com.tminus1010.budgetvalue.layer_data.ActiveCategoryDAOWrapper
import com.tminus1010.budgetvalue.layer_data.BudgetValueDB
import com.tminus1010.budgetvalue.layer_data.MiscDAO
import com.tminus1010.budgetvalue.model_app.ICategoryParser
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepoModule {
    @Provides
    @Singleton
    fun providesCategoryParser(activeCategoryDAOWrapper: ActiveCategoryDAOWrapper): ICategoryParser {
        return activeCategoryDAOWrapper
    }

    @Provides
    @Singleton
    fun providesSharedPreferences(app: App): SharedPreferences {
        return app.getSharedPreferences(
            SHARED_PREF_FILE_NAME,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun providesMyDao(roomDatabase: BudgetValueDB): MiscDAO {
        return roomDatabase.myDao()
    }

    @Provides
    @Singleton
    fun providesActiveCategoryDAO(roomDatabase: BudgetValueDB): ActiveCategoryDAO {
        return roomDatabase.activeCategoryDAO()
    }
}