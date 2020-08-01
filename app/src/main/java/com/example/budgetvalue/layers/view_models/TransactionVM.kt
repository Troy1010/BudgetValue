package com.example.budgetvalue.layers.view_models

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetvalue.layers.data_layer.Repo
import com.example.budgetvalue.models.Transaction
import com.example.tmcommonkotlin.logz
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TransactionVM(repo: Repo, transactionsVM: TransactionsVM): ViewModel() {
    val transaction = MediatorLiveData<Transaction>()
    init {
        viewModelScope.launch(Dispatchers.IO) {
            while (transactionsVM.transactions.value?.get(0) == null) {
                delay(50)
            }
            transaction.postValue(transactionsVM.transactions.value?.get(0))
        }
    }
}