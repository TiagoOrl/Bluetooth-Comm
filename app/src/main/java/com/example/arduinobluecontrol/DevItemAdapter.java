package com.example.arduinobluecontrol;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DevItemAdapter extends RecyclerView.Adapter<DevItemAdapter.DevItemViewHolder>{

    private ArrayList<DeviceItem> itemsList;

    public static class DevItemViewHolder extends RecyclerView.ViewHolder {

        TextView devName;
        CheckBox connCheckBox;

        public DevItemViewHolder(@NonNull View itemView) {
            super(itemView);
            devName = itemView.findViewById(R.id.devName);
            connCheckBox = itemView.findViewById(R.id.connCheckBox);
        }
    }

    public DevItemAdapter(ArrayList<DeviceItem> itemsList){
        this.itemsList = itemsList;
    }

    @NonNull
    @Override
    public DevItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        return new DevItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DevItemViewHolder holder, int position) {
        holder.devName.setText(this.itemsList.get(position).getDevName());

        holder.connCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Log.d("DB", "----------------------- " + itemsList.get(holder.getAbsoluteAdapterPosition()).name);
                    itemsList.get(holder.getAbsoluteAdapterPosition()).connectAsClient();
                }
                else { itemsList.get(holder.getAbsoluteAdapterPosition()).disconnectClient(); }

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

}
