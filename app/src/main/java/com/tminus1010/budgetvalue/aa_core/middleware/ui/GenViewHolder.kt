package com.tminus1010.budgetvalue.aa_core.middleware.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class GenViewHolder(v: View) : RecyclerView.ViewHolder(v)
class GenViewHolder2<T:ViewBinding>(val vb: T) : RecyclerView.ViewHolder(vb.root)