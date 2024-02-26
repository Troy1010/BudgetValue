package com.tminus1010.buva.data

import androidx.test.core.app.ApplicationProvider
import com.tminus1010.buva.all_layers.DaggerAppComponent
import com.tminus1010.buva.core_testing.BaseFakeEnvironmentModule
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.ReconciliationStrategyGroup
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CategoryRepoTest {
    @Test
    fun default() = runBlocking {
        // # When
        val result = categoryRepo.userCategories.first()
        // # Then
        assertEquals(listOf<Category>(), result)
    }

    @Test
    fun push() = runBlocking {
        // # Given
        val givenCategory = Category("Given Category")
        // # When
        categoryRepo.push(givenCategory)
        val result = categoryRepo.userCategories.first()
        // # Then
        assertEquals(listOf(givenCategory), result)
    }

    @Test
    fun delete() = runBlocking {
        // # Given
        val givenCategory = Category("Given Category")
        categoryRepo.push(givenCategory)
        // # When
        categoryRepo.delete(givenCategory)
        val result = categoryRepo.userCategories.first()
        // # Then
        assertEquals(listOf<Category>(), result)
    }

    @Test
    fun replace() = runBlocking {
        // # Given
        val givenCategory1 = Category("Given Category", reconciliationStrategyGroup = ReconciliationStrategyGroup.Always)
        categoryRepo.push(givenCategory1)
        val givenCategory2 = Category("Given Category", reconciliationStrategyGroup = ReconciliationStrategyGroup.Reservoir())
        // # When
        categoryRepo.push(givenCategory2)
        val result = categoryRepo.userCategories.first()
        // # Then
        assertEquals(listOf(givenCategory2), result)
    }

    lateinit var categoryRepo: CategoryRepo

    @Before
    fun before() {
        val component =
            DaggerAppComponent.builder()
                .environmentModule(BaseFakeEnvironmentModule())
                .application(ApplicationProvider.getApplicationContext())
                .build()
        categoryRepo = component.categoryRepo()
    }
}