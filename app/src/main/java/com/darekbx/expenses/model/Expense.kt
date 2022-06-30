package com.darekbx.expenses.model

import androidx.compose.ui.graphics.Color
import com.darekbx.expenses.repository.database.dtos.ExpenseDto
import java.text.SimpleDateFormat

data class Expense(
    val uid: Int?,
    val amount: Double,
    val description: String,
    val timestamp: String,
    val type: Type,
) {
    enum class Type(val value: Int, val color: Color, val text: String) {
        GROCERY(1, Color(0xFF90CAF9), "Grocery"),
        FOOD(2, Color(0xFFA5D6A7), "Food"),
        CAR(3, Color(0xFFFFAB91), "Car"),
        CLOTHES(4, Color(0xFFFFCC80), "Clothes"),
        ENTERTAIMENT(5, Color(0xFFCE93D8), "Entertaiment"),
        SCHOOL(6, Color(0xFFE2F1F8), "School"),
        HOUSE(7, Color(0xFFEFDCD5), "House"),
        OTHERS(10, Color.White, "Other")
    }

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        fun ExpenseDto.toDomain(): Expense {
            return Expense(
                uid,
                amount,
                description,
                dateFormat.format(timestamp),
                Type.values().first { it.value == type })
        }
    }
}
