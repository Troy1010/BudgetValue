package com.tminus1010.buva.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal


@Entity
data class Account(
    val name: String,
    val amount: BigDecimal,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)