package com.darekbx.expenses.model

import com.darekbx.expenses.repository.database.dtos.PaymentDto
import java.text.SimpleDateFormat

data class Payment(
    val uid: Int?,
    val amount: Double,
    val timestamp: String
) {
    var expenses: List<Expense> = emptyList()

    fun summaryExpenses() = expenses.sumOf { it.amount }

    fun expensesCount() = expenses.size

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        fun PaymentDto.toDomain(): Payment {
            return Payment(
                uid,
                amount,
                dateFormat.format(timestamp)
            )
        }
    }
}
