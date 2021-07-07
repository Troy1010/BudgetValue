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
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonRVItem
import com.tminus1010.budgetvalue._core.middleware.ui.GenViewHolder2
import com.tminus1010.budgetvalue._core.middleware.ui.LifecycleRVAdapter
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue._core.ui.HostActivity
import com.tminus1010.budgetvalue._core.ui.data_binding.bind
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
            val shouldNotifyDataSetChanged = field.size != value.size
            field = value
            if (shouldNotifyDataSetChanged) vb.recyclerviewAccounts.adapter?.notifyDataSetChanged()
        }
    var btns = emptyList<ButtonRVItem>()
        set(value) {
            field = value; vb.recyclerviewButtons.adapter?.notifyDataSetChanged()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Accounts RecyclerView
        accountsVM.accounts.observe(viewLifecycleOwner) { accounts = it }
        vb.recyclerviewAccounts.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = object : RecyclerView.Adapter<GenViewHolder2<ItemAccountBinding>>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    ItemAccountBinding.inflate(layoutInflater, parent, false)
                        .let { GenViewHolder2(it) }

                override fun getItemCount() = accounts.size
                override fun onBindViewHolder(holder: GenViewHolder2<ItemAccountBinding>, position: Int) {
                    holder.vb.bind(accounts[holder.adapterPosition], accountsVM)
                }
            }
        }

        // # Button RecyclerView
        vb.recyclerviewButtons.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        vb.recyclerviewButtons.addItemDecoration(LayoutMarginDecoration(8.toPX(requireContext())))
        vb.recyclerviewButtons.adapter = object : LifecycleRVAdapter<GenViewHolder2<ItemButtonBinding>>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                ItemButtonBinding.inflate(LayoutInflater.from(requireContext()), parent, false)
                    .let { GenViewHolder2(it) }

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
                onClick = { accountsVM.addAccount() }
            ),
        ).reversed()
    }
}