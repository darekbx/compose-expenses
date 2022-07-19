package com.darekbx.expenses.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.expenses.R
import com.darekbx.expenses.model.Expense
import com.darekbx.expenses.model.StatisticSum
import com.darekbx.expenses.viewmodel.ExpensesViewModel

@Composable
fun ExpensesList(
    expensesViewModel: ExpensesViewModel = hiltViewModel()
) {
    Box(Modifier.fillMaxSize()) {
        val statisticSums by expensesViewModel.listActiveExpenses().observeAsState()
        statisticSums?.let {
            ExpensesList(Modifier.padding(8.dp), it) {
                // onItemClick
                expensesViewModel.onEvent(ExpensesViewModel.UIEvent.OpenActualExpensesDialog(it))
            }
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            FloatingActionButton(
                modifier = Modifier.padding(0.dp),
                onClick = {
                    expensesViewModel.onEvent(ExpensesViewModel.UIEvent.MakePaymentButtonClick)
                }) {
                Box {
                    Icon(painterResource(id = R.drawable.ic_money_sign), "make_payment")
                    Icon(
                        Icons.Filled.Add,
                        "make_payment",
                        modifier = Modifier
                            .size(18.dp, 18.dp)
                            .absoluteOffset(x = (-7).dp, y = (-7).dp)
                    )
                }
            }
            FloatingActionButton(
                modifier = Modifier
                    .padding(top = 16.dp),
                onClick = {
                    expensesViewModel.onEvent(ExpensesViewModel.UIEvent.AddButtonClick)
                }) {
                Icon(Icons.Filled.Add, "add")
            }
        }
    }
}

@Composable
fun ExpensesList(
    modifier: Modifier = Modifier,
    statisticSums: List<StatisticSum>,
    onItemClick: (Expense.Type) -> (Unit) = { }
) {
    LazyColumn(modifier) {
        items(statisticSums) { statisticSum ->
            ExpenseSumItem(statisticSum = statisticSum) {
                onItemClick(it)
            }
        }
    }
}

@Composable
fun ExpenseSumItem(
    modifier: Modifier = Modifier,
    statisticSum: StatisticSum,
    onItemClick: (Expense.Type) -> (Unit)
) {
    Card(
        modifier.padding(8.dp).clickable { onItemClick(statisticSum.type) },
        elevation = 4.dp,
        backgroundColor = statisticSum.type.color
    ) {
        Row(
            Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "%.2fzł".format(statisticSum.sum),
                style = MaterialTheme.typography.h5
            )
            Text(
                modifier = Modifier.padding(end = 8.dp, top = 4.dp, bottom = 4.dp),
                text = "${statisticSum.type.text} (${statisticSum.count})",
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}

@Preview
@Composable
fun ExpensesListPreview() {
    ExpensesList(statisticSums = listOf(
        StatisticSum(153.99, 15, Expense.Type.OTHERS),
        StatisticSum(40.99, 2, Expense.Type.CLOTHES),
        StatisticSum(420.50, 20, Expense.Type.FOOD),
        StatisticSum(100.00, 1, Expense.Type.SCHOOL),
        StatisticSum(32.00, 3, Expense.Type.GROCERY),
    ))
}
