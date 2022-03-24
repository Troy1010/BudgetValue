package com.tminus1010.budgetvalue.categories

import com.tminus1010.budgetvalue.all_features.data.service.MoshiProvider.moshi
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.all_features.app.model.Category
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
import com.tminus1010.tmcommonkotlin.core.extensions.associate
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import javax.inject.Inject

class CategoryAmountFormulasConverter @Inject constructor(
    private val categoryParser: CategoriesInteractor,
) {
    fun toCategoryAmountFormulas(s: String?): Map<Category, AmountFormula> =
        if (s == null) emptyMap() else
            moshi.fromJson<Map<String, String>>(s)
                .associate { categoryParser.parseCategory(it.key) to AmountFormula.fromDTO((it.value as Any).toString()) }

    fun toJson(categoryAmounts: Map<Category, AmountFormula>): String =
        categoryAmounts
            .associate { it.key.name to it.value.toDTO() }
            .let { moshi.toJson(it) }
}