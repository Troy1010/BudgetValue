package com.example.budgetvalue.layer_data

import android.content.SharedPreferences
import com.example.budgetvalue.extensions.associate
import com.example.budgetvalue.getType
import com.example.budgetvalue.model_app.Category
import com.example.budgetvalue.model_app.ICategoryParser
import com.google.gson.Gson
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import javax.inject.Inject

class SharedPrefWrapper @Inject constructor(
    val sharedPreferences: SharedPreferences,
    val categoryParser: ICategoryParser
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

    private val reconcileCategoryAmountsPublisher = PublishSubject.create<Map<Category, BigDecimal>>()
    override fun fetchReconcileCategoryAmounts(): Observable<Map<Category, BigDecimal>> {
        val s = sharedPreferences.getString(KEY_RECONCILE_CATEGORY_AMOUNTS, null)
        val reconcileCategoryAmountsReceived: Map<String, String> = if (s == null) emptyMap() else {
            Gson().fromJson(s, getType<HashMap<String, String>>())
        }
        val reconcileCategoryAmounts = reconcileCategoryAmountsReceived.associate { categoryParser.parseCategory(it.key) to it.value.toBigDecimal() }
        return reconcileCategoryAmountsPublisher
            .startWithItem(reconcileCategoryAmounts)
    }

    override fun pushReconcileCategoryAmounts(reconcileCategoryAmounts: Map<Category, BigDecimal>?) {
        val reconcileCategoryAmountsReceived = reconcileCategoryAmounts?.associate { it.key.name to it.value.toString() }
        if (reconcileCategoryAmountsReceived == null) editor.remove(KEY_RECONCILE_CATEGORY_AMOUNTS) else {
            editor.putString(KEY_RECONCILE_CATEGORY_AMOUNTS, Gson().toJson(reconcileCategoryAmountsReceived))
        }
        editor.apply()
        reconcileCategoryAmountsPublisher.onNext(reconcileCategoryAmounts ?: hashMapOf())
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