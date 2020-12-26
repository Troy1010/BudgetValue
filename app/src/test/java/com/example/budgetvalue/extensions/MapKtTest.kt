package com.example.budgetvalue.extensions

import org.junit.Assert.assertEquals
import org.junit.Test

class MapKtTest {

    @Test
    fun toSourceHashMap() {
        // # Given
        val given = hashMapOf(1 to 2, 4 to 5, 6 to 0)
        // # Stimulate
        val result = given.toSourceHashMap()
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