package com.darekbx.expenses.repository.database.dtos

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense")
data class ExpenseDto(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo(name = "payment_id") val paymentId: Int?,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "type") val type: Int,
)