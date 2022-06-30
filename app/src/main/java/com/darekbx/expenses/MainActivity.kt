/**
 * TODO:
 *  - Display statistics for every "payment" entry
 */
package com.darekbx.expenses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darekbx.expenses.navigation.BottomAppBar
import com.darekbx.expenses.navigation.NavigationItem
import com.darekbx.expenses.ui.*
import com.darekbx.expenses.ui.theme.ExpensesTheme
import com.darekbx.expenses.viewmodel.ExpensesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpensesTheme {
                val navController = rememberNavController()
                Scaffold(
                    backgroundColor = Color.White,
                    bottomBar = { BottomAppBar(navController) }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Navigation(navController)
                    }
                }
            }
        }
    }

    @Composable
    private fun MainScreen(expensesViewModel: ExpensesViewModel = hiltViewModel()) {
        ExpensesList()
        val state by expensesViewModel.state
        if (state.addDialogVisible) {
            AddDialog()
        }
        if (state.paymentConfirmDialogVisible) {
            PaymentConfirmDialog()
        }
    }

    @Composable
    fun Navigation(navController: NavHostController) {
        NavHost(navController, startDestination = NavigationItem.Home.route) {
            composable(NavigationItem.Home.route) {
                MainScreen()
            }
            composable(NavigationItem.Payments.route) {
                PaymentsScreen()
            }
            composable(NavigationItem.Statistics.route) {
                StatisticsScreen()
            }
        }
    }
}
