package com.tminus1010.buva.domain


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
@Entity
data class AccountsUpdateInfo(
    val date: LocalDate,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
) : Parcelable