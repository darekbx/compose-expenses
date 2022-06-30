package com.darekbx.expenses.repository.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.expenses.repository.database.dtos.PaymentDto

@Dao
interface PaymentDao {

    @Query("SELECT * FROM payment")
    fun getAll(): List<PaymentDto>

    @Insert
    fun insert(paymentDto: PaymentDto): Long
}