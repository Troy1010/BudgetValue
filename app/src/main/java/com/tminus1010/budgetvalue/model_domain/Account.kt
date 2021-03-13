package com.tminus1010.budgetvalue.model_domain

import com.tminus1010.budgetvalue.model_data.AccountDTO
import java.math.BigDecimal


data class Account(
    val name: String,
    val amount: BigDecimal,
    val id: Int = 0,
) {
    fun toDTO(): AccountDTO =
        AccountDTO(name, amount.toString(), id)
    companion object {
        fun fromDTO(accountDTO: AccountDTO) =
            Account(accountDTO.name, accountDTO.amount.toBigDecimal(), accountDTO.id)
    }
}