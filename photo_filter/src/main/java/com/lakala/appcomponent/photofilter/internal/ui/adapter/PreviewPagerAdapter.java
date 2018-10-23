package com.lakala.appcomponent.photofilter.internal.ui.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;


import com.lakala.appcomponent.photofilter.internal.entity.Item;
import com.lakala.appcomponent.photofilter.internal.ui.PreviewItemFragment;

import java.util.ArrayList;
import java.util.List;

public class PreviewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Item> mItems = new ArrayList<>();
    private ArrayList<PreviewItemFragment> fragments = new ArrayList<>();
    private OnPrimaryItemSetListener mListener;

    public PreviewItemFragment currentFragment;

    public PreviewPagerAdapter(FragmentManager manager, OnPrimaryItemSetListener listener) {
        super(manager);
        mListener = listener;
    }

    @Override
    public PreviewItemFragment getItem(int position) {
        return mItems.size() > 0 ? PreviewItemFragment.newInstance(mItems.get(position)) : fragments.get(position);
    }

    @Override
    public int getCount() {
        return mItems.size() > 0 ? mItems.size() : fragments.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        currentFragment = (PreviewItemFragment) object;
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
