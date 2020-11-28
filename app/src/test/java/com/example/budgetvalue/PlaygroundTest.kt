package com.example.budgetvalue

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PlaygroundTest() {
    @Test
    fun test1() {
        // # Given
        val toKeep = arrayListOf(68, 35, 54)
        val subject = arrayListOf(4, 68, 65, 35, 54, 6, 6, 7, 97)
        // # Stimulate & Verify
        assertThrows(ConcurrentModificationException::class.java) {
            for (x in subject) {
                if (x !in toKeep)
                    subject.remove(x)
            }
        }
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