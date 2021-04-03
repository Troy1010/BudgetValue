package com.tminus1010.budgetvalue._core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.databinding.ActivityMockImportSelectionBinding
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue._core.middleware.ui.GenViewHolder
import com.tminus1010.budgetvalue.transactions.TransactionsVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MockImportSelectionActivity : AppCompatActivity(R.layout.activity_mock_import_selection) {
    val transactionsVM by viewModels<TransactionsVM>()
    val binding by viewBinding(ActivityMockImportSelectionBinding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // # RecyclerView
        val transactionPathNames =
            assets.list("transactions")
                ?.map { "transactions/$it" }
                ?: emptyList()
        binding.recyclerviewSelectMockImport.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        binding.recyclerviewSelectMockImport.adapter =
            object : RecyclerView.Adapter<GenViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    LayoutInflater.from(this@MockImportSelectionActivity)
                        .inflate(R.layout.button, parent, false)
                        .let { GenViewHolder(it as Button) }

                override fun onBindViewHolder(holder: GenViewHolder, position: Int) {
                    holder.itemView as Button
                    holder.itemView.text = "Import Transactions ${holder.adapterPosition}"
                    holder.itemView.setOnClickListener {
                        assets.open(transactionPathNames[holder.adapterPosition]).buffered()
                            .also { transactionsVM.importTransactions(it) }
                        finish()
                    }
                }

                override fun getItemCount() = transactionPathNames.size
            }
    }
}