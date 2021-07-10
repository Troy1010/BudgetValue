package com.tminus1010.budgetvalue._core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.databinding.ActivityMockImportSelectionBinding
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue._core.middleware.ui.GenViewHolder2
import com.tminus1010.budgetvalue.databinding.ItemButtonBinding
import com.tminus1010.budgetvalue.transactions.TransactionsVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MockImportSelectionActivity : AppCompatActivity(R.layout.activity_mock_import_selection) {
    val transactionsVM by viewModels<TransactionsVM>()
    val vb by viewBinding(ActivityMockImportSelectionBinding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        // # RecyclerView
        val transactionPathNames =
            assets.list("transactions")
                ?.map { "transactions/$it" }
                ?: emptyList()
        vb.recyclerviewSelectMockImport.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        vb.recyclerviewSelectMockImport.adapter =
            object : RecyclerView.Adapter<GenViewHolder2<ItemButtonBinding>>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    ItemButtonBinding.inflate(LayoutInflater.from(this@MockImportSelectionActivity), parent, false)
                        .let { GenViewHolder2(it) }

                override fun onBindViewHolder(holder: GenViewHolder2<ItemButtonBinding>, position: Int) {
                    holder.vb.btnItem.text = "Import Transactions ${holder.adapterPosition}"
                    holder.vb.btnItem.setOnClickListener {
                        assets.open(transactionPathNames[holder.adapterPosition]).buffered()
                            .also { transactionsVM.userImportTransactions(it) }
                        finish()
                    }
                }

                override fun getItemCount() = transactionPathNames.size
            }
    }
}