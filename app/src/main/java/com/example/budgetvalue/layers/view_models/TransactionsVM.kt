package com.example.budgetvalue.layers.view_models

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.budgetvalue.layers.data_layer.Repo
import java.lang.Exception

class TransactionsVM(private val repo: Repo):ViewModel() {
    fun importTransactions(uri: Uri): Boolean {
        return try {
            val transactions = repo.parseCsvToTransactions(uri)
            true
        } catch (e: Exception) {
            false
        }
    }
}