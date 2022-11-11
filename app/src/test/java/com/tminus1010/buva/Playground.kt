package com.tminus1010.buva

import com.tminus1010.buva.all_layers.categoryComparator
import com.tminus1010.buva.all_layers.extensions.reliableContains
import com.tminus1010.buva.domain.AmountFormula
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.ResetStrategy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.math.BigDecimal

class Playground {
    @Test
    fun test1() {
        val b = Category("Emergency", AmountFormula.Value(BigDecimal.ZERO), resetStrategy = ResetStrategy.Basic(BigDecimal("4")))
        val a =
            mapOf(
                Category("Unrecognized", AmountFormula.Value(BigDecimal.ZERO), resetStrategy = ResetStrategy.Basic(BigDecimal("0"))) to BigDecimal("23.33"),
                Category("Improvements", AmountFormula.Value(BigDecimal.ZERO), resetStrategy = ResetStrategy.Basic(BigDecimal("0"))) to BigDecimal("943.27"),
                Category("Medical", AmountFormula.Value(BigDecimal.ZERO), resetStrategy = ResetStrategy.Basic(BigDecimal("0"))) to BigDecimal("283.13"),
                Category("Rent", AmountFormula.Value(BigDecimal.ZERO), resetStrategy = ResetStrategy.Basic(BigDecimal("0"))) to BigDecimal("83.35"),
                Category("Unknown", AmountFormula.Value(BigDecimal.ZERO), resetStrategy = ResetStrategy.Basic(BigDecimal("0"))) to BigDecimal("8"),
                Category("Activities", AmountFormula.Value(BigDecimal.ZERO), resetStrategy = ResetStrategy.Basic(null)) to BigDecimal("8"),
                b.copy(resetStrategy = ResetStrategy.Basic(BigDecimal("5"))) to BigDecimal("50.71"),
                Category("Gifts", AmountFormula.Value(BigDecimal.ZERO), resetStrategy = ResetStrategy.Basic(null)) to BigDecimal("31.70"),
                Category("Misc", AmountFormula.Value(BigDecimal.ZERO), resetStrategy = ResetStrategy.Basic(BigDecimal("9.99"))) to BigDecimal("123.89"),
                Category("Savings", AmountFormula.Value(BigDecimal.ZERO), resetStrategy = ResetStrategy.Basic(null)) to BigDecimal("211.07"),
            )
                .toSortedMap(categoryComparator)
        (b !in a).logx("mmm1")
        (!a.reliableContains(b)).logx("mmm2")
    }

    @Test
    fun test2() {
        // # Given
        val toKeep = arrayListOf(68, 35, 54)
        val subject = arrayListOf(4, 68, 65, 35, 54, 6, 6, 7, 97)
        // # Stimulate & Verify
        assertThrows(ConcurrentModificationException::class.java) {
            subject.asSequence()
                .filter { it !in toKeep }
                .forEach { subject.remove(it) }
        }
    }

    @Test
    fun test3() {
        // # Given
        val toKeep = arrayListOf(68, 35, 54)
        val subject = arrayListOf(4, 68, 65, 35, 54, 6, 6, 7, 97)
        // # Stimulate & Verify
        assertThrows(ConcurrentModificationException::class.java) {
            subject.asSequence()
                .filter {
                    println("it !in toKeep:${it !in toKeep}")
                    it !in toKeep
                }
                .onEach {
                    println("remove:${it}")
                    subject.remove(it)
                }
                .toList()
        }
    }

    @Test
    fun test4() {
        // # Given
        val toKeep = arrayListOf(68, 35, 54)
        val subject = arrayListOf(4, 68, 65, 35, 54, 6, 6, 7, 97)
        // # Stimulate
        subject
            .filter { it !in toKeep }
            .onEach { subject.remove(it) }
        // # Verify
        assertEquals(toKeep, subject)
    }

    @Test
    fun test5() {
        // # Given
        val toKeep = arrayListOf(68, 35, 54)
        val subject = arrayListOf(4, 68, 65, 35, 54, 6, 6, 7, 97)
        // # Stimulate
        subject.toList().asSequence()
            .filter { it !in toKeep }
            .forEach { subject.remove(it) }
        // # Verify
        assertEquals(toKeep, subject)
    }

    @Test
    fun test6() {
        // # Given
        val toKeep = arrayListOf(68, 35, 54)
        val subject = arrayListOf(4, 68, 65, 35, 54, 6, 6, 7, 97)
        // # Stimulate & Verify
        subject.removeIf { it !in toKeep }
        // # Verify
        assertEquals(toKeep, subject)
    }
}