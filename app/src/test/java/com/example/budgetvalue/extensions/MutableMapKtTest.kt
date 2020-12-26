package com.example.budgetvalue.extensions

import org.junit.Test

import org.junit.Assert.*

class MutableMapKtTest {

    @Test
    fun removeIf() {
        // # Given
        val map = mutableMapOf(3 to 5, 85 to 4, 98 to 853)
        // # Stimulate
        map.removeIf { (it.key < 90) && (it.value > 2) }
        // # Verify
        assertEquals(mutableMapOf(98 to 853), map)
    }
}