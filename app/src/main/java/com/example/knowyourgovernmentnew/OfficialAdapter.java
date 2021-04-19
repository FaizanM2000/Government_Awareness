package com.example.knowyourgovernmentnew;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OfficialAdapter extends RecyclerView.Adapter<ViewHolder> {

    private ArrayList<Official> officialArrayList;
    private MainActivity mainActivity;

    public OfficialAdapter(MainActivity mainActivity, ArrayList<Official> officiallist){
        this.officialArrayList = officiallist;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.official, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Official n = officialArrayList.get(position);
        String rankdisplay = n.getPostname();
        holder.rank.setText(rankdisplay);

        holder.nameandparty.setText(n.getName()+" "+"("+n.getParty()+")");

    }

    @Override
    public int getItemCount() {
        return officialArrayList.size();
    }
}
