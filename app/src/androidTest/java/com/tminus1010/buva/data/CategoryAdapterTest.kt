package com.tminus1010.buva.data

import androidx.test.core.app.ApplicationProvider
import com.tminus1010.buva.all_layers.DaggerAppComponent
import com.tminus1010.buva.core_testing.BaseFakeEnvironmentModule
import com.tminus1010.buva.core_testing.shared.Given
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.buva.environment.adapter.MoshiWithCategoriesProvider
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CategoryAdapterTest {
    @Test
    fun toAndFromJson() = runBlocking {
        // # Given
        val given = Given.transaction1
        // # When
        val actual =
            moshiWithCategoriesProvider.moshiFlow.first().toJson(given)
                .logx("json")
                .let { moshiWithCategoriesProvider.moshiFlow.first().fromJson<Transaction>(it) }
        // # Then
        assertEquals(given, actual)
    }

    lateinit var moshiWithCategoriesProvider: MoshiWithCategoriesProvider

    @Before
    fun before() {
        val component =
            DaggerAppComponent.builder()
                .environmentModule(BaseFakeEnvironmentModule())
                .application(ApplicationProvider.getApplicationContext())
                .build()
        moshiWithCategoriesProvider = component.moshiWithCategoriesProvider()
    }
}