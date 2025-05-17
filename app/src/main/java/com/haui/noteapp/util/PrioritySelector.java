package com.haui.noteapp.util;

import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.haui.noteapp.R;

public class PrioritySelector {

    private final MaterialCardView cardHigh, cardMedium, cardLow;
    private final TextView textHigh, textMedium, textLow;
    private String selectedPriority = null;

    public PrioritySelector(View rootView) {
        cardHigh = rootView.findViewById(R.id.card_priority_high);
        cardMedium = rootView.findViewById(R.id.card_priority_medium);
        cardLow = rootView.findViewById(R.id.card_priority_low);

        textHigh = (TextView) cardHigh.getChildAt(0);
        textMedium = (TextView) cardMedium.getChildAt(0);
        textLow = (TextView) cardLow.getChildAt(0);

        View.OnClickListener clickListener = v -> {
            if (v == cardHigh) selectedPriority = "Cao";
            else if (v == cardMedium) selectedPriority = "Trung bình";
            else selectedPriority = "Thấp";

            setPriorityUI();
        };

        cardHigh.setOnClickListener(clickListener);
        cardMedium.setOnClickListener(clickListener);
        cardLow.setOnClickListener(clickListener);
    }

    private void setPriorityUI() {
        cardHigh.setChecked(false);
        cardMedium.setChecked(false);
        cardLow.setChecked(false);

        cardHigh.setBackgroundTintList(ContextCompat.getColorStateList(cardHigh.getContext(), android.R.color.transparent));
        cardMedium.setBackgroundTintList(ContextCompat.getColorStateList(cardMedium.getContext(), android.R.color.transparent));
        cardLow.setBackgroundTintList(ContextCompat.getColorStateList(cardLow.getContext(), android.R.color.transparent));

        textHigh.setTextColor(ContextCompat.getColor(cardHigh.getContext(), R.color.high_priority));
        textMedium.setTextColor(ContextCompat.getColor(cardMedium.getContext(), R.color.medium_priority));
        textLow.setTextColor(ContextCompat.getColor(cardLow.getContext(), R.color.low_priority));

        switch (selectedPriority) {
            case "Cao":
                cardHigh.setChecked(true);
                cardHigh.setBackgroundTintList(ContextCompat.getColorStateList(cardHigh.getContext(), R.color.high_priority));
                textHigh.setTextColor(ContextCompat.getColor(cardHigh.getContext(), android.R.color.white));
                break;
            case "Trung bình":
                cardMedium.setChecked(true);
                cardMedium.setBackgroundTintList(ContextCompat.getColorStateList(cardMedium.getContext(), R.color.medium_priority));
                textMedium.setTextColor(ContextCompat.getColor(cardMedium.getContext(), android.R.color.white));
                break;
            case "Thấp":
                cardLow.setChecked(true);
                cardLow.setBackgroundTintList(ContextCompat.getColorStateList(cardLow.getContext(), R.color.low_priority));
                textLow.setTextColor(ContextCompat.getColor(cardLow.getContext(), android.R.color.white));
                break;
        }
    }

    public String getSelectedPriority() {
        return selectedPriority;
    }
}
