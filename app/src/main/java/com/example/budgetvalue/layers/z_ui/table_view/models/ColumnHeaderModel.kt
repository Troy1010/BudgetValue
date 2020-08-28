package com.example.budgetvalue.layers.z_ui.table_view.models

import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal

data class ColumnHeaderModel(val title: String, val amount: BigDecimal? =null)