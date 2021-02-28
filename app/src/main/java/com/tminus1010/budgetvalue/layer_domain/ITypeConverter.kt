package com.tminus1010.budgetvalue.layer_domain

import com.tminus1010.budgetvalue.model_data.AccountDTO
import com.tminus1010.budgetvalue.model_domain.Account

interface ITypeConverter {
    fun toAccount(accountDTO: AccountDTO): Account
    fun toAccountDTO(accountDTO: Account): AccountDTO
}