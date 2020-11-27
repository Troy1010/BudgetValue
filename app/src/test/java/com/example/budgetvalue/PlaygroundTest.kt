package com.example.budgetvalue

import com.example.budgetvalue.extensions.toSourceHashMap
import org.junit.Test
import org.junit.Assert.assertEquals

class PlaygroundTest() {
    @Test
    fun test1() {
        // # Given
        val feedingMap = hashMapOf(0 to 10, 3 to 30, 9 to 90)
        // # Stimulate
//        val sourceHashMap = SourceHashMap<Int, Int>().apply { putAll(feedingMap) }
        val sourceHashMap = feedingMap.toSourceHashMap()
        // # Verify
        assertEquals(sourceHashMap[0], 10)
        assertEquals(sourceHashMap[3], 30)
        assertEquals(sourceHashMap[9], 90)
        Thread.sleep(1000) // TODO("use RxThreadsRule")
        assertEquals(sourceHashMap.observable.value[0]!!.value, 10)
        assertEquals(sourceHashMap.observable.value[3]!!.value, 30)
        assertEquals(sourceHashMap.observable.value[9]!!.value, 90)
    }
}