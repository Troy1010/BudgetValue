package com.tminus1010.budgetvalue.layer_data

import android.content.SharedPreferences
import com.tminus1010.budgetvalue.extensions.noEnd
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
        enum class Key {
            RECONCILE_CATEGORY_AMOUNTS,
            PLAN_CATEGORY_AMOUNTS,
            EXPECTED_INCOME,
            ANCHOR_DATE_OFFSET,
            BLOCK_SIZE
        }

        const val ANCHOR_DATE_OFFSET_DEFAULT: Long = 0
        const val BLOCK_SIZE_DEFAULT: Long = 14
    }

    val editor = sharedPreferences.edit()

    // # ReconcileCategoryAmounts
    override fun fetchActiveReconcileCAs(): Map<Category, BigDecimal> {
        return sharedPreferences.getString(Key.RECONCILE_CATEGORY_AMOUNTS.name, null)
            .let { typeConverter.categoryAmounts(it) }
    }

    override fun pushActiveReconcileCAs(categoryAmounts: Map<Category, BigDecimal>?) {
        typeConverter.string(categoryAmounts)
            ?.also { editor.putString(Key.RECONCILE_CATEGORY_AMOUNTS.name, it) }
            ?: editor.remove(Key.RECONCILE_CATEGORY_AMOUNTS.name)
        editor.apply()
    }

    override fun pushActiveReconcileCA(kv: Pair<Category, BigDecimal?>) {
        fetchActiveReconcileCAs()
            .toMutableMap()
            .also { kv.also { (k, v) -> if (v == null) it.remove(k) else it[k] = v } }
            .also { pushActiveReconcileCAs(it) }
    }

    override fun clearActiveReconcileCAs() = pushActiveReconcileCAs(null)

    // # PlanCategoryAmounts

    private val activePlanPublisher = PublishSubject.create<Map<Category, BigDecimal>?>()
    override val activePlan: Observable<Map<Category, BigDecimal>> =
        activePlanPublisher
            .startWithItem(
                sharedPreferences.getString(Key.PLAN_CATEGORY_AMOUNTS.name, null)
                    .let { typeConverter.categoryAmounts(it) }
            )
            .noEnd().replay(1).refCount()

    override fun pushActivePlanCAs(categoryAmounts: Map<Category, BigDecimal>?) {
        typeConverter.string(categoryAmounts)
            ?.also { editor.putString(Key.PLAN_CATEGORY_AMOUNTS.name, it) }
            ?: editor.remove(Key.PLAN_CATEGORY_AMOUNTS.name)
        editor.apply()
        activePlanPublisher.onNext(categoryAmounts ?: emptyMap())
    }

    override fun pushActivePlanCA(kv: Pair<Category, BigDecimal?>) {
        activePlan.blockingFirst()
            .toMutableMap()
            .also { kv.also { (k, v) -> if (v == null) it.remove(k) else it[k] = v } }
            .also { pushActivePlanCAs(it) }
    }

    override fun clearActivePlan() = pushActivePlanCAs(null)

    // # ExpectedIncome

    override fun fetchExpectedIncome(): BigDecimal {
        return sharedPreferences.getString(Key.EXPECTED_INCOME.name, null)?.toBigDecimal()
            ?: BigDecimal.ZERO
    }

    override fun pushExpectedIncome(expectedIncome: BigDecimal?) {
        expectedIncome
            ?.also { editor.putString(Key.EXPECTED_INCOME.name, it.toString()) }
            ?: editor.remove(Key.EXPECTED_INCOME.name)
        editor.apply()
    }

    // # AnchorDateOffset

    private val anchorDateOffsetPublisher = PublishSubject.create<Long>()
    override fun fetchAnchorDateOffset(): Observable<Long> {
        return anchorDateOffsetPublisher
            .startWithItem(
                sharedPreferences.getLong(Key.ANCHOR_DATE_OFFSET.name, ANCHOR_DATE_OFFSET_DEFAULT))
    }

    override fun pushAnchorDateOffset(anchorDateOffset: Long?) {
        anchorDateOffset
            ?.also { editor.putString(Key.ANCHOR_DATE_OFFSET.name, it.toString()) }
            ?: editor.remove(Key.ANCHOR_DATE_OFFSET.name)
        editor.apply()
        anchorDateOffsetPublisher.onNext(anchorDateOffset ?: ANCHOR_DATE_OFFSET_DEFAULT)
    }

    // # BlockSizePublisher

    private val blockSizePublisher = PublishSubject.create<Long>()
    override fun fetchBlockSize(): Observable<Long> {
        return blockSizePublisher
            .startWithItem(sharedPreferences.getLong(Key.BLOCK_SIZE.name, BLOCK_SIZE_DEFAULT))
    }

    override fun pushBlockSize(blockSize: Long?) {
        blockSize
            ?.also { editor.putLong(Key.BLOCK_SIZE.name, it) }
            ?: editor.remove(Key.BLOCK_SIZE.name)
        editor.apply()
        blockSizePublisher.onNext(blockSize ?: BLOCK_SIZE_DEFAULT)
    }
}