package com.lakala.appcomponent.photofilter.internal.ui.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.lakala.appcomponent.photofilter.internal.entity.Item;
import com.lakala.appcomponent.photofilter.internal.ui.PreviewItemFragment;

import java.util.ArrayList;
import java.util.List;

public class PreviewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Item> mItems = new ArrayList<>();
    private ArrayList<PreviewItemFragment> fragments = new ArrayList<>();
    private OnPrimaryItemSetListener mListener;

    public PreviewPagerAdapter(FragmentManager manager, OnPrimaryItemSetListener listener) {
        super(manager);
        mListener = listener;
    }

    @Override
    public PreviewItemFragment getItem(int position) {
        return mItems.size() > 0 ? PreviewItemFragment.newInstance(mItems.get(position),false) : fragments.get(position);
    }

    @Override
    public int getCount() {
        return mItems.size() > 0 ? mItems.size() : fragments.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (mListener != null) {
            mListener.onPrimaryItemSet(position);
        }
    }

    public Item getMediaItem(int position) {
        return mItems.size() > 0 ? mItems.get(position) : (Item) fragments.get(position).getArguments().getParcelable(PreviewItemFragment.ARGS_ITEM);
    }

    public void addAll(List<Item> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void addAllFragment(List<PreviewItemFragment> items) {
        fragments.addAll(items);
        notifyDataSetChanged();
    }

    interface OnPrimaryItemSetListener {

        void onPrimaryItemSet(int position);
    }
}
