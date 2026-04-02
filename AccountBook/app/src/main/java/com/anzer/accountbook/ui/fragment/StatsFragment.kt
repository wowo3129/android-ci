package com.anzer.accountbook.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.anzer.accountbook.databinding.FragmentStatsBinding
import com.anzer.accountbook.ui.viewmodel.MainViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.DecimalFormat

class StatsFragment : Fragment() {
    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChart()
        setupObservers()
    }

    private fun setupChart() {
        binding.pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 40f
            legend.isEnabled = true
        }
    }

    private fun setupObservers() {
        val df = DecimalFormat("#,##0.00")
        viewModel.monthlySummary.observe(viewLifecycleOwner) { summary ->
            binding.tvStatsIncome.text = "Income: $${df.format(summary.income)}"
            binding.tvStatsExpense.text = "Expense: $${df.format(summary.expense)}"
            binding.tvStatsBalance.text = "Balance: $${df.format(summary.balance)}"
        }
        viewModel.monthlyRecords.observe(viewLifecycleOwner) { records ->
            val expenseByCategory = records.filter { it.type == 0 }.groupBy { it.category }.mapValues { it.value.sumOf { r -> r.amount } }
            if (expenseByCategory.isEmpty()) {
                binding.pieChart.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE
                return@observe
            }
            binding.pieChart.visibility = View.VISIBLE
            binding.tvNoData.visibility = View.GONE
            val entries = expenseByCategory.map { (category, amount) -> com.github.mikephil.charting.data.PieEntry(amount.toFloat(), category) }
            val dataSet = PieDataSet(entries, "Expense").apply { colors = ColorTemplate.MATERIAL_COLORS.toList() }
            binding.pieChart.data = PieData(dataSet)
            binding.pieChart.invalidate()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
