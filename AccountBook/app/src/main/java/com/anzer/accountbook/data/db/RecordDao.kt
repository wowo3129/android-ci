package com.anzer.accountbook.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.anzer.accountbook.data.model.Record

@Dao
interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: Record): Long

    @Update
    suspend fun update(record: Record)

    @Delete
    suspend fun delete(record: Record)

    @Query("SELECT * FROM records ORDER BY date DESC")
    fun getAllRecords(): LiveData<List<Record>>

    @Query("SELECT * FROM records WHERE date BETWEEN :startTime AND :endTime ORDER BY date DESC")
    fun getRecordsByMonth(startTime: Long, endTime: Long): LiveData<List<Record>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM records WHERE type = 1 AND date BETWEEN :startTime AND :endTime")
    suspend fun getMonthlyIncome(startTime: Long, endTime: Long): Double

    @Query("SELECT COALESCE(SUM(amount), 0) FROM records WHERE type = 0 AND date BETWEEN :startTime AND :endTime")
    suspend fun getMonthlyExpense(startTime: Long, endTime: Long): Double
}
