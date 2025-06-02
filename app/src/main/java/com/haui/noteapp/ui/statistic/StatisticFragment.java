package com.haui.noteapp.ui.statistic;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.haui.noteapp.R;
import com.haui.noteapp.databinding.FragmentStatisticBinding;
import com.haui.noteapp.model.Priority;
import com.haui.noteapp.model.StatisticData;
import com.haui.noteapp.util.Event;

import java.util.ArrayList;
import java.util.List;
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

        // Setup TabLayout and ViewPager2
        TabLayout tabLayout = binding.tabLayout;
        ViewPager2 viewPager = binding.viewPager;
        ViewPagerAdapter adapter = new ViewPagerAdapter(inflater, this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Theo ưu tiên");
                    break;
                case 1:
                    tab.setText("Theo danh mục");
                    break;
            }
        }).attach();

        // Observe statistic data
        statisticViewModel.getStatisticData().observe(getViewLifecycleOwner(), statistic -> {
            if (statistic != null) {
                tvTotalTasksValue.setText(String.valueOf(statistic.getTotal()));
                tvCompletionRateValue.setText(statistic.getCompletionRate() + "%");
                tvCompletedValue.setText(String.valueOf(statistic.getCompleted()));
                tvPendingValue.setText(String.valueOf(statistic.getPending()));
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

    private class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
        private final LayoutInflater inflater;
        private final List<Integer> layouts;
        private final StatisticFragment fragment;

        ViewPagerAdapter(LayoutInflater inflater, StatisticFragment fragment) {
            this.inflater = inflater;
            this.fragment = fragment;
            this.layouts = new ArrayList<>();
            layouts.add(R.layout.chart_priority); // Tab 1: Priority Chart
            layouts.add(R.layout.chart_category); // Tab 2: Category Bar Chart
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = inflater.inflate(viewType, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (position == 0) {
                // Tab 1: Priority Chart
                PieChart pieChart = holder.itemView.findViewById(R.id.pie_chart);
                statisticViewModel.getPriorityStats().observe(fragment.getViewLifecycleOwner(), priorityStats -> {
                    if (priorityStats != null) {
                        fragment.updatePieChart(pieChart, priorityStats);
                    }
                });
            } else if (position == 1) {
                // Tab 2: Category Bar Chart
                BarChart barChart = holder.itemView.findViewById(R.id.bar_chart);
                statisticViewModel.getCategoryStats().observe(fragment.getViewLifecycleOwner(), categoryStats -> {
                    if (categoryStats != null) {
                        fragment.updateBarChart(barChart, categoryStats);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return layouts.size();
        }

        @Override
        public int getItemViewType(int position) {
            return layouts.get(position);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    private void updatePieChart(PieChart pieChart, Map<String, Integer> priorityStats) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Priority priority : Priority.values()) {
            int count = priorityStats.getOrDefault(priority.name(), 0);
            if (count > 0) {
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
        dataSet.setValueTextSize(16f);
        dataSet.setValueTextColor(android.graphics.Color.WHITE);

        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f", value);
            }
        });

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);

        Legend legend = pieChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setWordWrapEnabled(true);

        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);

        pieChart.invalidate();
    }


private void updateBarChart(BarChart barChart, Map<String, Map<String, Integer>> categoryStats) {
    if (categoryStats.isEmpty()) {
        barChart.setVisibility(View.GONE);
        return;
    }

    barChart.setVisibility(View.VISIBLE);

    List<String> categoryNames = new ArrayList<>(categoryStats.keySet());
    List<BarEntry> completedEntries = new ArrayList<>();
    List<BarEntry> pendingEntries = new ArrayList<>();

    for (int i = 0; i < categoryNames.size(); i++) {
        String categoryName = categoryNames.get(i);
        Map<String, Integer> stats = categoryStats.get(categoryName);
        int completed = stats.getOrDefault("completed", 0);
        int pending = stats.getOrDefault("pending", 0);

        completedEntries.add(new BarEntry(i, completed));
        pendingEntries.add(new BarEntry(i, pending));
    }

    BarDataSet completedDataSet = new BarDataSet(completedEntries, "Hoàn thành");
    completedDataSet.setColor(Color.parseColor("#4CAF50"));
    completedDataSet.setValueTextSize(14f);
    completedDataSet.setValueTextColor(Color.BLACK);

    BarDataSet pendingDataSet = new BarDataSet(pendingEntries, "Chưa hoàn thành");
    pendingDataSet.setColor(Color.parseColor("#FF9800"));
    pendingDataSet.setValueTextSize(14f);
    pendingDataSet.setValueTextColor(Color.BLACK);

    ValueFormatter valueFormatter = new ValueFormatter() {
        @Override
        public String getFormattedValue(float value) {
            return String.format("%.0f", value);
        }
    };
    completedDataSet.setValueFormatter(valueFormatter);
    pendingDataSet.setValueFormatter(valueFormatter);

    BarData barData = new BarData(completedDataSet, pendingDataSet);

    // 💡 Cấu hình group
    float groupSpace = 0.3f;
    float barSpace = 0.05f;
    float barWidth = 0.3f;
    int groupCount = categoryNames.size();

    barData.setBarWidth(barWidth);
    barChart.setData(barData);

    // ⚙️ Cấu hình trục X
    XAxis xAxis = barChart.getXAxis();
    xAxis.setGranularity(1f);
    xAxis.setCenterAxisLabels(true);
    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    xAxis.setValueFormatter(new IndexAxisValueFormatter(categoryNames));
    xAxis.setLabelRotationAngle(45f);
    xAxis.setLabelCount(categoryNames.size());

    xAxis.setAxisMinimum(0f);
    xAxis.setAxisMaximum(0f + barData.getGroupWidth(groupSpace, barSpace) * groupCount);

    // Trục Y
    barChart.getAxisLeft().setAxisMinimum(0f);
    barChart.getAxisRight().setEnabled(false);

    // Nhóm bar lại
    barChart.groupBars(0f, groupSpace, barSpace);

    // Hiệu ứng & tùy chỉnh khác
    barChart.setExtraOffsets(10f, 10f, 10f, 16f);
    barChart.setDrawGridBackground(false);
    barChart.getDescription().setEnabled(false);

    Legend legend = barChart.getLegend();
    legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
    legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
    legend.setWordWrapEnabled(true);

    barChart.animateY(1000);
    barChart.invalidate();
}



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}