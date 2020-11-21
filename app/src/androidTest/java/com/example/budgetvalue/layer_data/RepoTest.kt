package com.example.budgetvalue.layer_data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.budgetvalue.App
import com.example.budgetvalue.globals.appComponent
import com.example.budgetvalue.model_data.PlanCategoryAmounts
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
class RepoTest {
    val repo by lazy { appComponent.getRepo() }

    @Test
    fun addAndGetPlanCategoryAmountsTest() {
        // # Given
        val planCategoryAmounts = PlanCategoryAmounts("SomeCategory", BigDecimal.TEN)
        // # Stimulate
        repo.addPlanCategoryAmounts(planCategoryAmounts)
        // # Verify
        assertEquals(1, repo.getPlanCategoryAmounts().blockingFirst().size)
        assertEquals(planCategoryAmounts, repo.getPlanCategoryAmounts().blockingFirst()[0])
    }

    @Test
    fun deleteAllPlanCategoryAmountTests() {

    }
}