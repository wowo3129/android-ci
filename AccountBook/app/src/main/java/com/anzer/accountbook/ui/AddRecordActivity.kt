package com.anzer.accountbook.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.anzer.accountbook.R
import com.anzer.accountbook.databinding.ActivityAddRecordBinding
import com.anzer.accountbook.ui.viewmodel.AddRecordViewModel
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.*

class AddRecordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddRecordBinding
    private val viewModel: AddRecordViewModel by viewModels()
    private var selectedDate = System.currentTimeMillis()
    private var recordType = 0

    private val expenseCategories = listOf("Catering", "Transport", "Shopping", "Entertainment", "Medical", "Housing", "Education", "Other")
    private val incomeCategories = listOf("Salary", "Bonus", "Part-time", "Investment", "RedPacket", "Other")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupTypeToggle()
        setupDatePicker()
        setupSaveButton()
        updateCategories()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupTypeToggle() {
        binding.toggleType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) { recordType = if (checkedId == R.id.btnIncome) 1 else 0; updateCategories() }
        }
        binding.toggleType.check(R.id.btnExpense)
    }

    private fun setupDatePicker() {
        updateDateDisplay()
        binding.tvDate.setOnClickListener {
            val cal = Calendar.getInstance().apply { timeInMillis = selectedDate }
            DatePickerDialog(this, { _, year, month, day ->
                cal.set(year, month, day); selectedDate = cal.timeInMillis; updateDateDisplay()
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun updateDateDisplay() {
        binding.tvDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date(selectedDate))
    }

    private fun updateCategories() {
        binding.chipGroupCategory.removeAllViews()
        val categories = if (recordType == 0) expenseCategories else incomeCategories
        categories.forEach { category ->
            val chip = Chip(this).apply { text = category; isCheckable = true }
            binding.chipGroupCategory.addView(chip)
        }
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            val amount = binding.etAmount.text.toString().toDoubleOrNull() ?: 0.0
            val note = binding.etNote.text.toString()
            val selectedChip = binding.chipGroupCategory.findViewById<Chip>(binding.chipGroupCategory.checkedChipId)
            val category = selectedChip?.text?.toString() ?: ""
            viewModel.saveRecord(recordType, amount, category, note, selectedDate,
                { Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show(); finish() },
                { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() })
        }
    }
}
