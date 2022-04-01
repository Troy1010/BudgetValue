package com.tminus1010.budgetvalue.__core_testing

import android.content.res.AssetManager
import androidx.test.platform.app.InstrumentationRegistry
import com.tminus1010.budgetvalue.MockImportSelectionActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MockImportSelectionModule {
    @Provides
    @Singleton
    fun provideAssets(): MockImportSelectionActivity.AndroidTestAssetsProvider = object : MockImportSelectionActivity.AndroidTestAssetsProvider() {
        override fun get(): AssetManager = InstrumentationRegistry.getInstrumentation().context.assets
    }
}