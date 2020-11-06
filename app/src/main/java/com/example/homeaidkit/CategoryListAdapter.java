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


    public CategoryListAdapter(@NonNull Context context, List<Category> resource) {
        super(context, -1,resource);
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

        return convertView;
    }
}
