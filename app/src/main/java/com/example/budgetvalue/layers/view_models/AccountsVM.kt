package com.example.budgetvalue.layers.view_models

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.layers.data_layer.Repo

class AccountsVM(val repo: Repo): ViewModel() {
    val accounts = repo.getAccounts()
}