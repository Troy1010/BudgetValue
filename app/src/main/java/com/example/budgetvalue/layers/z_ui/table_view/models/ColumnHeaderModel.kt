package com.example.budgetvalue.layers.z_ui.table_view.models

import androidx.lifecycle.MediatorLiveData
import java.math.BigDecimal

data class ColumnHeaderModel(val title: String, val amount: MediatorLiveData<BigDecimal>? =null)