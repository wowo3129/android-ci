package com.anzer.accountbook.data.repository

import androidx.lifecycle.LiveData
import com.anzer.accountbook.data.db.RecordDao
import com.anzer.accountbook.data.model.MonthlySummary
import com.anzer.accountbook.data.model.Record
import java.util.Calendar

class RecordRepository(private val dao: RecordDao) {
    fun getAllRecords(): LiveData<List<Record>> = dao.getAllRecords()

    fun getRecordsByMonth(year: Int, month: Int): LiveData<List<Record>> {
        val (start, end) = getMonthRange(year, month)
        return dao.getRecordsByMonth(start, end)
    }

    suspend fun insert(record: Record): Long = dao.insert(record)
    suspend fun delete(record: Record) = dao.delete(record)

    suspend fun getMonthlySummary(year: Int, month: Int): MonthlySummary {
        val (start, end) = getMonthRange(year, month)
        val income = dao.getMonthlyIncome(start, end)
        val expense = dao.getMonthlyExpense(start, end)
        return MonthlySummary(income, expense)
    }

    private fun getMonthRange(year: Int, month: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        return Pair(start, cal.timeInMillis)
    }
}
