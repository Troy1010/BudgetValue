package com.tminus1010.budgetvalue.dependency_injection

import android.content.Context
import android.content.SharedPreferences
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.SHARED_PREF_FILE_NAME
import com.tminus1010.budgetvalue.layer_data.*
import com.tminus1010.budgetvalue.layer_ui.CategoriesAppVM
import com.tminus1010.budgetvalue.model_app.ICategoryParser
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepoModule {
    @Provides
    @Singleton
    fun providesCategoriesAppVM(): CategoriesAppVM {
        return CategoriesAppVM()
    }

    @Provides
    @Singleton
    fun providesParseCategory(activeCategoryDAOWrapper: ActiveCategoryDAOWrapper): ICategoryParser {
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
    fun providesMyDao(roomDatabase: BudgetValueDB): MyDao {
        return roomDatabase.myDao()
    }

    @Provides
    @Singleton
    fun providesActiveCategoryDAO(roomDatabase: BudgetValueDB): ActiveCategoryDAO {
        return roomDatabase.activeCategoryDAO()
    }

    @Provides
    @Singleton
    fun providesRepo(myDaoWrapper: MyDaoWrapper, sharedPrefWrapper: SharedPrefWrapper, transactionParser: TransactionParser, activeCategoryDAOWrapper: ActiveCategoryDAOWrapper): Repo {
        return Repo(
            transactionParser,
            sharedPrefWrapper,
            myDaoWrapper,
            activeCategoryDAOWrapper
        )
    }
}