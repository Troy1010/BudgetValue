package com.example.budgetvalue.layers.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetvalue.layers.data_layer.Repo
import com.example.budgetvalue.models.Transaction
import com.example.tmcommonkotlin.logz
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionVM(repo: Repo): ViewModel() {
    val transaction = MutableLiveData<Transaction>()
    init {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getTransactions().getOrNull(0)?.let {
                transaction.postValue(it)
            }
        }
    }
}