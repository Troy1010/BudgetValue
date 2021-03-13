package com.tminus1010.budgetvalue.layer_domain

import com.tminus1010.budgetvalue.model_data.AccountDTO
import com.tminus1010.budgetvalue.model_domain.Account
import com.tminus1010.budgetvalue.model_domain.Category
import java.math.BigDecimal

interface ITypeConverter {
    fun toAccount(accountDTO: AccountDTO): Account
    fun toAccountDTO(accountDTO: Account): AccountDTO
    fun toCategoryAmount(s: String?): Map<Category, BigDecimal>
    fun toString(categoryAmounts: Map<Category, BigDecimal>): String
}