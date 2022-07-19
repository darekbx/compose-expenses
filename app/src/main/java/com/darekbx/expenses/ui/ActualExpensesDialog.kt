package com.darekbx.expenses.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.expenses.model.Expense
import com.darekbx.expenses.viewmodel.ExpensesViewModel

@Composable
fun ActualExpensesDialog(
    expensesViewModel: ExpensesViewModel = hiltViewModel(),
    expensesType: Expense.Type
) {
    val expenses by expensesViewModel.listActiveExpenses(expensesType).observeAsState()

    Dialog(
        onDismissRequest = {
            expensesViewModel.onEvent(ExpensesViewModel.UIEvent.CloseActualExpensesDialog)
        },
        content = {
            Surface(
                modifier = Modifier
                    .fillMaxHeight(0.8F)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = "'${expensesType.text}' expenses",
                        style = MaterialTheme.typography.h6
                    )

                    expenses?.let { list ->
                        ExpensesList(
                            modifier = Modifier.padding(8.dp).fillMaxHeight(0.9F),
                            expenses = list
                        )
                    } ?: run { Progress() }

                    Button(
                        modifier = Modifier.align(Alignment.End),
                        onClick = {
                        expensesViewModel.onEvent(ExpensesViewModel.UIEvent.CloseActualExpensesDialog)
                    }) {
                        Text("Close")
                    }
                }
            }
        }
    )
}

@Composable
private fun ExpensesList(modifier: Modifier = Modifier, expenses: List<Expense>) {
    LazyColumn(modifier) {
        items(expenses) { expense ->
            ExpenseItem(expense = expense)
        }
    }
}

@Composable
private fun ExpenseItem(expense: Expense) {
    Row {
        Text(
            modifier = Modifier.width(100.dp).padding(end = 8.dp),
            textAlign = TextAlign.Right,
            text = "%.2fzł".format(expense.amount),
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = Modifier,
            text = expense.description
        )
    }
}

@Composable
private fun Progress() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(progress = -1f)
    }
}

@Preview
@Composable
fun ProgressPreview() {
    Progress()
}