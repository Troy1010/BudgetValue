package com.tminus1010.budgetvalue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue.databinding.ActivityMockImportSelectionBinding
import com.tminus1010.budgetvalue.dependency_injection.ViewModelProviders
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.middleware.GenViewHolder

class MockImportSelectionActivity : AppCompatActivity(R.layout.activity_mock_import_selection) {
    val binding by viewBinding(ActivityMockImportSelectionBinding::inflate)
    val vmps by lazy { ViewModelProviders(this, appComponent) }
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
                            .also { vmps.transactionsVM.importTransactions(it) }
                        finish()
                    }
                }

                override fun getItemCount() = transactionPathNames.size
            }
    }
}