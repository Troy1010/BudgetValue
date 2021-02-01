package com.tminus1010.budgetvalue.layer_data

import androidx.test.platform.app.InstrumentationRegistry
import com.tminus1010.budgetvalue.AppMock
import com.tminus1010.budgetvalue.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.budgetvalue.model_app.Reconciliation
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.math.BigDecimal
import java.time.LocalDate

class ReconciliationTests {
    val app by lazy { InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as AppMock }
    val repo by lazy { app.appComponent.getRepo() }
    val a by lazy { Category("SomeCategoryA", Category.Type.Always) }
    val b by lazy { Category("SomeCategoryB", Category.Type.Always) }
    val c by lazy { Category("SomeCategoryC", Category.Type.Always) }
    val x by lazy {
        app.appComponent.getCategoriesAppVM()
            .userAddedCategories
            .addAll(listOf(
                a,
                b,
                c,
            ))
    }

    @Before
    fun before() {
        repo.clearReconciliations()
        x
    }

//    @Test
//    fun pushActiveReconcileCA() {
//        // # Given
//        val localDate = LocalDate.now()
//        val reconciliation = Reconciliation(
//            LocalDate.now(),
//            SourceHashMap(mapOf(a to BigDecimal(8), b to BigDecimal(90), c to BigDecimal(3))),
//            BigDecimal(50),
//        )
//        // # Stimulate
//        repo.pushReconciliation(reconciliation).blockingAwait()
//        // # Verify
//        assertEquals(BigDecimal(8), repo.fetchReconciliations().blockingFirst()[0].categoryAmounts[a])
//        assertEquals(BigDecimal(90), repo.fetchReconciliations().blockingFirst()[0].categoryAmounts[b])
//        assertEquals(BigDecimal(3), repo.fetchReconciliations().blockingFirst()[0].categoryAmounts[c])
//        assertEquals(localDate, repo.fetchReconciliations().blockingFirst()[0].localDate)
//    }
}