package com.example.budgetvalue.layer_data

import com.example.budgetvalue.SourceHashMap
import com.example.budgetvalue.extensions.associate
import com.example.budgetvalue.extensions.toSourceHashMap
import com.example.budgetvalue.getTypeForGson
import com.example.budgetvalue.model_app.Category
import com.example.budgetvalue.model_app.ICategoryParser
import com.example.budgetvalue.model_app.Transaction
import com.example.budgetvalue.model_data.ICategoryAmountReceived
import com.example.budgetvalue.model_data.TransactionReceived
import com.google.gson.Gson
import java.math.BigDecimal
import javax.inject.Inject

class TypeConverterUtil @Inject constructor(
    val categoryParser: ICategoryParser
): ICategoryParser by categoryParser {
    fun transactions(transactionsReceived: Iterable<TransactionReceived>): List<Transaction> {
        return transactionsReceived.map { it.toTransaction(categoryParser) }
    }

    fun categoryAmounts(categoryAmountsReceived: Iterable<ICategoryAmountReceived>): SourceHashMap<Category, BigDecimal> {
        return categoryAmountsReceived
            .associate { categoryParser.parseCategory(it.categoryName) to it.amount }
            .toSourceHashMap()
    }

    fun categoryAmounts(s: String?): SourceHashMap<Category, BigDecimal> {
        val reconcileCategoryAmountsReceived: Map<String, String> =
            if (s == null) emptyMap() else {
                Gson().fromJson(s, getTypeForGson<HashMap<String, String>>())
            }
        return reconcileCategoryAmountsReceived
            .associate { categoryParser.parseCategory(it.key) to it.value.toBigDecimal() }
            .toSourceHashMap()
    }

    fun string(categoryAmounts: Map<Category, BigDecimal>?): String? {
        val reconcileCategoryAmountsReceived = categoryAmounts?.associate { it.key.name to it.value.toString() }
        return if (reconcileCategoryAmountsReceived == null) null else {
            Gson().toJson(reconcileCategoryAmountsReceived)
        }
    }
}