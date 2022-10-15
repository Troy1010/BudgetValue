package com.tminus1010.buva.domain


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class TransactionImportInfo(
    val period: LocalDatePeriod,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
) : Parcelable