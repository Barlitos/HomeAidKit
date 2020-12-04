package com.example.homeaidkit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CategoryListAdapter extends ArrayAdapter<Category> {

    private OnItemClickListener listener;

    public CategoryListAdapter(@NonNull Context context, List<Category> resource) {
        super(context, -1,resource);
        listener= (OnItemClickListener)context;
    }

    interface OnItemClickListener{
        void onItemClickListener(Category cat);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Category category=getItem(position);
        if(convertView==null) {
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.category,parent,false);
        }
        TextView categoryName=convertView.findViewById(R.id.categoryName);

        categoryName.setText(category.getName());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               listener.onItemClickListener(category);
            }
        });
        return convertView;
    }

}
