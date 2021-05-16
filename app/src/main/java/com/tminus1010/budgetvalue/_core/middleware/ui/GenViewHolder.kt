package com.tminus1010.budgetvalue._core.middleware.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


class GenViewHolder(v: View) : RecyclerView.ViewHolder(v)
class GenViewHolder2<T : ViewBinding>(val vb: T) : RecyclerView.ViewHolder(vb.root)
class GenViewHolder3<T : ViewBinding>(val vb: T) : LifecycleViewHolder(vb.root)