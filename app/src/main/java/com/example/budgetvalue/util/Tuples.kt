package com.example.budgetvalue.util

import java.io.Serializable

public data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
) : Serializable {
    public override fun toString(): String = "($first, $second, $third, $fourth)"
}
public fun <T> Quadruple<T, T, T, T>.toList(): List<T> = listOf(first, second, third, fourth)

public data class Quintuple<out A, out B, out C, out D, out E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
) : Serializable {
    public override fun toString(): String = "($first, $second, $third, $fourth, $fifth)"
}
public fun <T> Quintuple<T, T, T, T, T>.toList(): List<T> = listOf(first, second, third, fourth, fifth)