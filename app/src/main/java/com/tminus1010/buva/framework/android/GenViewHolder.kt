package com.tminus1010.buva.framework.android

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class GenViewHolder2<T : ViewBinding>(val vb: T) : RecyclerView.ViewHolder(vb.root)