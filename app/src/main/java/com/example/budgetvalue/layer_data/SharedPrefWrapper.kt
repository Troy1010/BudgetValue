package com.example.budgetvalue.layer_data

import android.content.SharedPreferences
import com.example.budgetvalue.getType
import com.example.budgetvalue.model_data.ReconcileCategoryAmount
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.math.BigDecimal
import javax.inject.Inject

class SharedPrefWrapper @Inject constructor(val sharedPreferences: SharedPreferences) :
    ISharedPrefWrapper {
    companion object {
        const val KEY_INCOME_CA = "KEY_INCOME_CA"
        const val KEY_EXPECTED_INCOME = "KEY_EXPECTED_INCOME"
    }
    val editor = sharedPreferences.edit()
    override fun fetchReconcileCategoryAmounts(): List<ReconcileCategoryAmount> {
        val storedReconcileCategoryAmounts = sharedPreferences.getString(KEY_INCOME_CA, null)
        return if (storedReconcileCategoryAmounts==null) listOf() else {
            Gson().fromJson(storedReconcileCategoryAmounts, getType<List<ReconcileCategoryAmount>>())
        }
    }

    override fun pushReconcileCategoryAmounts(reconcileCA: List<ReconcileCategoryAmount>?) {
        if (reconcileCA == null) editor.remove(KEY_INCOME_CA) else {
            editor.putString(KEY_INCOME_CA, Gson().toJson(reconcileCA))
        }
        editor.apply()
    }

    override fun fetchExpectedIncome(): BigDecimal {
        val returning = sharedPreferences.getString(KEY_EXPECTED_INCOME, null)
        return returning?.toBigDecimal() ?: BigDecimal.ZERO
    }

    override fun pushExpectedIncome(expectedIncome: BigDecimal?) {
        if (expectedIncome==null) editor.remove(KEY_EXPECTED_INCOME) else {
            editor.putString(KEY_EXPECTED_INCOME, expectedIncome.toString())
        }
        editor.apply()
    }
}