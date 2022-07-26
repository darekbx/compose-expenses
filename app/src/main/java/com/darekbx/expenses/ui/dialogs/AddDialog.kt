package com.darekbx.expenses.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.expenses.model.Expense
import com.darekbx.expenses.viewmodel.ExpensesViewModel

@Composable
fun AddDialog(
    expensesViewModel: ExpensesViewModel = hiltViewModel()
) {
    val amount = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val type = remember { mutableStateOf(Expense.Type.OTHERS) }

    AlertDialog(
        onDismissRequest = {
            expensesViewModel.onEvent(ExpensesViewModel.UIEvent.CloseAddDialog)
        },
        title = {
            Text(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                textAlign = TextAlign.Center,
                text = "New expense"
            )
        },
        text = { DialogContent(Modifier, amount, description, type) },
        confirmButton = {
            Button(
                onClick = {
                    expensesViewModel.onEvent(
                        ExpensesViewModel.UIEvent.CloseDialogAndSave(
                            amount.value,
                            description.value,
                            type.value
                        )
                    )
                }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    expensesViewModel.onEvent(ExpensesViewModel.UIEvent.CloseAddDialog)
                }) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DialogContent(
    modifier: Modifier = Modifier,
    amount: MutableState<String>,
    description: MutableState<String>,
    type: MutableState<Expense.Type>,
) {
    val options = Expense.Type.values()
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = amount.value,
            onValueChange = { amount.value = it },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )
        OutlinedTextField(
            value = description.value,
            onValueChange = { description.value = it },
            label = { Text("Description") },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            )
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = type.value.text,
                onValueChange = { },
                label = { Text("Type") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { selected ->
                    DropdownMenuItem(
                        onClick = {
                            type.value = selected
                            expanded = false
                        },
                        modifier = Modifier.background(selected.color)
                    ) {
                        Text(text = selected.text)
                    }
                }
            }
        }
    }
}