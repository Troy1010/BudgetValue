package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue.middleware.GenViewHolder2
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.databinding.FragErrorBinding
import com.tminus1010.budgetvalue.databinding.ItemButtonBinding
import com.tminus1010.budgetvalue.dependency_injection.ViewModelProviders
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue.layer_ui.misc.viewBinding
import com.tminus1010.budgetvalue.features_shared.IViewModels
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class ErrorFrag : Fragment(R.layout.frag_error), IViewModels {
    override val viewModelProviders by lazy { ViewModelProviders(requireActivity(), appComponent) }
    val binding by viewBinding(FragErrorBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # RecyclerView
        binding.recyclerviewButtons.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = object : RecyclerView.Adapter<GenViewHolder2<ItemButtonBinding>>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    ItemButtonBinding.inflate(LayoutInflater.from(requireContext()), parent, false)
                        .let { GenViewHolder2(it) }

                override fun onBindViewHolder(holder: GenViewHolder2<ItemButtonBinding>, position: Int) {
                    holder.binding.btnItem.setOnClickListener { errorVM.buttons.value[holder.adapterPosition].action() }
                    holder.binding.btnItem.text = errorVM.buttons.value[position].title
                }

                override fun getItemCount(): Int = errorVM.buttons.value.size
            }
        }
        // # Message
        errorVM.message
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) {
                binding.textviewMessage.text = it
            }
        // # Buttons
        errorVM.buttons
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) {
                binding.recyclerviewButtons.adapter?.notifyDataSetChanged()
            }
    }
}