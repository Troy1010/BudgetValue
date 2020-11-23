package com.example.budgetvalue.layer_data

import android.content.SharedPreferences
import com.example.budgetvalue.model_data.IncomeCategoryAmounts
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class SharedPrefWrapper @Inject constructor(val sharedPreferences: SharedPreferences) :
    ISharedPrefWrapper {
    companion object {
        const val KEY_INCOME_CA = "KEY_INCOME_CA"
    }
    val editor = sharedPreferences.edit()
    override fun readIncomeCA(): List<IncomeCategoryAmounts> {
        var storedIncomeCA = sharedPreferences.getString(KEY_INCOME_CA, null)
        if (storedIncomeCA==null) {
            return listOf()
        } else {
            val t = object : TypeToken<List<IncomeCategoryAmounts>>() {}.type
            return Gson().fromJson(storedIncomeCA, t)
        }
    }

    override fun writeIncomeCA(incomeCA: List<IncomeCategoryAmounts>?) {
        if (incomeCA == null) {
            editor.clear()
        } else {
            editor.putString(KEY_INCOME_CA, Gson().toJson(incomeCA))
        }
        editor.commit()

    }
}