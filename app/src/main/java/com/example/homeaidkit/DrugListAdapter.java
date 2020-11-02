package com.example.homeaidkit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.List;

public class DrugListAdapter extends ArrayAdapter<Drug>{
    private OnItemClickListener listener;

    public DrugListAdapter(@NonNull Context context, List<Drug> resource) {
        super(context,-1,resource);

        listener=(OnItemClickListener) context;

    }

    interface OnItemClickListener{
        void onItemClickListener(Drug drug);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Drug drug=getItem(position);
        System.out.println(drug.getQuantity());
        if(convertView==null) {
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.drug,parent,false);
        }
        TextView drugName=convertView.findViewById(R.id.drugName);
        TextView drugDate=convertView.findViewById(R.id.drugDate);
        TextView drugQuantity=convertView.findViewById(R.id.drugQuantity);

        drugName.setText(drug.getName());
        drugDate.setText(drug.getExpDate());
        drugQuantity.setText(String.valueOf(drug.getQuantity()));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClickListener(drug);
            }
        });


        return convertView;
    }


}
