package com.tminus1010.budgetvalue.extensions

import com.tminus1010.budgetvalue._core.extensions.toSourceHashMap
import com.tminus1010.tmcommonkotlin.misc.extensions.toHashMap
import org.junit.Assert.assertEquals
import org.junit.Test

// TODO("move test to tmcommonkotlin")
class MapKtTest {

    @Test
    fun toSourceHashMap() {
        // # Given
        val given = hashMapOf(1 to 2, 4 to 5, 6 to 0)
        // # Stimulate
        val result = given.toSourceHashMap(0)
        // # Result
        assertEquals(given.size, result.size)
    }

    @Test
    fun toHashMap() {
        // # Given
        val given = hashMapOf(1 to 2, 4 to 5, 6 to 0)
        // # Stimulate
        val result = given.toHashMap()
        // # Result
        assertEquals(given.size, result.size)
    }
}