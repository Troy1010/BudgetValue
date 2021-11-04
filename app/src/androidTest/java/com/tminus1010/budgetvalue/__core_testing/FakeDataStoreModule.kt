package com.tminus1010.budgetvalue.__core_testing

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.tminus1010.budgetvalue.FakeDatastore
import com.tminus1010.budgetvalue._core.all.dependency_injection.DataStoreModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataStoreModule::class],
)
object FakeDataStoreModule {
    @Provides
    @Singleton
    fun provideDataStore(): DataStore<Preferences> = FakeDatastore()
}