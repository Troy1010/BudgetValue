package com.tminus1010.budgetvalue

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class GenViewHolder(v: View) : RecyclerView.ViewHolder(v)
class GenViewHolder2<T:ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)