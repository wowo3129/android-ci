package com.anzer.accountbook.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.anzer.accountbook.data.db.AppDatabase
import com.anzer.accountbook.data.model.Record
import com.anzer.accountbook.data.repository.RecordRepository
import kotlinx.coroutines.launch

class AddRecordViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RecordRepository

    init {
        val dao = AppDatabase.getDatabase(application).recordDao()
        repository = RecordRepository(dao)
    }

    fun saveRecord(type: Int, amount: Double, category: String, note: String, date: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (amount <= 0) { onError("Amount must > 0"); return }
        if (category.isEmpty()) { onError("Select category"); return }
        viewModelScope.launch {
            try {
                repository.insert(Record(type = type, amount = amount, category = category, note = note, date = date))
                onSuccess()
            } catch (e: Exception) { onError("Save failed: ${e.message}") }
        }
    }
}
