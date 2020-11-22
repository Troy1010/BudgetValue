package com.example.budgetvalue.dependency_injection

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.budgetvalue.SHARED_PREF_FILE_NAME
import com.example.budgetvalue.layer_data.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MiscModule(val app: Application) {
    @Provides
    @Singleton
    fun providesAppContext(): Context = app

    @Provides
    @Singleton
    fun providesApp(): Application = app

    @Provides
    @Singleton
    fun providesSharedPreferences(appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences(
            SHARED_PREF_FILE_NAME,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun providesDao(roomDatabase: BudgetValueDB): MyDao {
        return roomDatabase.myDao()
    }

    @Provides
    @Singleton
    fun providesRepo(myDao: MyDao, sharedPrefWrapper: SharedPrefWrapper): Repo {
        return Repo(
            TransactionParser(),
            sharedPrefWrapper,
            myDao
        )
    }
}