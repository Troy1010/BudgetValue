package com.example.budgetvalue

import org.junit.Test

import org.junit.Assert.*

class SourceHashMapTest {

    @Test
    fun put() {
        // # Given
        val sourceHashMap = SourceHashMap<Int, Int>()
        // # Stimulate
        sourceHashMap[0] = 10
        // # Verify
        assertEquals(sourceHashMap[0], 10)
        Thread.sleep(1000) // TODO("use RxThreadsRule")
        assertEquals(10, sourceHashMap.observable.value[0]!!.value)
    }

    @Test
    fun putAll() {
        // # Given
        val sourceHashMap = SourceHashMap<Int, Int>()
        val feedingMap = hashMapOf(0 to 10, 3 to 30, 9 to 90)
        // # Stimulate
        sourceHashMap.putAll(feedingMap)
        // # Verify
        assertEquals(sourceHashMap[0], 10)
        assertEquals(sourceHashMap[3], 30)
        assertEquals(sourceHashMap[9], 90)
        Thread.sleep(1000) // TODO("use RxThreadsRule")
        assertEquals(10, sourceHashMap.observable.value[0]!!.value)
        assertEquals(30, sourceHashMap.observable.value[3]!!.value)
        assertEquals(90, sourceHashMap.observable.value[9]!!.value)
    }

    @Test
    fun putAll_WhenApplied() {
        // # Given
        val feedingMap = hashMapOf(0 to 10, 3 to 30, 9 to 90)
        // # Stimulate
        val sourceHashMap = SourceHashMap<Int, Int>().apply { putAll(feedingMap) }
        // # Verify
        assertEquals(sourceHashMap[0], 10)
        assertEquals(sourceHashMap[3], 30)
        assertEquals(sourceHashMap[9], 90)
        Thread.sleep(1000) // TODO("use RxThreadsRule")
        assertEquals(10, sourceHashMap.observable.value[0]!!.value)
        assertEquals(30, sourceHashMap.observable.value[3]!!.value)
        assertEquals(90, sourceHashMap.observable.value[9]!!.value)
    }
}