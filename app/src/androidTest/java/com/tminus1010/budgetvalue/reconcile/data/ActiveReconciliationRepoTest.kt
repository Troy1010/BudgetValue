package com.tminus1010.budgetvalue.reconcile.data

import androidx.room.Room
import com.tminus1010.budgetvalue.Given
import com.tminus1010.budgetvalue.__core_testing.app
import com.tminus1010.budgetvalue._core.all.dependency_injection.DatabaseModule
import com.tminus1010.budgetvalue._core.data.CategoryDatabase
import com.tminus1010.budgetvalue._core.data.MiscDatabase
import com.tminus1010.budgetvalue._core.data.RoomWithCategoriesTypeConverter
import com.tminus1010.budgetvalue._core.domain.CategoryAmounts
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@UninstallModules(DatabaseModule::class)
@HiltAndroidTest
class ActiveReconciliationRepoTest {
    @Test
    fun default() = runBlocking {
        // # Given
        // # When
        // # Then
        assertEquals(
            CategoryAmounts(),
            activeReconciliationRepo.activeReconciliationCAs.value
        )
        Thread.sleep(500) // Why is this necessary..?
    }

    @Test
    fun push() = runBlocking {
        // # Given
        // # When
        activeReconciliationRepo.push(CategoryAmounts(Given.categories[0] to BigDecimal("7")))
        Thread.sleep(500) // Why is this necessary..?
        // # Then
        assertEquals(
            CategoryAmounts(Given.categories[0] to BigDecimal("7")),
            activeReconciliationRepo.activeReconciliationCAs.value,
        )
        Thread.sleep(500) // Why is this necessary..?
    }

    @Test
    fun pushCategoryAmount() = runBlocking {
        // # Given
        // # When
        activeReconciliationRepo.pushCategoryAmount(Given.categories[0], BigDecimal("7"))
        Thread.sleep(500) // Why is this necessary..?
        // # Then
        assertEquals(
            CategoryAmounts(Given.categories[0] to BigDecimal("7")),
            activeReconciliationRepo.activeReconciliationCAs.value,
        )
        Thread.sleep(500) // Why is this necessary..?
    }

    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @Inject
    lateinit var activeReconciliationRepo: ActiveReconciliationRepo

    @Before
    fun before() {
        hiltAndroidRule.inject()
    }

    @InstallIn(SingletonComponent::class)
    @Module
    object MockModule {
        @Provides
        @Singleton
        fun categoryDatabase(): CategoryDatabase {
            return Room.inMemoryDatabaseBuilder(app, CategoryDatabase::class.java).build()
        }

        @Provides
        @Singleton
        fun miscDatabase(roomWithCategoriesTypeConverter: RoomWithCategoriesTypeConverter): MiscDatabase {
            return Room.inMemoryDatabaseBuilder(app, MiscDatabase::class.java)
                .addTypeConverter(roomWithCategoriesTypeConverter)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}