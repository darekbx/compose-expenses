package com.darekbx.expenses.ui

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.expenses.viewmodel.ExpensesViewModel

@Composable
fun NoPaymentDialog(
    expensesViewModel: ExpensesViewModel = hiltViewModel()
) {
    AlertDialog(
        onDismissRequest = {
            expensesViewModel.onEvent(ExpensesViewModel.UIEvent.CloseNoPaymentDialog)
        },
        title = {
            Text(text = "Add expense")
        },
        text = {
            Text(text = "There's no payments, please add new to continue")
        },
        confirmButton = {
            Button(
                onClick = {
                    expensesViewModel.onEvent(
                        ExpensesViewModel.UIEvent.CloseNoPaymentDialog
                    )
                }) {
                Text("Ok")
            }
        }
    )
}
