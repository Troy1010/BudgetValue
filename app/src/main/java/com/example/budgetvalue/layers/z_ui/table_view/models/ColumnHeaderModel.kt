package com.example.budgetvalue.layers.z_ui.table_view.models

import androidx.lifecycle.LiveData
import java.math.BigDecimal

data class ColumnHeaderModel(val title: String, val amount: LiveData<BigDecimal>? =null)