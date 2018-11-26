package com.lakala.appcomponent.photofilter.internal.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.appcomponent.photofilter.R;
import com.lakala.appcomponent.photofilter.internal.entity.FilterInfo;

import java.util.List;

public class FilterTypeAdapter extends RecyclerView.Adapter<FilterTypeAdapter.MyViewHolder> {

    Context context;
    List<FilterInfo> mData;

    public FilterTypeAdapter(Context context, List<FilterInfo> mData) {
        this.context = context;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.filter_list_item, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (mData.get(position).bitmap != null) {
            holder.iv.setImageBitmap(mData.get(position).bitmap);
        } else {
            holder.iv.setImageResource(mData.get(position).imgRes);
        }
        if (mData.get(position).isSelected) {
            holder.tv_bg.setVisibility(View.VISIBLE);
        } else {
            holder.tv_bg.setVisibility(View.GONE);
        }
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(position);
                for (int i = 0; i < mData.size(); i++) {
                    mData.get(i).isSelected = false;
                }
                mData.get(position).isSelected = true;
                notifyDataSetChanged();
            }
        });
    }

    public OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView iv;
        TextView tv_bg;

        public MyViewHolder(View view) {
            super(view);
            iv = (ImageView) view.findViewById(R.id.iv);
            tv_bg = (TextView) view.findViewById(R.id.tv_bg);
        }
    }
}
