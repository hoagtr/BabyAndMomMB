package com.example.prm392mnlv.ui.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

public class CategoryFilterAdapter extends ArrayAdapter<String> {

    public CategoryFilterAdapter(@NonNull Context context, @NonNull List<String> categories) {
        super(context, android.R.layout.simple_spinner_item, categories);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }
}
