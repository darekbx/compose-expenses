package com.darekbx.expenses.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.expenses.model.Payment
import com.darekbx.expenses.viewmodel.ExpensesViewModel

@Composable
fun PaymentsScreen(
    expensesViewModel: ExpensesViewModel = hiltViewModel()
) {
    val payments by expensesViewModel.loadAllPayments().observeAsState(initial = emptyList())
    LazyColumn(Modifier.padding(8.dp)) {
        items(payments, { payment: Payment -> payment.uid!! }) { payment ->
            Card(
                modifier = Modifier.padding(8.dp),
                elevation = 4.dp
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = "Payment: ${payment.amount}zł",
                            style = MaterialTheme.typography.h6
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = "Spent: ${payment.summaryExpenses()}zł",
                            style = MaterialTheme.typography.subtitle1
                        )
                    }
                    Column {
                        Text(
                            modifier = Modifier.padding(end = 8.dp),
                            text = payment.timestamp,
                            style = MaterialTheme.typography.caption
                        )
                        Text(
                            modifier = Modifier.padding(end = 8.dp),
                            text = "Expenses: ${payment.expensesCount()}",
                            style = MaterialTheme.typography.caption
                        )
                    }
                }
            }
        }
    }
}