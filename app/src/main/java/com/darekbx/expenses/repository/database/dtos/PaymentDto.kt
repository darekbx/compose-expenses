package com.darekbx.expenses.repository.database.dtos

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment")
data class PaymentDto(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
)