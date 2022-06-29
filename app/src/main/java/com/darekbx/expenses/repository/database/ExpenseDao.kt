package com.darekbx.expenses.repository.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expense")
    fun getAll(): LiveData<List<ExpenseDto>>

    @Insert
    fun insertAll(vararg expense: ExpenseDto)

    @Query("DELETE FROM expense WHERE uid = :uid")
    fun delete(uid: Int)
}