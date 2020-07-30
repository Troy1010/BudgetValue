package com.example.budgetvalue.layers.view_models

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.budgetvalue.layers.data_layer.Repo
import com.example.tmcommonkotlin.logz
import java.lang.Exception

class TransactionsVM(private val repo: Repo):ViewModel() {
    fun importTransactions(uri: Uri): Boolean {
        return try {
            val transactions = repo.parseCsvToTransactions(uri)
            repo.add(transactions)
            logz("transactions:${repo.getTransactions().joinToString(",")}")
            true
        } catch (e: Exception) {
            false
        }
    }
}