package com.darekbx.expenses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.expenses.ui.AddDialog
import com.darekbx.expenses.ui.ExpensesList
import com.darekbx.expenses.ui.theme.ExpensesTheme
import com.darekbx.expenses.viewmodel.ExpensesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpensesTheme {
                MainScreen()
            }
        }
    }

    @Composable
    private fun MainScreen(
        expensesViewModel: ExpensesViewModel = hiltViewModel()
    ) {
        Scaffold(
            backgroundColor = Color.White,
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    expensesViewModel.onEvent(ExpensesViewModel.UIEvent.AddButtonClick())
                }) {
                    Icon(Icons.Filled.Add, "add")
                }
            }
        ) {
            ExpensesList()

            val state by expensesViewModel.state
            if (state.addDialogVisible) {
                AddDialog()
            }
        }
    }
}
