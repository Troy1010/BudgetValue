package com.tminus1010.budgetvalue.source_objects

import org.junit.Assert.*
import org.junit.Test

class SourceHashMapTest {

    @Test
    fun put() {
        // # Given
        val sourceHashMap = SourceHashMap<Int, Int>(exitValue = 0)
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
    fun putAll_GivenInitMap() {
        // # Given
        val initMap = hashMapOf(0 to 10, 3 to 30, 9 to 90)
        // # Stimulate
        val sourceHashMap = SourceHashMap(initMap, exitValue = 0)
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
    fun changeSet() {
        // # Given
        val initMap = hashMapOf(0 to 10, 3 to 30, 9 to 90)
        val sourceHashMap = SourceHashMap(initMap, exitValue = 0)
        // # Stimulate
        val results = sourceHashMap.changeSet.test()
        sourceHashMap[0] = 3
        sourceHashMap[1] = 20
        sourceHashMap.remove(3)
        // # Verify
        results
            .assertValues(
                Change(ChangeType.EDIT, 0, 3),
                Change(ChangeType.ADD, 1, 20),
                Change(ChangeType.EDIT, 3, 0),
                Change(ChangeType.REMOVE, 3, 0),
            )
    }

    @Test
    fun changeSet_WhenClear() {
        // # Given
        val initMap = hashMapOf(0 to 10, 2 to 22)
        val sourceHashMap = SourceHashMap(initMap, exitValue = 77)
        // # Stimulate
        val results = sourceHashMap.changeSet.test()
        sourceHashMap.clear()
        // # Verify
        results
            .assertValues(
                Change(ChangeType.EDIT, 0, 77),
                Change(ChangeType.EDIT, 2, 77),
                Change(ChangeType.REMOVE, 0, 77),
                Change(ChangeType.REMOVE, 2, 77),
            )
    }
}