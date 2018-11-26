package com.lakala.appcomponent.photofilter.internal.entity;


import android.graphics.Bitmap;

import com.lakala.appcomponent.photofilter.FilterDataSet;

public class FilterInfo {
    public String name;
    public int imgRes;
    public Bitmap bitmap;
    public FilterDataSet.FilterType type;
    public boolean isSelected;

    public FilterInfo(String name, int imgRes, FilterDataSet.FilterType type, boolean isSelected) {
        this.name = name;
        this.imgRes = imgRes;
        this.type = type;
        this.isSelected = isSelected;
    }

    public FilterInfo(String name, Bitmap bitmap, FilterDataSet.FilterType type, boolean isSelected) {
        this.name = name;
        this.bitmap = bitmap;
        this.type = type;
        this.isSelected = isSelected;
    }
}
