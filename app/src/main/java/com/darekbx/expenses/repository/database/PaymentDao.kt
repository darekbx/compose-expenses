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

    @Query("SELECT * FROM payment ORDER BY timestamp DESC LIMIT 1")
    fun getLastPayment(): LiveData<PaymentDto>

    @Query("SELECT * FROM payment ORDER BY timestamp DESC LIMIT 1")
    fun getLastPaymentAsync(): PaymentDto

    @Query("SELECT COUNT(uid) FROM payment")
    fun countPayments(): Int

    @Insert
    fun insert(paymentDto: PaymentDto): Long
}
