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
import com.tminus1010.buva.framework.android.GenViewHolder2
import com.tminus1010.buva.framework.android.onDone
import com.tminus1010.buva.framework.android.viewBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
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
            adapter = object : RecyclerView.Adapter<GenViewHolder2<ItemAccountBinding>>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    GenViewHolder2(ItemAccountBinding.inflate(layoutInflater, parent, false))

                override fun getItemCount() = accountsPresentationModel.size
                override fun onBindViewHolder(holder: GenViewHolder2<ItemAccountBinding>, position: Int) {
                    val vb = holder.vb
                    val accountPresentationModel = accountsPresentationModel[position]
                    vb.edittextName.easyText2 = accountPresentationModel.title
                    vb.edittextName.onDone {
                        // TODO("There should be a better way to avoid NO_POSITION error.")
                        if (holder.adapterPosition == RecyclerView.NO_POSITION) return@onDone
                        val accountVMItem = accountsPresentationModel[holder.adapterPosition]
                        accountVMItem.userSetTitle(it)
                    }
                    vb.edittextAmount.easyText2 = accountPresentationModel.amount
                    vb.edittextAmount.onDone {
                        // TODO("There should be a better way to avoid NO_POSITION error.")
                        if (holder.adapterPosition == RecyclerView.NO_POSITION) return@onDone
                        val accountVMItem = accountsPresentationModel[holder.adapterPosition]
                        accountVMItem.userSetAmount(it)
                    }
                    vb.btnDeleteAccount.setOnClickListener { accountPresentationModel.userDeleteAccount() }
                }
            }
        }
    }
}