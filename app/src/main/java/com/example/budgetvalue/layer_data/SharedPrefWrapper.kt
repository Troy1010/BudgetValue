package com.example.budgetvalue.layer_data

import android.content.SharedPreferences
import com.example.budgetvalue.model_data.ReconcileCategoryAmounts
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
    override fun fetchReconcileCategoryAmounts(): List<ReconcileCategoryAmounts> {
        var storedReconcileCategoryAmounts = sharedPreferences.getString(KEY_INCOME_CA, null)
        if (storedReconcileCategoryAmounts==null) {
            return listOf()
        } else {
            val t = object : TypeToken<List<ReconcileCategoryAmounts>>() {}.type
            return Gson().fromJson(storedReconcileCategoryAmounts, t)
        }
    }

    override fun pushReconcileCategoryAmounts(reconcileCA: List<ReconcileCategoryAmounts>?) {
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