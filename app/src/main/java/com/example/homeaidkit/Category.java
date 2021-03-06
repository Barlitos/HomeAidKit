package com.example.homeaidkit;

import androidx.annotation.NonNull;

public class Category implements Comparable<Category>{
    private int id;
    private String name;

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Category o) {
        return this.getName().toLowerCase().compareTo(o.getName().toLowerCase());
    }
}
