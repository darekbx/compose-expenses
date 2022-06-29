package com.darekbx.expenses.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.expenses.model.Expense
import com.darekbx.expenses.model.Expense.Companion.toDomain
import com.darekbx.expenses.repository.database.ExpenseDto
import com.darekbx.expenses.viewmodel.ExpensesViewModel

@Composable
fun ExpensesList(
    expensesViewModel: ExpensesViewModel = hiltViewModel()
) {
    val expenses by expensesViewModel.listAll().observeAsState()
    expenses?.let {
        ExpensesList(Modifier.padding(8.dp), it) { expense ->
            expensesViewModel.delete(expense)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExpensesList(
    modifier: Modifier = Modifier,
    expenses: List<Expense>,
    onDelete: (Expense) -> Unit
) {
    LazyColumn(modifier) {
        items(expenses) { expense ->
            val dismissState = rememberDismissState()
            if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                onDelete(expense)
            }
            SwipeToDismiss(
                state = dismissState,
                modifier = Modifier.padding(vertical = Dp(1f)),
                directions = setOf(DismissDirection.EndToStart),
                dismissThresholds = { direction ->
                    FractionalThreshold(if (direction == DismissDirection.EndToStart) 1.1f else 0.05f)
                },
                background = {
                    val alignment = Alignment.CenterEnd
                    val icon = Icons.Default.Delete

                    val scale by animateFloatAsState(
                        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
                    )

                    Box(
                        Modifier.fillMaxSize().padding(horizontal = Dp(20f)),
                        contentAlignment = alignment
                    ) {
                        Icon(icon, contentDescription = "Delete Icon", modifier = Modifier.scale(scale))
                    }
                },
                dismissContent = {
                    ExpenseItem(expense = expense, dismissState = dismissState)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExpenseItem(
    modifier: Modifier = Modifier,
    expense: Expense,
    dismissState: DismissState
) {
    Card(
        modifier.padding(8.dp),
        elevation = animateDpAsState(
            if (dismissState.dismissDirection != null) 10.dp else 0.dp
        ).value,
        backgroundColor = expense.type.color
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "${expense.amount}zł",
                style = MaterialTheme.typography.h5
            )
            Column(
                modifier = Modifier.padding(end = 8.dp, top = 4.dp, bottom = 4.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = expense.timestamp,
                    style = MaterialTheme.typography.caption
                )
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = expense.description,
                    style = MaterialTheme.typography.subtitle1
                )
            }
        }
    }
}

@Preview
@Composable
fun ExpensesListPreview() {
    ExpensesList(expenses = listOf(
        ExpenseDto(0, 153.99, "Fuel", System.currentTimeMillis(), 2).toDomain(),
        ExpenseDto(0, 40.99, "Food", System.currentTimeMillis(), 1).toDomain(),
        ExpenseDto(0, 420.50, "Clothes", System.currentTimeMillis(), 3).toDomain(),
        ExpenseDto(0, 100.00, "Fuel", System.currentTimeMillis(), 4).toDomain(),
        ExpenseDto(0, 32.00, "Book", System.currentTimeMillis(), 5).toDomain(),
    )) { }
}
