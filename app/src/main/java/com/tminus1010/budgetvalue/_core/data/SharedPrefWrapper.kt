package com.tminus1010.budgetvalue._core.data

import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import com.tminus1010.budgetvalue._core.extensions.fromJson
import com.tminus1010.budgetvalue._core.extensions.toJson
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import javax.inject.Inject

class SharedPrefWrapper @Inject constructor(
    val sharedPreferences: SharedPreferences,
    val moshi: Moshi,
) {
    companion object {
        enum class Key {
            RECONCILE_CATEGORY_AMOUNTS,
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

    private val activeReconciliationCAsPublisher = PublishSubject.create<Map<String, String>>()
    val activeReconciliationCAs: BehaviorSubject<Map<String, String>> =
        activeReconciliationCAsPublisher
            .startWithItem(moshi.fromJson(sharedPreferences.getString(Key.RECONCILE_CATEGORY_AMOUNTS.name, null)?:"{}"))
            .distinctUntilChanged()
            .toBehaviorSubject()

    fun pushActiveReconciliationCAs(categoryAmounts: Map<String, String>?): Completable {
        categoryAmounts
            ?.let { moshi.toJson(it) }
            ?.also { editor.putString(Key.RECONCILE_CATEGORY_AMOUNTS.name, it) }
            ?: editor.remove(Key.RECONCILE_CATEGORY_AMOUNTS.name)
        return Completable.fromAction {
            editor.commit()
            activeReconciliationCAsPublisher.onNext(categoryAmounts ?: emptyMap())
        }
    }

    fun pushActiveReconciliationCA(kv: Pair<String, String?>): Completable {
        val (k, v) = kv
        return activeReconciliationCAs.value
            .toMutableMap()
            .also { if (v==null || v == BigDecimal.ZERO.toString()) it.remove(k) else it[k] = v }
            .let { pushActiveReconciliationCAs(it) }
    }

    fun clearActiveReconcileCAs() = pushActiveReconciliationCAs(null)

    // # ExpectedIncome

    fun fetchExpectedIncome(): String =
        sharedPreferences.getString(Key.EXPECTED_INCOME.name, null) ?: "0"

    fun pushExpectedIncome(expectedIncome: String?): Completable {
        expectedIncome
            ?.also { editor.putString(Key.EXPECTED_INCOME.name, it) }
            ?: editor.remove(Key.EXPECTED_INCOME.name)
        return Completable.fromAction {
            editor.commit()
        }
    }

    // # AnchorDateOffset

    private val anchorDateOffsetPublisher = PublishSubject.create<Long>()
    val anchorDateOffset: Observable<Long> =
        anchorDateOffsetPublisher
            .startWithItem(sharedPreferences.getLong(Key.ANCHOR_DATE_OFFSET.name, ANCHOR_DATE_OFFSET_DEFAULT))
            .distinctUntilChanged()

    fun pushAnchorDateOffset(anchorDateOffset: Long?): Completable {
        anchorDateOffset
            ?.also { editor.putString(Key.ANCHOR_DATE_OFFSET.name, it.toString()) }
            ?: editor.remove(Key.ANCHOR_DATE_OFFSET.name)
        return Completable.fromAction {
            editor.commit()
            anchorDateOffsetPublisher.onNext(anchorDateOffset ?: ANCHOR_DATE_OFFSET_DEFAULT)
        }
    }

    // # BlockSize

    private val blockSizePublisher = PublishSubject.create<Long>()
    val blockSize: Observable<Long> =
        blockSizePublisher
            .startWithItem(sharedPreferences.getLong(Key.BLOCK_SIZE.name, BLOCK_SIZE_DEFAULT))
            .distinctUntilChanged()

    fun pushBlockSize(blockSize: Long?): Completable {
        blockSize
            ?.also { editor.putLong(Key.BLOCK_SIZE.name, it) }
            ?: editor.remove(Key.BLOCK_SIZE.name)
        return Completable.fromAction {
            editor.commit()
            blockSizePublisher.onNext(blockSize ?: BLOCK_SIZE_DEFAULT)
        }
    }

    // # AppInitBool

    fun fetchAppInitBool(): Boolean =
        sharedPreferences.getBoolean(Key.APP_INIT_BOOL.name, false)

    fun pushAppInitBool(boolean: Boolean): Completable {
        editor.putBoolean(Key.APP_INIT_BOOL.name, boolean)
        return Completable.fromAction { editor.commit() }
    }
}