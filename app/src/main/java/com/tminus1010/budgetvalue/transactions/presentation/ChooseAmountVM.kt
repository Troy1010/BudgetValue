package com.tminus1010.budgetvalue.transactions.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChooseAmountVM @Inject constructor(
) : ViewModel() {
    // # Presentation State
    val buttons =
        listOf(
            listOf(
                ButtonVMItem(
                    title = "Plus $10",
                    onClick = {

                    }
                ),
                ButtonVMItem(
                    title = "Minus $10",
                    onClick = {

                    }
                ),
            ),
            listOf(
                ButtonVMItem(
                    title = "Plus $1",
                    onClick = {

                    }
                ),
                ButtonVMItem(
                    title = "Minus $1",
                    onClick = {

                    }
                ),
            ),
            listOf(
                ButtonVMItem(
                    title = "Plus $0.10",
                    onClick = {

                    }
                ),
                ButtonVMItem(
                    title = "Minus $0.10",
                    onClick = {

                    }
                ),
            ),
            listOf(
                ButtonVMItem(
                    title = "Plus $0.01",
                    onClick = {

                    }
                ),
                ButtonVMItem(
                    title = "Minus $0.01",
                    onClick = {

                    }
                ),
            ),
        )
}