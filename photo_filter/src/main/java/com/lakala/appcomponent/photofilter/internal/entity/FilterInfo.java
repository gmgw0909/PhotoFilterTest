package com.lakala.appcomponent.photofilter.internal.entity;


import com.lakala.appcomponent.photofilter.FilterDataSet;

public class FilterInfo {
    public String name;
    public FilterDataSet.FilterType type;
    public boolean isSelected;

    public FilterInfo(String name, FilterDataSet.FilterType type, boolean isSelected) {
        this.name = name;
        this.type = type;
        this.isSelected = isSelected;
    }
}
