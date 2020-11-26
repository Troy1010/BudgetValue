package com.example.budgetvalue.layer_data

import android.content.SharedPreferences
import com.example.budgetvalue.getType
import com.example.budgetvalue.model_data.ReconcileCategoryAmount
import com.google.gson.Gson
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import javax.inject.Inject

class SharedPrefWrapper @Inject constructor(val sharedPreferences: SharedPreferences) :
    ISharedPrefWrapper {
    companion object {
        const val KEY_INCOME_CA = "KEY_INCOME_CA"
        const val KEY_EXPECTED_INCOME = "KEY_EXPECTED_INCOME"
        const val KEY_ANCHOR_DATE_OFFSET = "KEY_ANCHOR_DATE_OFFSET"
        const val KEY_BLOCK_SIZE = "KEY_BLOCK_SIZE"
        const val ANCHOR_DATE_OFFSET_DEFAULT: Long = 0
        const val BLOCK_SIZE_DEFAULT: Long = 14
    }

    val editor = sharedPreferences.edit()
    override fun fetchReconcileCategoryAmounts(): List<ReconcileCategoryAmount> {
        val storedReconcileCategoryAmounts = sharedPreferences.getString(KEY_INCOME_CA, null)
        return if (storedReconcileCategoryAmounts == null) listOf() else {
            Gson().fromJson(storedReconcileCategoryAmounts,
                getType<List<ReconcileCategoryAmount>>())
        }
    }

    override fun pushReconcileCategoryAmounts(reconcileCA: List<ReconcileCategoryAmount>?) {
        if (reconcileCA == null) editor.remove(KEY_INCOME_CA) else {
            editor.putString(KEY_INCOME_CA, Gson().toJson(reconcileCA))
        }
        editor.apply()
    }

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
            .startWithItem(sharedPreferences.getLong(KEY_ANCHOR_DATE_OFFSET, ANCHOR_DATE_OFFSET_DEFAULT))
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