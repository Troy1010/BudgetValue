package com.tminus1010.budgetvalue.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thekhaeng.recyclerviewmargin.LayoutMarginDecoration
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.LaunchImportUC
import com.tminus1010.budgetvalue._core.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.middleware.ui.*
import com.tminus1010.budgetvalue._core.ui.HostActivity
import com.tminus1010.budgetvalue._core.ui.data_binding.bindButtonRVItem
import com.tminus1010.budgetvalue.accounts.models.Account
import com.tminus1010.budgetvalue.databinding.FragImportBinding
import com.tminus1010.budgetvalue.databinding.ItemAccountBinding
import com.tminus1010.budgetvalue.databinding.ItemButtonBinding
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.toPX
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ImportFrag : Fragment(R.layout.frag_import) {
    @Inject
    lateinit var launchImportUC: LaunchImportUC
    private val accountsVM: AccountsVM by activityViewModels()
    private val vb by viewBinding(FragImportBinding::bind)
    var accounts = emptyList<Account>()
        set(value) {
            field = value; vb.recyclerviewAccounts.adapter?.notifyDataSetChanged()
        }
    var btns = emptyList<ButtonRVItem>()
        set(value) {
            field = value; vb.recyclerviewButtons.adapter?.notifyDataSetChanged()
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
                holder.vb.editTextName.setText(accounts[holder.adapterPosition].name)
                holder.vb.editTextName.onDone {
                    if (holder.adapterPosition != RecyclerView.NO_POSITION)
                        accountsVM.userUpdateAccount(accounts[holder.adapterPosition].copy(name = it))
                }
                holder.vb.editTextAmount.setText(accounts[holder.adapterPosition].amount.toString())
                holder.vb.editTextAmount.onDone {
                    if (holder.adapterPosition != RecyclerView.NO_POSITION)
                        accountsVM.userUpdateAccount(accounts[holder.adapterPosition].copy(amount = it.toMoneyBigDecimal()))
                }
            }
        }
        // # Button RecyclerView
        vb.recyclerviewButtons.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        vb.recyclerviewButtons.addItemDecoration(LayoutMarginDecoration(8.toPX(requireContext())))
        vb.recyclerviewButtons.adapter = object : LifecycleRVAdapter<GenViewHolder2<ItemButtonBinding>>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                GenViewHolder2(ItemButtonBinding.inflate(LayoutInflater.from(requireContext()), parent, false))

            override fun onViewAttachedToWindow(holder: GenViewHolder2<ItemButtonBinding>, lifecycle: LifecycleOwner) {
                holder.vb.btnItem.bindButtonRVItem(lifecycle, btns[holder.adapterPosition])
            }

            override fun getItemCount() = btns.size
        }
        btns = listOfNotNull(
            ButtonRVItem(
                title = "Import",
                onClick = { launchImportUC(requireActivity() as HostActivity) },
            ),
            ButtonRVItem(
                title = "Add Account",
                onClick = { accountsVM.userAddAccount() }
            ),
        ).reversed()
    }
}