package com.example.homeaidkit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DrugListAdapter extends ArrayAdapter<Drug> implements Filterable{
    private OnItemClickListener listener;
    private Date today=new Date();
    private ArrayList<Drug> drugList;
    private ArrayList<Drug> tmpList;

    DrugListAdapter(@NonNull Context context
            ,List<Drug> resource) {
        super(context,-1,resource);
        listener=(OnItemClickListener) context;
        this.drugList= (ArrayList<Drug>) resource;
        this.tmpList=new ArrayList<Drug>(drugList);

    }
    interface OnItemClickListener{
        void onItemClickListener(Drug drug);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults filtered = new FilterResults();
                ArrayList<Drug> results = new ArrayList<Drug>();

                if (constraint.length() == 0 || constraint==null) {
                    results.addAll(tmpList);
                } else {
                    constraint = constraint.toString().toLowerCase().trim();
                    for (Drug d :tmpList) {
                        if (d.getName().contains(constraint)) {
                            results.add(d);
                        }
                    }
                }
                filtered.count = results.size();
                filtered.values = results;
                return filtered;
            }

        @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                    drugList.clear();
                    drugList.addAll((ArrayList<Drug>)results.values);
                    notifyDataSetChanged();
            }
        };
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView
            , @NonNull ViewGroup parent){
        final Drug drug=getItem(position);
        if(convertView==null) {
            convertView= LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.drug,parent,false);
        }
        TextView drugName=convertView.findViewById(R.id.drugName);
        TextView drugDate=convertView.findViewById(R.id.drugDate);
        TextView drugQuantity=convertView.findViewById(R.id.drugQuantity);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format=new SimpleDateFormat("dd-MM-yy");
        try {
            assert drug != null;
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
            drugQuantity.setText((drug.getQuantity())+" szt");
        }
        else{
            drugQuantity.setText((drug.getQuantity())+" ml");
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
