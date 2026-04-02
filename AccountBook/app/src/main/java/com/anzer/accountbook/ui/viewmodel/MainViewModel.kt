package com.anzer.accountbook.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.anzer.accountbook.data.db.AppDatabase
import com.anzer.accountbook.data.model.MonthlySummary
import com.anzer.accountbook.data.model.Record
import com.anzer.accountbook.data.repository.RecordRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RecordRepository
    private val _currentYear = MutableLiveData<Int>()
    private val _currentMonth = MutableLiveData<Int>()
    private val _monthlySummary = MutableLiveData<MonthlySummary>()

    val currentYear: LiveData<Int> = _currentYear
    val currentMonth: LiveData<Int> = _currentMonth
    val monthlySummary: LiveData<MonthlySummary> = _monthlySummary
    val monthlyRecords: LiveData<List<Record>>

    init {
        val dao = AppDatabase.getDatabase(application).recordDao()
        repository = RecordRepository(dao)
        val cal = Calendar.getInstance()
        _currentYear.value = cal.get(Calendar.YEAR)
        _currentMonth.value = cal.get(Calendar.MONTH) + 1

        monthlyRecords = MediatorLiveData<List<Record>>().apply {
            fun update() {
                val year = _currentYear.value ?: return
                val month = _currentMonth.value ?: return
                addSource(repository.getRecordsByMonth(year, month)) { value = it }
            }
            addSource(_currentYear) { update() }
            addSource(_currentMonth) { update() }
        }
        loadMonthlySummary()
    }

    fun loadMonthlySummary() {
        viewModelScope.launch {
            val year = _currentYear.value ?: return@launch
            val month = _currentMonth.value ?: return@launch
            _monthlySummary.value = repository.getMonthlySummary(year, month)
        }
    }

    fun previousMonth() {
        val year = _currentYear.value ?: return
        val month = _currentMonth.value ?: return
        if (month == 1) { _currentYear.value = year - 1; _currentMonth.value = 12 }
        else { _currentMonth.value = month - 1 }
        loadMonthlySummary()
    }

    fun nextMonth() {
        val year = _currentYear.value ?: return
        val month = _currentMonth.value ?: return
        if (month == 12) { _currentYear.value = year + 1; _currentMonth.value = 1 }
        else { _currentMonth.value = month + 1 }
        loadMonthlySummary()
    }

    fun deleteRecord(record: Record) {
        viewModelScope.launch { repository.delete(record); loadMonthlySummary() }
    }
}
