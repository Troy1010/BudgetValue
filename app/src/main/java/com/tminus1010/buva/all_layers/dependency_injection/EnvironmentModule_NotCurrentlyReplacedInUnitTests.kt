package com.tminus1010.buva.all_layers.dependency_injection

import com.tminus1010.buva.environment.CategoryDatabase
import com.tminus1010.buva.environment.MiscDAO
import com.tminus1010.buva.environment.MiscDatabase
import com.tminus1010.buva.environment.UserCategoriesDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EnvironmentModule_NotCurrentlyReplacedInUnitTests {
    @Provides
    @Singleton
    fun providesMiscDao(roomDatabase: MiscDatabase): MiscDAO = roomDatabase.miscDAO()

    @Provides
    @Singleton
    fun provideUserCategoriesDAO(categoryDatabase: CategoryDatabase): UserCategoriesDAO = categoryDatabase.userCategoriesDAO()
}