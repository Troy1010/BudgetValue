package com.example.budgetvalue.dependency_injection

import android.content.Context
import android.content.SharedPreferences
import com.example.budgetvalue.App
import com.example.budgetvalue.SHARED_PREF_FILE_NAME
import com.example.budgetvalue.layer_data.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepoModule {
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
    fun providesDaoWrapper(myDao: MyDao): MyDaoWrapper {
        return MyDaoWrapper(myDao)
    }

    @Provides
    @Singleton
    fun providesRepo(myDaoWrapper: MyDaoWrapper, sharedPrefWrapper: SharedPrefWrapper): Repo {
        return Repo(
            TransactionParser(),
            sharedPrefWrapper,
            myDaoWrapper
        )
    }
}