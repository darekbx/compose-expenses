package com.darekbx.expenses.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.darekbx.expenses.model.Expense
import com.darekbx.expenses.model.Expense.Companion.toDomain
import com.darekbx.expenses.model.Payment
import com.darekbx.expenses.model.Payment.Companion.toDomain
import com.darekbx.expenses.model.StatisticSum
import com.darekbx.expenses.model.StatisticValue
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

    fun loadStatistics(): LiveData<List<StatisticValue>> = liveData {
        withContext(Dispatchers.IO) {
            paymentDao.getLastPaymentAsync().let { payment ->
                val expenses = expenseDao.getExpensesForPayment(payment.uid!!)
                val overallSum = expenses.sumOf { it.amount }
                val values = expenses
                    .groupBy { it.type }
                    .mapValues {
                        it.value.sumOf { expense -> expense.amount } / overallSum * 100
                    }
                    .map { pair ->
                        StatisticValue(
                            pair.value,
                            Expense.Type.values().first { it.value == pair.key }
                        )
                    }
                    .sortedByDescending { it.percent }
                emit(values)
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

    fun listActiveExpenses(): LiveData<List<StatisticSum>> =
        Transformations.switchMap(paymentDao.getLastPayment()) { paymentDto ->
            if (paymentDto == null) {
                return@switchMap MutableLiveData<List<StatisticSum>>()
            }
            Transformations.map(expenseDao.getActiveExpenses(paymentDto.uid!!)) {
                it
                    .map { dto -> dto.toDomain() }
                    .groupBy { expense -> expense.type }
                    .map { entry ->
                        val sum = entry.value.sumOf { expense -> expense.amount }
                        val count = entry.value.size
                        StatisticSum(sum, count, entry.key)
                    }
                    .sortedByDescending { statisticSum -> statisticSum.sum }
            }
        }

    private fun add(expense: Expense) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val lastPayment = paymentDao.getLastPaymentAsync()
                with(expense) {
                    val dto = ExpenseDto(
                        null,
                        lastPayment.uid,
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
                paymentDao.insert(dto)
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
