package com.example.homeaidkit;

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

    public DrugListAdapter(@NonNull Context context
            ,List<Drug> resource) {
        super(context,-1,resource);
        listener=(OnItemClickListener) context;
        this.drugList= (ArrayList<Drug>) resource;
        tmpList=(ArrayList<Drug>)resource;
        System.out.println("WYWOŁANY KONSTRUKTOR");
    }
    interface OnItemClickListener{
        void onItemClickListener(Drug drug);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        System.out.println("performuje");
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults filtered=new FilterResults();
                ArrayList<Drug> results=new ArrayList<Drug>();

                if(constraint.toString().length()>0){
                    if(drugList!=null && drugList.size()>0){
                        for(final Drug d:drugList){
                            if(d.getName().toLowerCase().contains(constraint.toString())){
                                results.add(d);
                            }
                        }
                    }
                    filtered.values=results;
                    filtered.count=results.size();
                }
                else {
                    filtered.values=tmpList;
                    filtered.count=tmpList.size();
                    System.out.println("count : "+filtered.count);
                }
                return filtered;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(constraint.length()>0 && results.count>0){
                    System.out.println("constraint: "+constraint+" len: "+constraint.length());
                    drugList.clear();
                    drugList.addAll((ArrayList<Drug>) results.values);
                    notifyDataSetChanged();
                }
                else{
                    drugList=tmpList;
                    notifyDataSetChanged();
                }
            }
        };
    }

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
        SimpleDateFormat format=new SimpleDateFormat("dd-MM-yy");
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
