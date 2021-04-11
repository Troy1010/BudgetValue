package com.tminus1010.budgetvalue.accounts

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.view.clicks
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.LaunchImportUC
import com.tminus1010.budgetvalue._core.middleware.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.middleware.ui.*
import com.tminus1010.budgetvalue._core.ui.HostActivity
import com.tminus1010.budgetvalue._core.ui.view_binding.bind
import com.tminus1010.budgetvalue.accounts.models.Account
import com.tminus1010.budgetvalue.databinding.FragImportBinding
import com.tminus1010.budgetvalue.databinding.ItemAccountBinding
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.pairwise
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@AndroidEntryPoint
class ImportFrag : Fragment(R.layout.frag_import) {
    @Inject lateinit var launchImportUC: LaunchImportUC
    val accountsVM: AccountsVM by activityViewModels()
    val vb by viewBinding(FragImportBinding::bind)
    var accounts = emptyList<Account>()
        set(value) {
            val shouldNotifyDataSetChanged = field.size != value.size
            field = value
            if (shouldNotifyDataSetChanged) vb.recyclerviewAccounts.adapter?.notifyDataSetChanged()
        }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Clicks
        vb.btnImport.setOnClickListener { launchImportUC(requireActivity() as HostActivity) }
        vb.btnAddAccount.setOnClickListener { accountsVM.addAccount() }
        // # RecyclerView
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
    }
}