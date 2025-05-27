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
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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
                    tab.setText("Khác");
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
            layouts.add(R.layout.chart_placeholder); // Tab 2: Placeholder
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
            }
            // Tab 2: Placeholder không cần logic
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
            if (count > 0) { // Chỉ hiển thị các mức độ ưu tiên có công việc
                entries.add(new PieEntry(count, priority.getLabel()));
            }
        }

        if (entries.isEmpty()) {
            pieChart.setVisibility(View.GONE);
            return;
        }

        pieChart.setVisibility(View.VISIBLE);

        PieDataSet dataSet = new PieDataSet(entries, ""); // Nhãn rỗng để ẩn "Tasks by Priority"
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(16f); // Kích thước chữ 16sp
        dataSet.setValueTextColor(android.graphics.Color.WHITE);

        // Định dạng giá trị thành số nguyên
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
        pieChart.setDrawEntryLabels(false);

        // Căn giữa chú thích (legend)
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