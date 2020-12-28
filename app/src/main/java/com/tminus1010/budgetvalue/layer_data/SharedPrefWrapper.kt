package com.tminus1010.budgetvalue.layer_data

import android.content.SharedPreferences
import com.tminus1010.budgetvalue.model_app.Category
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import javax.inject.Inject

class SharedPrefWrapper @Inject constructor(
    val sharedPreferences: SharedPreferences,
    val typeConverter: TypeConverter,
) : ISharedPrefWrapper {
    companion object {
        const val KEY_RECONCILE_CATEGORY_AMOUNTS = "KEY_RECONCILE_CATEGORY_AMOUNTS"
        const val KEY_EXPECTED_INCOME = "KEY_EXPECTED_INCOME"
        const val KEY_ANCHOR_DATE_OFFSET = "KEY_ANCHOR_DATE_OFFSET"
        const val KEY_BLOCK_SIZE = "KEY_BLOCK_SIZE"
        const val ANCHOR_DATE_OFFSET_DEFAULT: Long = 0
        const val BLOCK_SIZE_DEFAULT: Long = 14
    }

    val editor = sharedPreferences.edit()

    // # ReconcileCategoryAmounts
    override fun fetchActiveReconcileCAs(): Map<Category, BigDecimal> {
        return sharedPreferences.getString(KEY_RECONCILE_CATEGORY_AMOUNTS, null)
            .let { typeConverter.categoryAmounts(it) }
    }

    override fun pushActiveReconcileCAs(reconcileCategoryAmounts: Map<Category, BigDecimal>?) {
        val s = typeConverter.string(reconcileCategoryAmounts)
        if (s == null) editor.remove(KEY_RECONCILE_CATEGORY_AMOUNTS) else {
            editor.putString(KEY_RECONCILE_CATEGORY_AMOUNTS, s)
        }
        editor.apply()
    }

    override fun pushActiveReconcileCA(kv: Pair<Category, BigDecimal?>) {
        fetchActiveReconcileCAs()
            .toMutableMap()
            .also { val (k,v) = kv; if (v==null) it.remove(k) else it[k] = v }
            .also { pushActiveReconcileCAs(it) }
    }

    //

    override fun fetchExpectedIncome(): BigDecimal {
        return sharedPreferences.getString(KEY_EXPECTED_INCOME, null)?.toBigDecimal()
            ?: BigDecimal.ZERO
    }

    override fun pushExpectedIncome(expectedIncome: BigDecimal?) {
        if (expectedIncome == null) editor.remove(KEY_EXPECTED_INCOME) else {
            editor.putString(KEY_EXPECTED_INCOME, expectedIncome.toString())
        }
        editor.apply()
    }

    private val anchorDateOffsetPublisher = PublishSubject.create<Long>()
    override fun fetchAnchorDateOffset(): Observable<Long> {
        return anchorDateOffsetPublisher
            .startWithItem(sharedPreferences
                .getLong(KEY_ANCHOR_DATE_OFFSET, ANCHOR_DATE_OFFSET_DEFAULT))
    }

    override fun pushAnchorDateOffset(anchorDateOffset: Long?) {
        if (anchorDateOffset == null) editor.remove(KEY_ANCHOR_DATE_OFFSET) else {
            editor.putLong(KEY_ANCHOR_DATE_OFFSET, anchorDateOffset)
        }
        editor.apply()
        anchorDateOffsetPublisher.onNext(anchorDateOffset ?: ANCHOR_DATE_OFFSET_DEFAULT)
    }


    private val blockSizePublisher = PublishSubject.create<Long>()
    override fun fetchBlockSize(): Observable<Long> {
        return blockSizePublisher
            .startWithItem(sharedPreferences.getLong(KEY_BLOCK_SIZE, BLOCK_SIZE_DEFAULT))
    }

    override fun pushBlockSize(blockSize: Long?) {
        if (blockSize == null) editor.remove(KEY_BLOCK_SIZE) else {
            editor.putLong(KEY_BLOCK_SIZE, blockSize)
        }
        editor.apply()
        anchorDateOffsetPublisher.onNext(blockSize ?: BLOCK_SIZE_DEFAULT)
    }
}