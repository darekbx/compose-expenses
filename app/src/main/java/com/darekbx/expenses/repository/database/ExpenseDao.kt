package com.darekbx.expenses.repository.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.expenses.repository.database.dtos.ExpenseDto

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expense where payment_id = :paymentId")
    fun getActiveExpenses(paymentId: Int): LiveData<List<ExpenseDto>>

    @Query("SELECT * FROM expense where payment_id = :paymentUid")
    fun getExpensesForPayment(paymentUid: Int): List<ExpenseDto>

    @Insert
    fun insert(expense: ExpenseDto)

    @Query("DELETE FROM expense WHERE uid = :uid")
    fun delete(uid: Int)
}