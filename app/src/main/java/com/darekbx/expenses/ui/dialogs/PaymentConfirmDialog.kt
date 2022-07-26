package com.darekbx.expenses.ui

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.expenses.viewmodel.ExpensesViewModel

@Composable
fun PaymentConfirmDialog(
    expensesViewModel: ExpensesViewModel = hiltViewModel()
) {
    val amount = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {
            expensesViewModel.onEvent(ExpensesViewModel.UIEvent.CloseConfirmPaymentDialog)
        },
        title = {
            Text(text = "Confirm new payment")
        },
        text = {
            OutlinedTextField(
                value = amount.value,
                onValueChange = { amount.value = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    expensesViewModel.onEvent(
                        ExpensesViewModel.UIEvent.CloseConfirmPaymentDialogAndSave(amount.value)
                    )
                }) {
                Text("Make Payment")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    expensesViewModel.onEvent(ExpensesViewModel.UIEvent.CloseConfirmPaymentDialog)
                }) {
                Text("Cancel")
            }
        }
    )
}