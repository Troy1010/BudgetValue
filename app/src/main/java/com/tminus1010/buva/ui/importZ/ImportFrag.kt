package com.tminus1010.buva.ui.importZ

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.easyText2
import com.tminus1010.buva.databinding.FragImportBinding
import com.tminus1010.buva.databinding.ItemAccountBinding
import com.tminus1010.buva.databinding.ItemButtonBinding
import com.tminus1010.buva.all_layers.android.LifecycleRVAdapter2
import com.tminus1010.buva.all_layers.android.onDone
import com.tminus1010.buva.all_layers.android.viewBinding
import com.tminus1010.tmcommonkotlin.androidx.GenViewHolder
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImportFrag : Fragment(R.layout.frag_import) {
    private val viewModel by viewModels<ImportVM>()
    private val vb by viewBinding(FragImportBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # State
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
        vb.recyclerviewAccounts.layoutManager = LinearLayoutManager(requireActivity())
        vb.recyclerviewAccounts.bind(viewModel.accountVMItemList) { accountsPresentationModel ->
            adapter = object : LifecycleRVAdapter2<GenViewHolder<ItemAccountBinding>>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    GenViewHolder(ItemAccountBinding.inflate(layoutInflater, parent, false))

                override fun getItemCount() = accountsPresentationModel.size

                override fun onLifecycleAttached(holder: GenViewHolder<ItemAccountBinding>) {
                    accountsPresentationModel[holder.bindingAdapterPosition].bind(holder.vb)
                }
            }
        }
    }
}