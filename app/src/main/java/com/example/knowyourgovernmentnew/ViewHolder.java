package com.example.knowyourgovernmentnew;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends  RecyclerView.ViewHolder {

    TextView rank;
    TextView nameandparty;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        rank = itemView.findViewById(R.id.officialpostlayout);
        nameandparty = itemView.findViewById(R.id.officialnamelayout);
    }
}
