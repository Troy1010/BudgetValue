package com.tminus1010.budgetvalue.accounts

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.LaunchImport
import com.tminus1010.budgetvalue._core.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.middleware.presentation.ButtonVMItem
import com.tminus1010.budgetvalue._core.middleware.view.GenViewHolder2
import com.tminus1010.budgetvalue._core.middleware.view.onDone
import com.tminus1010.budgetvalue._core.middleware.view.viewBinding
import com.tminus1010.budgetvalue._core.ui.HostActivity
import com.tminus1010.budgetvalue.accounts.models.Account
import com.tminus1010.budgetvalue.databinding.FragImportBinding
import com.tminus1010.budgetvalue.databinding.ItemAccountBinding
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ImportFrag : Fragment(R.layout.frag_import) {
    @Inject
    lateinit var launchImport: LaunchImport
    private val accountsVM: AccountsVM by activityViewModels()
    private val vb by viewBinding(FragImportBinding::bind)
    var accounts = emptyList<Account>()
        set(value) {
            field = value; vb.recyclerviewAccounts.adapter?.notifyDataSetChanged()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Accounts RecyclerView
        accountsVM.accounts.observe(viewLifecycleOwner) { accounts = it }
        vb.recyclerviewAccounts.layoutManager = LinearLayoutManager(requireActivity())
        vb.recyclerviewAccounts.adapter = object : RecyclerView.Adapter<GenViewHolder2<ItemAccountBinding>>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                GenViewHolder2(ItemAccountBinding.inflate(layoutInflater, parent, false))

            override fun getItemCount() = accounts.size
            override fun onBindViewHolder(holder: GenViewHolder2<ItemAccountBinding>, position: Int) {
                holder.vb.btnDeleteAccount.setOnClickListener { accountsVM.userDeleteAccount(accounts[holder.adapterPosition]) }
                holder.vb.edittextName.setText(accounts[holder.adapterPosition].name)
                holder.vb.edittextName.onDone {
                    if (holder.adapterPosition != RecyclerView.NO_POSITION)
                        accountsVM.userUpdateAccount(accounts[holder.adapterPosition].copy(name = it))
                }
                holder.vb.edittextAmount.setText(accounts[holder.adapterPosition].amount.toString())
                holder.vb.edittextAmount.onDone {
                    if (holder.adapterPosition != RecyclerView.NO_POSITION)
                        accountsVM.userUpdateAccount(accounts[holder.adapterPosition].copy(amount = it.toMoneyBigDecimal()))
                }
            }
        }
        // # Button RecyclerView
        vb.buttonsview.buttons = listOfNotNull(
            ButtonVMItem(
                title = "Import",
                onClick = { launchImport(requireActivity() as HostActivity) },
            ),
            ButtonVMItem(
                title = "Add Account",
                onClick = { accountsVM.userAddAccount() }
            ),
        ).reversed()
    }
}