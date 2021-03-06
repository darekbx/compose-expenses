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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class UIState(
    val addDialogVisible: Boolean = false,
    val noPaymentDialog: Boolean = false,
    val paymentConfirmDialogVisible: Boolean = false,
    val actualExpesnesDialogVisible: Boolean = false,
    val statisticsLoaded: Boolean = false,
    val expensesType: Expense.Type = Expense.Type.OTHERS
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
        object CloseActualExpensesDialog : UIEvent()
        object CloseNoPaymentDialog : UIEvent()
        object StatisticsLoaded : UIEvent()

        class OpenActualExpensesDialog(val expensesType: Expense.Type) : UIEvent()
        class CloseConfirmPaymentDialogAndSave(val amount: String) : UIEvent()
        class CloseDialogAndSave(
            val amount: String,
            val description: String,
            val type: Expense.Type
        ) : UIEvent()
    }

    fun onEvent(event: UIEvent) {
        when (event) {
            is UIEvent.AddButtonClick -> {
                checkPayment { hasPayment ->
                    if (hasPayment) {
                        _state.value = state.value.copy(
                            addDialogVisible = true
                        )
                    } else {
                        _state.value = state.value.copy(
                            noPaymentDialog = true
                        )
                    }
                }
            }
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
            is UIEvent.StatisticsLoaded ->
                _state.value = state.value.copy(
                    statisticsLoaded = true
                )
            is UIEvent.OpenActualExpensesDialog -> {
                _state.value = state.value.copy(
                    actualExpesnesDialogVisible = true,
                    expensesType = event.expensesType
                )
            }
            is UIEvent.CloseActualExpensesDialog ->
                _state.value = state.value.copy(
                    actualExpesnesDialogVisible = false
                )
            is UIEvent.CloseNoPaymentDialog ->
                _state.value = state.value.copy(
                    noPaymentDialog = false
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

                delay(50L)
                onEvent(UIEvent.StatisticsLoaded)
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

    fun listActiveExpenses(type: Expense.Type): LiveData<List<Expense>> =
        Transformations.switchMap(paymentDao.getLastPayment()) { paymentDto ->
            if (paymentDto == null) {
                return@switchMap MutableLiveData<List<Expense>>()
            }
            Transformations.map(expenseDao.getActiveExpenses(paymentDto.uid!!, type.value)) {
                it.map { dto -> dto.toDomain() }
            }
        }

    private fun checkPayment(result: (Boolean) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val count = paymentDao.countPayments()
                withContext(Dispatchers.Main) {
                    result(count > 0)
                }
            }
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
