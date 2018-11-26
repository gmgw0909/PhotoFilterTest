package com.lakala.appcomponent.photofilter.internal.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lakala.appcomponent.photofilter.FilterDataSet;
import com.lakala.appcomponent.photofilter.R;
import com.lakala.appcomponent.photofilter.internal.entity.FilterInfo;
import com.lakala.appcomponent.photofilter.internal.entity.Item;
import com.lakala.appcomponent.photofilter.internal.entity.SelectionSpec;
import com.lakala.appcomponent.photofilter.internal.ui.adapter.FilterTypeAdapter;
import com.lakala.appcomponent.photofilter.internal.ui.widget.BaseFragment;
import com.lakala.appcomponent.photofilter.internal.utils.BitmapUtils;
import com.lakala.appcomponent.photofilter.internal.utils.PhotoMetadataUtils;
import com.lakala.appcomponent.photofilter.listener.OnFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import jp.co.cyberagent.android.gpuimage.GPUImage;

public class PreviewItemFragment extends BaseFragment {

    public static final String ARGS_ITEM = "ARGS_ITEM";
    public static final String IS_NEED_FILTER = "IS_NEED_FILTER";
    private OnFragmentInteractionListener mListener;
    public GPUImage gpuImage;
    private ImageViewTouch image;
    private Point size;
    public Bitmap bitmap;
    private Item item;
    private boolean isNeedFilter;//区别相册预览和选中的Fragment 是否显示滤镜选择器
    private boolean isInit;//控制只加载一次Fragment
    private RecyclerView mRecyclerView;
    private FilterTypeAdapter adapter;
    List<FilterInfo> data = new ArrayList<>();

    public static PreviewItemFragment newInstance(Item item, boolean isNeedFilter) {
        PreviewItemFragment fragment = new PreviewItemFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_ITEM, item);
        bundle.putBoolean(IS_NEED_FILTER, isNeedFilter);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_preview_item;
    }

    @Override
    protected void normalLoad() {
        gpuImage = new GPUImage(getActivity());
        item = getArguments().getParcelable(ARGS_ITEM);
        isNeedFilter = getArguments().getBoolean(IS_NEED_FILTER);

        image = findViewById(R.id.image_view);
        image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        image.setSingleTapListener(new ImageViewTouch.OnImageViewTouchSingleTapListener() {
            @Override
            public void onSingleTapConfirmed() {
                if (mListener != null) {
                    mListener.onClick();
                }
            }
        });

        View videoPlayButton = findViewById(R.id.video_play_button);
        if (item.isVideo()) {
            videoPlayButton.setVisibility(View.VISIBLE);
            videoPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(item.uri, "video/*");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getContext(), R.string.error_no_video_activity, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            videoPlayButton.setVisibility(View.GONE);
        }
        mRecyclerView = findViewById(R.id.filter_rv);
        if (SelectionSpec.getInstance().setFilter && isNeedFilter) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            mRecyclerView.setAdapter(adapter = new FilterTypeAdapter(getActivity(), data));
            adapter.setOnItemClickListener(new FilterTypeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    //设置灰度的滤镜
                    gpuImage.setFilter(FilterDataSet.createFilterForType(getActivity(), data.get(position).type));
                    bitmap = gpuImage.getBitmapWithFilterApplied();
                    //显示处理后的图片
                    image.setImageBitmap(bitmap);
                }
            });
        } else {
            mRecyclerView.setVisibility(View.GONE);
        }
        size = PhotoMetadataUtils.getBitmapSize(item.getContentUri(), getActivity());
        if (!isNeedFilter) {
            if (item.isGif()) {
                SelectionSpec.getInstance().imageEngine.loadGifImage(getContext(), size.x, size.y, image,
                        item.getContentUri());
            } else {
                SelectionSpec.getInstance().imageEngine.loadImage(getContext(), size.x, size.y, image,
                        item.getContentUri());
            }
        } else {
            Glide.with(this)
                    .asBitmap()
                    .load(item.getContentUri())
                    .apply(new RequestOptions()
                            .override(size.x, size.y)
                            .priority(Priority.NORMAL)
                            .fitCenter())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            bitmap = resource;
                            gpuImage.setImage(bitmap);
                            //显示处理后的图片
                            image.setImageBitmap(bitmap);
                        }
                    });
        }
    }

    @Override
    protected void lazyLoad() {//延迟加载且只加载一次
        if (SelectionSpec.getInstance().setFilter && isNeedFilter && !isInit) {
            initFilterList();
            isInit = true;
        }

    }

    private void initFilterList() {
        Glide.with(this).asBitmap().apply(new RequestOptions().override(70, 70)
                .centerCrop()).load(item.getContentUri()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                //设置滤镜类型数据集合
                data.clear();
                data.addAll(FilterDataSet.initFilterData(getActivity(), resource));
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void resetView() {
        if (getView() != null) {
            ((ImageViewTouch) getView().findViewById(R.id.image_view)).resetMatrix();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消加载
        Glide.with(this).clear(image);
    }
}
