package com.tminus1010.buva.data

import androidx.test.core.app.ApplicationProvider
import com.tminus1010.buva.all_layers.DaggerAppComponent
import com.tminus1010.buva.core_testing.BaseFakeEnvironmentModule
import com.tminus1010.buva.core_testing.shared.Given
import com.tminus1010.buva.domain.CategoryAmounts
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class ActiveReconciliationRepoTest {
    @Test
    fun default() = runBlocking {
        // # Given
        Given.categories.forEach { categoryRepo.push(it) }
        // # When
        // # Then
        assertEquals(
            CategoryAmounts(),
            activeReconciliationRepo.activeReconciliationCAs.value,
        )
        Thread.sleep(500) // Why is this necessary..?
    }

    @Test
    fun push() = runBlocking {
        // # Given
        Given.categories.forEach { categoryRepo.push(it) }
        // # When
        activeReconciliationRepo.pushCategoryAmounts(CategoryAmounts(Given.categories[0] to BigDecimal("7")))
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
        Given.categories.forEach { categoryRepo.push(it) }
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

    lateinit var categoryRepo: CategoryRepo
    lateinit var activeReconciliationRepo: ActiveReconciliationRepo

    @Before
    fun before() {
        val component =
            DaggerAppComponent.builder()
                .environmentModule(BaseFakeEnvironmentModule())
                .application(ApplicationProvider.getApplicationContext())
                .build()
        categoryRepo = component.categoryRepo()
        activeReconciliationRepo = component.activeReconciliationRepo()
    }
}