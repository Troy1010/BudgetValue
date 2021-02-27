package com.tminus1010.budgetvalue.layer_domain

import com.tminus1010.budgetvalue.extensions.fromJson
import com.tminus1010.budgetvalue.extensions.toJson
import com.tminus1010.budgetvalue.model_data.AccountDTO
import com.tminus1010.budgetvalue.model_domain.Account
import com.tminus1010.budgetvalue.moshi
import javax.inject.Inject

class TypeConverter @Inject constructor() : ITypeConverter {
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
}