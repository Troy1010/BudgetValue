package com.tminus1010.budgetvalue

import android.content.res.AssetManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue._core.framework.view.Toaster
import com.tminus1010.budgetvalue._core.framework.view.GenViewHolder2
import com.tminus1010.budgetvalue.databinding.ActivityMockImportSelectionBinding
import com.tminus1010.budgetvalue.databinding.ItemButtonBinding
import com.tminus1010.budgetvalue.importZ.data.ImportTransactions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@VisibleForTesting
@AndroidEntryPoint
class MockImportSelectionActivity : AppCompatActivity() {
    val vb by lazy { ActivityMockImportSelectionBinding.inflate(layoutInflater) }

    @Inject
    lateinit var assets2: AssetManager

    @Inject
    lateinit var toaster: Toaster

    @Inject
    lateinit var importTransactions: ImportTransactions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        // # RecyclerView
        val transactionPathNames =
            assets2.list("transactions")
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
                        importTransactions(assets.open(transactionPathNames[holder.adapterPosition]).buffered()).subscribe()
                        toaster.toast(R.string.import_successful)
                        finish()
                    }
                }

                override fun getItemCount() = transactionPathNames.size
            }
    }
}