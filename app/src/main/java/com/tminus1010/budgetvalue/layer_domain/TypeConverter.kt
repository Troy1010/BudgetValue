package com.tminus1010.budgetvalue.layer_domain

import com.tminus1010.budgetvalue.extensions.fromJson
import com.tminus1010.budgetvalue.extensions.toJson
import com.tminus1010.budgetvalue.layer_data.ICategoryParser
import com.tminus1010.budgetvalue.model_data.AccountDTO
import com.tminus1010.budgetvalue.model_data.Category
import com.tminus1010.budgetvalue.model_domain.Account
import com.tminus1010.budgetvalue.moshi
import com.tminus1010.tmcommonkotlin.rx.extensions.associate
import java.math.BigDecimal
import javax.inject.Inject

class TypeConverter @Inject constructor(
    val categoryParser: ICategoryParser,
) : ITypeConverter {
    override fun toAccount(accountDTO: AccountDTO): Account =
        Account(
            name = accountDTO.name,
            amount = moshi.fromJson(accountDTO.amount),
            id = accountDTO.id
        )

    override fun toAccountDTO(accountDTO: Account): AccountDTO =
        AccountDTO(
            name = accountDTO.name,
            amount = moshi.toJson(accountDTO.amount),
            id = accountDTO.id
        )

    override fun toCategoryAmount(s: String?): Map<Category, BigDecimal> =
        if (s == null) emptyMap() else
            moshi.fromJson<Map<String, String>>(s)
                .associate { categoryParser.parseCategory(it.key) to (it.value as Any).toString().toBigDecimal() }

    override fun toString(categoryAmounts: Map<Category, BigDecimal>): String =
        categoryAmounts
            .associate { it.key.name to it.value.toString() }
            .let { moshi.toJson(it) }
}