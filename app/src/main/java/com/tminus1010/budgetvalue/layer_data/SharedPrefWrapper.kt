package com.tminus1010.budgetvalue.layer_data

import android.content.SharedPreferences
import com.tminus1010.budgetvalue.model_data.Category
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
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
            BLOCK_SIZE,
            APP_INIT_BOOL,
        }

        const val ANCHOR_DATE_OFFSET_DEFAULT: Long = 0
        const val BLOCK_SIZE_DEFAULT: Long = 14
    }

    val editor = sharedPreferences.edit()

    // # ActiveReconciliation

    private val activeReconciliationCAsPublisher = PublishSubject.create<Map<Category, BigDecimal>?>()
    override val activeReconciliationCAs: BehaviorSubject<Map<Category, BigDecimal>> =
        activeReconciliationCAsPublisher
            .startWithItem(sharedPreferences.getString(Key.RECONCILE_CATEGORY_AMOUNTS.name, null)
                .let { typeConverter.categoryAmounts(it) })
            .distinctUntilChanged()
            .toBehaviorSubject()

    override fun pushActiveReconciliationCAs(categoryAmounts: Map<Category, BigDecimal>?) {
        categoryAmounts
            ?.let { typeConverter.string(it) }
            ?.also { editor.putString(Key.RECONCILE_CATEGORY_AMOUNTS.name, it) }
            ?: editor.remove(Key.RECONCILE_CATEGORY_AMOUNTS.name)
        editor.apply()
        activeReconciliationCAsPublisher.onNext(categoryAmounts ?: emptyMap())
    }

    override fun pushActiveReconciliationCA(kv: Pair<Category, BigDecimal?>) {
        activeReconciliationCAs.value
            .toMutableMap()
            .also { kv.also { (k, v) -> if (v == null || v == BigDecimal.ZERO) it.remove(k) else it[k] = v } }
            .also { pushActiveReconciliationCAs(it) }
    }

    override fun clearActiveReconcileCAs() = pushActiveReconciliationCAs(null)

    // # ActivePlan

    private val activePlanCAsPublisher = PublishSubject.create<Map<Category, BigDecimal>?>()
    override val activePlanCAs: BehaviorSubject<Map<Category, BigDecimal>> =
        activePlanCAsPublisher
            .startWithItem(sharedPreferences.getString(Key.PLAN_CATEGORY_AMOUNTS.name, null)
                .let { typeConverter.categoryAmounts(it) })
            .distinctUntilChanged()
            .toBehaviorSubject()

    override fun pushActivePlanCAs(categoryAmounts: Map<Category, BigDecimal>?) {
        categoryAmounts
            ?.let { typeConverter.string(it) }
            ?.also { editor.putString(Key.PLAN_CATEGORY_AMOUNTS.name, it) }
            ?: editor.remove(Key.PLAN_CATEGORY_AMOUNTS.name)
        editor.apply()
        activePlanCAsPublisher.onNext(categoryAmounts ?: emptyMap())
    }

    override fun pushActivePlanCA(kv: Pair<Category, BigDecimal?>) {
        activePlanCAs.value
            .toMutableMap()
            .also { kv.also { (k, v) -> if (v == null || v == BigDecimal.ZERO) it.remove(k) else it[k] = v } }
            .also { pushActivePlanCAs(it) }
    }

    override fun clearActivePlan() = pushActivePlanCAs(null)

    // # ExpectedIncome

    override fun fetchExpectedIncome(): BigDecimal {
        return sharedPreferences.getString(Key.EXPECTED_INCOME.name, null)
            ?.toBigDecimal()
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
    override val anchorDateOffset: Observable<Long> =
        anchorDateOffsetPublisher
            .startWithItem(sharedPreferences.getLong(Key.ANCHOR_DATE_OFFSET.name, ANCHOR_DATE_OFFSET_DEFAULT))
            .distinctUntilChanged()
            .toBehaviorSubject()

    override fun pushAnchorDateOffset(anchorDateOffset: Long?) {
        anchorDateOffset
            ?.also { editor.putString(Key.ANCHOR_DATE_OFFSET.name, it.toString()) }
            ?: editor.remove(Key.ANCHOR_DATE_OFFSET.name)
        editor.apply()
        anchorDateOffsetPublisher.onNext(anchorDateOffset ?: ANCHOR_DATE_OFFSET_DEFAULT)
    }

    // # BlockSize

    private val blockSizePublisher = PublishSubject.create<Long>()
    override val blockSize: Observable<Long> =
        blockSizePublisher
            .startWithItem(sharedPreferences.getLong(Key.BLOCK_SIZE.name, BLOCK_SIZE_DEFAULT))
            .distinctUntilChanged()
            .toBehaviorSubject()

    override fun pushBlockSize(blockSize: Long?) {
        blockSize
            ?.also { editor.putLong(Key.BLOCK_SIZE.name, it) }
            ?: editor.remove(Key.BLOCK_SIZE.name)
        editor.apply()
        blockSizePublisher.onNext(blockSize ?: BLOCK_SIZE_DEFAULT)
    }

    // # AppInitBool

    override fun fetchAppInitBool(): Boolean =
        sharedPreferences.getBoolean(Key.APP_INIT_BOOL.name, false)

    override fun pushAppInitBool(boolean: Boolean) {
        editor.putBoolean(Key.APP_INIT_BOOL.name, boolean)
        editor.apply()
    }
}