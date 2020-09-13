package com.example.budgetvalue.layers.data_layer

import android.content.SharedPreferences
import com.example.budgetvalue.models.IncomeCategoryAmounts
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class SharedPrefWrapper @Inject constructor(val sharedPreferences: SharedPreferences) :
    ISharedPrefWrapper {
    companion object {
        val KEY_INCOME_CA = "KEY_INCOME_CA"
    }
    val editor = sharedPreferences.edit()
    override fun readIncomeCA(): List<IncomeCategoryAmounts> {
        // get User from SharedPref, and feed it into loginAttemptStream
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