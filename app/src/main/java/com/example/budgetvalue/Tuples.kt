package com.example.budgetvalue

import java.io.Serializable

data class Single<out A>(
    val first: A
) : Serializable {
    override fun toString(): String = "($first)"
}
fun <T> Single<T>.toList(): List<T> = listOf(first)

data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
) : Serializable {
    override fun toString(): String = "($first, $second, $third, $fourth)"
}
fun <T> Quadruple<T, T, T, T>.toList(): List<T> = listOf(first, second, third, fourth)

data class Quintuple<out A, out B, out C, out D, out E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
) : Serializable {
    override fun toString(): String = "($first, $second, $third, $fourth, $fifth)"
}
fun <T> Quintuple<T, T, T, T, T>.toList(): List<T> = listOf(first, second, third, fourth, fifth)