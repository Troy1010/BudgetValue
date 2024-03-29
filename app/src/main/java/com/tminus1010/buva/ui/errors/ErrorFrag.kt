package com.tminus1010.buva.ui.errors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.android.viewBinding
import com.tminus1010.buva.databinding.FragErrorBinding
import com.tminus1010.buva.databinding.ItemButtonBinding
import com.tminus1010.tmcommonkotlin.androidx.GenViewHolder
import com.tminus1010.tmcommonkotlin.rx3.extensions.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ErrorFrag : Fragment(R.layout.frag_error) {
    val errorVM: ErrorVM by activityViewModels()
    val vb by viewBinding(FragErrorBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # RecyclerView
        vb.recyclerviewButtons.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = object : RecyclerView.Adapter<GenViewHolder<ItemButtonBinding>>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    ItemButtonBinding.inflate(LayoutInflater.from(requireContext()), parent, false)
                        .let { GenViewHolder(it) }

                override fun onBindViewHolder(holder: GenViewHolder<ItemButtonBinding>, position: Int) {
                    holder.vb.btnItem.setOnClickListener { errorVM.buttons.value[holder.adapterPosition].onClick() }
                    holder.vb.btnItem.text = errorVM.buttons.value[position].title
                }

                override fun getItemCount(): Int = errorVM.buttons.value.size
            }
        }
        // # Message
        errorVM.message
            .observe(viewLifecycleOwner) {
                vb.textviewMessage.text = it
            }
        // # Buttons
        errorVM.buttons
            .observe(viewLifecycleOwner) {
                vb.recyclerviewButtons.adapter?.notifyDataSetChanged()
            }
    }
}