package com.example.budgetvalue

import com.example.budgetvalue.extensions.toSourceHashMap
import com.example.budgetvalue.extensions.withLatestFrom
import org.junit.Test
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
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
        assertEquals(sourceHashMap.itemObservablesObservable.value[0]!!.value, 10)
        assertEquals(sourceHashMap.itemObservablesObservable.value[3]!!.value, 30)
        assertEquals(sourceHashMap.itemObservablesObservable.value[9]!!.value, 90)
    }
}