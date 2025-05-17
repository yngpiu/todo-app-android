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

import com.haui.noteapp.databinding.FragmentStatisticBinding;
import com.haui.noteapp.model.StatisticData;
import com.haui.noteapp.util.Event;

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}