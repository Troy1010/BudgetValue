package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.view.clicks
import com.tminus1010.budgetvalue.GenViewHolder
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.dependency_injection.ViewModelProviders
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.flavorIntersection
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.frag_error.*
import kotlinx.android.synthetic.main.frag_import.*
import kotlinx.android.synthetic.main.item_account.view.*
import kotlinx.android.synthetic.main.item_button.view.*

class ErrorFrag : Fragment(R.layout.frag_error), IViewModelFrag {
    override val viewModelProviders by lazy { ViewModelProviders(requireActivity(), appComponent) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # RecyclerView
        recyclerview_buttons.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = object : RecyclerView.Adapter<GenViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenViewHolder =
                    LayoutInflater.from(requireContext()).inflate(R.layout.item_button, parent, false)
                        .let { GenViewHolder(it) }

                override fun onBindViewHolder(holder: GenViewHolder, position: Int) {
                    holder.itemView.btn_item.setOnClickListener { errorVM.buttons.value[holder.adapterPosition].action() }
                    holder.itemView.btn_item.text = errorVM.buttons.value[position].title
                }

                override fun getItemCount(): Int = errorVM.buttons.value.size
            }
        }
        // # Observe
        errorVM.message
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) {
                textview_message.text = it
            }
        errorVM.buttons
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) {
                recyclerview_buttons.adapter?.notifyDataSetChanged()
            }
    }
}