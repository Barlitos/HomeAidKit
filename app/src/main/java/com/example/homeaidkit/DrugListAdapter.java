package com.example.homeaidkit;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DrugListAdapter extends ArrayAdapter<Drug>{
    private OnItemClickListener listener;
    private Date today=new Date();

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
        if(convertView==null) {
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.drug,parent,false);
        }
        TextView drugName=convertView.findViewById(R.id.drugName);
        TextView drugDate=convertView.findViewById(R.id.drugDate);
        TextView drugQuantity=convertView.findViewById(R.id.drugQuantity);

        SimpleDateFormat format=new SimpleDateFormat("dd-mm-yy");
        try {
            Date drugExpdate=format.parse(drug.getExpDate());
            if(today.after(drugExpdate)) {
                drugDate.setTextColor(Color.RED);
            }else{
                drugDate.setTextColor(Color.WHITE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        drugName.setText(drug.getName());
        drugDate.setText(drug.getExpDate());
        if(drug.getUnit()==1) {
            drugQuantity.setText(String.valueOf(drug.getQuantity())+" szt");
        }
        else{
            drugQuantity.setText(String.valueOf(drug.getQuantity())+" ml");
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClickListener(drug);
            }
        });
        return convertView;
    }
}
