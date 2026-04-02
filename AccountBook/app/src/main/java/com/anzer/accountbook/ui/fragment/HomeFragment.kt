package com.anzer.accountbook.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.anzer.accountbook.databinding.FragmentHomeBinding
import com.anzer.accountbook.ui.AddRecordActivity
import com.anzer.accountbook.ui.adapter.RecordAdapter
import com.anzer.accountbook.ui.viewmodel.MainViewModel
import java.text.DecimalFormat

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: RecordAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = RecordAdapter { record -> viewModel.deleteRecord(record) }
        binding.rvRecords.adapter = adapter
    }

    private fun setupObservers() {
        val df = DecimalFormat("#,##0.00")
        viewModel.currentYear.observe(viewLifecycleOwner) { updateMonthTitle() }
        viewModel.currentMonth.observe(viewLifecycleOwner) { updateMonthTitle() }
        viewModel.monthlySummary.observe(viewLifecycleOwner) { summary ->
            binding.tvIncome.text = "$${df.format(summary.income)}"
            binding.tvExpense.text = "$${df.format(summary.expense)}"
            binding.tvBalance.text = "$${df.format(summary.balance)}"
        }
        viewModel.monthlyRecords.observe(viewLifecycleOwner) { records ->
            adapter.submitList(records)
            binding.tvEmpty.visibility = if (records.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun updateMonthTitle() {
        val year = viewModel.currentYear.value ?: return
        val month = viewModel.currentMonth.value ?: return
        binding.tvMonthTitle.text = "$year-$month"
    }

    private fun setupClickListeners() {
        binding.btnPrevMonth.setOnClickListener { viewModel.previousMonth() }
        binding.btnNextMonth.setOnClickListener { viewModel.nextMonth() }
        binding.fabAdd.setOnClickListener { startActivity(Intent(requireContext(), AddRecordActivity::class.java)) }
    }

    override fun onResume() { super.onResume(); viewModel.loadMonthlySummary() }
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
