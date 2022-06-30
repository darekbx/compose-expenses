package com.darekbx.expenses.navigation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomAppBar(navController: NavController) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Statistics,
        NavigationItem.Payments
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Column {
        Separator()
        androidx.compose.material.BottomAppBar {
            BottomNavigation(backgroundColor = Color.Black) {
                items.forEach { item ->
                    BottomNavigationItem(item, currentRoute, navController)
                }
            }
        }
    }
}

@Composable
private fun RowScope.BottomNavigationItem(
    item: NavigationItem,
    currentRoute: String?,
    navController: NavController
) {
    BottomNavigationItem(
        icon = { Icon(painterResource(id = item.iconResId), contentDescription = item.route) },
        label = { Text(text = stringResource(item.labelResId), fontSize = 8.sp) },
        alwaysShowLabel = false,
        selected = currentRoute == item.route,
        onClick = {
            navController.navigate(item.route) {
                navController.graph.startDestinationRoute?.let { route ->
                    popUpTo(route) {
                        saveState = true
                    }
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}

@Composable
private fun Separator() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
    ) {
        drawLine(Color.DarkGray, Offset(0F, 0F), Offset(size.width, 0F), strokeWidth = 2F)
    }
}