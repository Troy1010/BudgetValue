package com.tminus1010.budgetvalue.aa_core.data

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface ISharedPrefWrapper {
    val activeReconciliationCAs: Observable<Map<String, String>>
    fun pushActiveReconciliationCAs(categoryAmounts: Map<String, String>?): Completable
    fun pushActiveReconciliationCA(kv: Pair<String, String?>): Completable
    fun clearActiveReconcileCAs(): Completable
    fun fetchExpectedIncome(): String
    fun pushExpectedIncome(expectedIncome: String?): Completable
    val anchorDateOffset: Observable<Long>
    fun pushAnchorDateOffset(anchorDateOffset: Long?): Completable
    val blockSize: Observable<Long>
    fun pushBlockSize(blockSize: Long?): Completable
    fun fetchAppInitBool(): Boolean
    fun pushAppInitBool(boolean: Boolean = true): Completable
}