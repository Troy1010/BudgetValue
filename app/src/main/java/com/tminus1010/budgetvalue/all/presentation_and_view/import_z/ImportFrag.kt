package com.tminus1010.budgetvalue.all.presentation_and_view.import_z

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.LaunchSelectFile
import com.tminus1010.budgetvalue._core.extensions.easyText
import com.tminus1010.budgetvalue._core.middleware.view.GenViewHolder2
import com.tminus1010.budgetvalue._core.middleware.view.onDone
import com.tminus1010.budgetvalue._core.middleware.view.viewBinding
import com.tminus1010.budgetvalue._middleware.view.extensions.onClick
import com.tminus1010.budgetvalue.all.presentation_and_view._models.AccountVMItem
import com.tminus1010.budgetvalue.databinding.FragImportBinding
import com.tminus1010.budgetvalue.databinding.ItemAccountBinding
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ImportFrag : Fragment(R.layout.frag_import) {
    @Inject
    lateinit var launchSelectFile: LaunchSelectFile
    private val accountsVM: AccountsVM by activityViewModels()
    private val vb by viewBinding(FragImportBinding::bind)
    var accounts = emptyList<AccountVMItem>()
        set(value) {
            field = value; vb.recyclerviewAccounts.adapter?.notifyDataSetChanged()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Buttons
        vb.buttonsview.buttons = accountsVM.buttons
        // # Accounts
        accountsVM.accounts.observe(viewLifecycleOwner) { accounts = it }
        vb.recyclerviewAccounts.layoutManager = LinearLayoutManager(requireActivity())
        vb.recyclerviewAccounts.adapter = object : RecyclerView.Adapter<GenViewHolder2<ItemAccountBinding>>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                GenViewHolder2(ItemAccountBinding.inflate(layoutInflater, parent, false))

            override fun getItemCount() = accounts.size
            override fun onBindViewHolder(holder: GenViewHolder2<ItemAccountBinding>, position: Int) {
                val vb = holder.vb
                val accountVMItem = accounts[position]
                vb.edittextName.easyText = accountVMItem.title
                vb.edittextName.onDone {
                    // TODO("There should be a better way to avoid NO_POSITION error.")
                    if (holder.adapterPosition == RecyclerView.NO_POSITION) return@onDone
                    val accountVMItem = accounts[holder.adapterPosition]
                    accountVMItem.userSetTitle(it)
                }
                vb.edittextAmount.easyText = accountVMItem.amount
                vb.edittextAmount.onDone {
                    // TODO("There should be a better way to avoid NO_POSITION error.")
                    if (holder.adapterPosition == RecyclerView.NO_POSITION) return@onDone
                    val accountVMItem = accounts[holder.adapterPosition]
                    accountVMItem.userSetAmount(it)
                }
                vb.btnDeleteAccount.onClick(accountVMItem::userDeleteAccount)
            }
        }
    }
}