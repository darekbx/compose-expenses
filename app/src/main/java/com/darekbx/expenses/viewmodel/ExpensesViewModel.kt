package com.darekbx.expenses.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.expenses.model.Expense
import com.darekbx.expenses.model.Expense.Companion.toDomain
import com.darekbx.expenses.repository.database.ExpenseDao
import com.darekbx.expenses.repository.database.ExpenseDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class UIState(
    val addDialogVisible: Boolean = false
)

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val expenseDao: ExpenseDao,
): ViewModel() {

    private val _state = mutableStateOf(UIState())
    val state: State<UIState> = _state

    sealed class UIEvent {
        class AddButtonClick : UIEvent()
        class CloseAddDialog : UIEvent()
        class CloseDialogAndSave(
            val amount: String,
            val description: String,
            val type: Expense.Type
        ) : UIEvent()
    }

    fun onEvent(event: UIEvent) {
        when (event) {
            is UIEvent.AddButtonClick ->
                _state.value = state.value.copy(
                    addDialogVisible = true
                )
            is UIEvent.CloseAddDialog ->
                _state.value = state.value.copy(
                    addDialogVisible = false
                )
            is UIEvent.CloseDialogAndSave -> {
                add(
                    Expense(
                        null,
                        event.amount.toDouble(),
                        event.description,
                        "",
                        event.type
                    )
                )
                _state.value = state.value.copy(
                    addDialogVisible = false
                )
            }

        }
    }

    fun listAll(): LiveData<List<Expense>> = Transformations.map(expenseDao.getAll()) { dtos ->
        dtos.map { it.toDomain() }
    }

    fun add(expense: Expense) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                with(expense) {
                    val dto = ExpenseDto(
                        null,
                        amount,
                        description,
                        System.currentTimeMillis(),
                        type.value
                    )
                    expenseDao.insertAll(dto)
                }
            }
        }
    }

    fun delete(expense: Expense) {
        val uid = expense.uid ?: return
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                expenseDao.delete(uid)
            }
        }
    }
}
