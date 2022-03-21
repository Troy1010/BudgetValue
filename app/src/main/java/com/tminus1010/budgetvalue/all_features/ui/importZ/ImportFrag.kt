package com.tminus1010.budgetvalue.all_features.ui.importZ

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.easyText2
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.onClick
import com.tminus1010.budgetvalue.all_features.framework.view.GenViewHolder2
import com.tminus1010.budgetvalue.all_features.framework.view.onDone
import com.tminus1010.budgetvalue.all_features.framework.view.viewBinding
import com.tminus1010.budgetvalue.all_features.ui.all_features.model.AccountPresentationModel
import com.tminus1010.budgetvalue.databinding.FragImportBinding
import com.tminus1010.budgetvalue.databinding.ItemAccountBinding
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImportFrag : Fragment(R.layout.frag_import) {
    private val importVM by activityViewModels<ImportVM>()
    private val vb by viewBinding(FragImportBinding::bind)
    var accounts = emptyList<AccountPresentationModel>()
        set(value) {
            field = value; vb.recyclerviewAccounts.adapter?.notifyDataSetChanged()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Buttons
        vb.buttonsview.buttons = importVM.buttons
        // # Accounts
        importVM.accountVMItemList.observe(viewLifecycleOwner) { accounts = it }
        vb.recyclerviewAccounts.layoutManager = LinearLayoutManager(requireActivity())
        vb.recyclerviewAccounts.adapter = object : RecyclerView.Adapter<GenViewHolder2<ItemAccountBinding>>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                GenViewHolder2(ItemAccountBinding.inflate(layoutInflater, parent, false))

            override fun getItemCount() = accounts.size
            override fun onBindViewHolder(holder: GenViewHolder2<ItemAccountBinding>, position: Int) {
                val vb = holder.vb
                val accountVMItem = accounts[position]
                vb.edittextName.easyText2 = accountVMItem.title
                vb.edittextName.onDone {
                    // TODO("There should be a better way to avoid NO_POSITION error.")
                    if (holder.adapterPosition == RecyclerView.NO_POSITION) return@onDone
                    val accountVMItem = accounts[holder.adapterPosition]
                    accountVMItem.userSetTitle(it)
                }
                vb.edittextAmount.easyText2 = accountVMItem.amount
                vb.edittextAmount.onDone {
                    // TODO("There should be a better way to avoid NO_POSITION error.")
                    if (holder.adapterPosition == RecyclerView.NO_POSITION) return@onDone
                    val accountVMItem = accounts[holder.adapterPosition]
                    accountVMItem.userSetAmount(it)
                }
                vb.btnDeleteAccount.onClick { accountVMItem.userDeleteAccount() }
            }
        }
    }
}