package com.lakala.appcomponent.photofilter.internal.entity;


import com.lakala.appcomponent.photofilter.FilterDataSet;

public class FilterInfo {
    public String name;
    public int imgRes;
    public FilterDataSet.FilterType type;
    public boolean isSelected;

    public FilterInfo(String name, int imgRes, FilterDataSet.FilterType type, boolean isSelected) {
        this.name = name;
        this.imgRes = imgRes;
        this.type = type;
        this.isSelected = isSelected;
    }
}
