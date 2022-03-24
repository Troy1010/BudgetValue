package com.tminus1010.budgetvalue.__core_testing

import android.content.res.AssetManager
import androidx.test.platform.app.InstrumentationRegistry
import com.tminus1010.budgetvalue.all_layers.dependency_injection.AssetsModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AssetsModule::class],
)
object FakeAssetsModule {
    @Provides
    @Singleton
    fun provideAssets(): AssetManager = InstrumentationRegistry.getInstrumentation().context.assets
}