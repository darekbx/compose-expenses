package com.darekbx.expenses.navigation

import com.darekbx.expenses.R

sealed class NavigationItem(
    var route: String,
    var labelResId: Int,
    val iconResId: Int
) {
    object Home: NavigationItem("home", R.string.home, R.drawable.ic_home)
    object Statistics: NavigationItem("statistics", R.string.statistics, R.drawable.ic_chart)
    object Payments: NavigationItem("payments", R.string.payments, R.drawable.ic_money)
}