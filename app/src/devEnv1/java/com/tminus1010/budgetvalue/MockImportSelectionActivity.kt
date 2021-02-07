package com.tminus1010.budgetvalue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue.extensions.viewModels2
import com.tminus1010.budgetvalue.layer_ui.TransactionsVM
import kotlinx.android.synthetic.devEnv1.activity_mock_import_selection.*

class MockImportSelectionActivity : AppCompatActivity(R.layout.activity_mock_import_selection) {
    val app by lazy { application as App }
    val transactionsVM: TransactionsVM by viewModels2 {
        TransactionsVM(app.appComponent.getRepo(),
            app.appComponent.getDatePeriodGetter())
    }

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
            object : RecyclerView.Adapter<GenericViewHolder<Button>>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    LayoutInflater.from(this@MockImportSelectionActivity)
                        .inflate(R.layout.button, parent, false)
                        .let { GenericViewHolder(it as Button) }

                override fun onBindViewHolder(holder: GenericViewHolder<Button>, position: Int) {
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