package com.darekbx.expenses.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ExpenseDto::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expensedao(): ExpenseDao

    companion object {
        val DB_NAME = "expenses"
    }
}