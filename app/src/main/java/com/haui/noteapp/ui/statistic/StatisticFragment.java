package com.haui.noteapp.ui.statistic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.haui.noteapp.databinding.FragmentStatisticBinding;
import com.haui.noteapp.model.Priority;
import com.haui.noteapp.model.StatisticData;
import com.haui.noteapp.util.Event;

import java.util.ArrayList;
import java.util.Map;

public class StatisticFragment extends Fragment {

    private FragmentStatisticBinding binding;
    private StatisticViewModel statisticViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticViewModel = new ViewModelProvider(this).get(StatisticViewModel.class);

        binding = FragmentStatisticBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Bind UI elements
        TextView tvTotalTasksValue = binding.tvTotalTasksValue;
        TextView tvCompletionRateValue = binding.tvCompletionRateValue;
        TextView tvCompletedValue = binding.tvCompletedValue;
        TextView tvPendingValue = binding.tvPendingValue;
        PieChart pieChart = binding.pieChart;

        // Observe statistic data
        statisticViewModel.getStatisticData().observe(getViewLifecycleOwner(), statistic -> {
            if (statistic != null) {
                tvTotalTasksValue.setText(String.valueOf(statistic.getTotal()));
                tvCompletionRateValue.setText(statistic.getCompletionRate() + "%");
                tvCompletedValue.setText(String.valueOf(statistic.getCompleted()));
                tvPendingValue.setText(String.valueOf(statistic.getPending()));
            }
        });

        // Observe priority stats and update PieChart
        statisticViewModel.getPriorityStats().observe(getViewLifecycleOwner(), priorityStats -> {
            if (priorityStats != null) {
                updatePieChart(pieChart, priorityStats);
            }
        });

        // Observe error messages
        statisticViewModel.getErrorMessage().observe(getViewLifecycleOwner(), event -> {
            String msg = event.getContentIfNotHandled();
            if (msg != null) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    private void updatePieChart(PieChart pieChart, Map<String, Integer> priorityStats) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Priority priority : Priority.values()) {
            int count = priorityStats.getOrDefault(priority.name(), 0);
            if (count > 0) { // Chỉ hiển thị các mức độ ưu tiên có công việc
                entries.add(new PieEntry(count, priority.getLabel()));
            }
        }

        if (entries.isEmpty()) {
            pieChart.setVisibility(View.GONE);
            return;
        }

        pieChart.setVisibility(View.VISIBLE);

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(android.graphics.Color.WHITE);
        dataSet.setValueTextSize(18f);

        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f", value); // Hiển thị số nguyên
            }
        });

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        // Customize PieChart
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterTextSize(14f);
        pieChart.setDrawEntryLabels(false);

        Legend legend = pieChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setWordWrapEnabled(true);

        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);

        pieChart.invalidate(); // Refresh chart
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}