package com.tminus1010.budgetvalue.aa_core.middleware.ui

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyTableViewLayoutManager(
    context: Context
) : LinearLayoutManager(context, RecyclerView.VERTICAL, false) {
    override fun isAutoMeasureEnabled()=false
}