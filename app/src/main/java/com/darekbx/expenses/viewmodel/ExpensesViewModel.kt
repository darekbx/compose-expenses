package com.darekbx.expenses.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.darekbx.expenses.model.Expense
import com.darekbx.expenses.model.Expense.Companion.toDomain
import com.darekbx.expenses.model.Payment
import com.darekbx.expenses.model.Payment.Companion.toDomain
import com.darekbx.expenses.repository.database.ExpenseDao
import com.darekbx.expenses.repository.database.dtos.ExpenseDto
import com.darekbx.expenses.repository.database.PaymentDao
import com.darekbx.expenses.repository.database.dtos.PaymentDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class UIState(
    val addDialogVisible: Boolean = false,
    val paymentConfirmDialogVisible: Boolean = false
)

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val paymentDao: PaymentDao,
): ViewModel() {

    private val _state = mutableStateOf(UIState())
    val state: State<UIState> = _state

    sealed class UIEvent {

        object AddButtonClick : UIEvent()
        object MakePaymentButtonClick : UIEvent()
        object CloseAddDialog : UIEvent()
        object CloseConfirmPaymentDialog : UIEvent()

        class CloseConfirmPaymentDialogAndSave(val amount: String) : UIEvent()
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
            is UIEvent.MakePaymentButtonClick ->
                _state.value = state.value.copy(
                    paymentConfirmDialogVisible = true
                )
            is UIEvent.CloseAddDialog ->
                _state.value = state.value.copy(
                    addDialogVisible = false
                )
            is UIEvent.CloseConfirmPaymentDialog ->
                _state.value = state.value.copy(
                    paymentConfirmDialogVisible = false
                )
            is UIEvent.CloseConfirmPaymentDialogAndSave -> {
                makePayment(event.amount.toDouble())
                _state.value = state.value.copy(
                    paymentConfirmDialogVisible = false
                )
            }
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

    fun loadAllPayments(): LiveData<List<Payment>> = liveData {
        withContext(Dispatchers.IO) {
            val payments = paymentDao
                .getAll()
                .map { paymentDto ->
                    val expenses = expenseDao.getExpensesForPayment(paymentDto.uid!!)
                    paymentDto.toDomain()
                        .also {
                            it.expenses = expenses.map { dto -> dto.toDomain() }
                        }
                }
            emit(payments)
        }
    }

    fun listActiveExpenses(): LiveData<List<Expense>> =
        Transformations.map(expenseDao.getActiveExpenses()) { dtos ->
            dtos.map { it.toDomain() }
        }

    private fun add(expense: Expense) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                with(expense) {
                    val dto = ExpenseDto(
                        null,
                        null,
                        amount,
                        description,
                        System.currentTimeMillis(),
                        type.value
                    )
                    expenseDao.insert(dto)
                }
            }
        }
    }

    private fun makePayment(amount: Double) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val dto = PaymentDto(
                    null,
                    amount,
                    System.currentTimeMillis()
                )
                val paymentUid = paymentDao.insert(dto)
                expenseDao.updateExpenses(paymentUid)
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
