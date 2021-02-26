package com.tminus1010.budgetvalue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.transactionsVM
import kotlinx.android.synthetic.devEnv1.activity_mock_import_selection.*

class MockImportSelectionActivity : AppCompatActivity(R.layout.activity_mock_import_selection) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // # RecyclerView
        val transactionPathNames =
            assets.list("transactions")
                ?.map { "transactions/$it" }
                ?: emptyList()
        recyclerview_select_mock_import.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        recyclerview_select_mock_import.adapter =
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