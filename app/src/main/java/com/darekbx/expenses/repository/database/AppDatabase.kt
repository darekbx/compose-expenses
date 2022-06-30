package com.darekbx.expenses.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.darekbx.expenses.repository.database.dtos.ExpenseDto
import com.darekbx.expenses.repository.database.dtos.PaymentDto

@Database(entities = [ExpenseDto::class, PaymentDto::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expensedao(): ExpenseDao

    abstract fun paymentdao(): PaymentDao

    companion object {
        val DB_NAME = "expenses"
    }
}