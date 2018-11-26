package com.lakala.appcomponent.photofilter.internal.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.lakala.appcomponent.photofilter.FilterDataSet;
import com.lakala.appcomponent.photofilter.R;
import com.lakala.appcomponent.photofilter.internal.entity.FilterInfo;
import com.lakala.appcomponent.photofilter.internal.entity.Item;
import com.lakala.appcomponent.photofilter.internal.entity.SelectionSpec;
import com.lakala.appcomponent.photofilter.internal.ui.adapter.FilterTypeAdapter;
import com.lakala.appcomponent.photofilter.internal.utils.BitmapUtils;
import com.lakala.appcomponent.photofilter.internal.utils.PathUtils;
import com.lakala.appcomponent.photofilter.ui.PhotoFilterActivity;

import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;

public class FilterPreviewActivity extends BasePreviewActivity {

    TextView pageSize;

    List<Item> selected;

//    Item currentItem;

    List<PreviewItemFragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!SelectionSpec.getInstance().hasInited) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        pageSize = (TextView) findViewById(R.id.page_size);
        pageSize.setVisibility(View.VISIBLE);
        mCheckView.setVisibility(View.GONE);
        selected = getIntent().getParcelableArrayListExtra("extra_filter_item");
//        currentItem = selected.get(0);
        pageSize.setText("1/" + selected.size());
        fragments.clear();
        for (int i = 0; i < selected.size(); i++) {
            fragments.add(PreviewItemFragment.newInstance(selected.get(i), true));
        }
        mAdapter.addAllFragment(fragments);
        mPager.setOffscreenPageLimit(8);
        mButtonApply.setEnabled(true);
        mButtonApply.setTextColor(Color.parseColor("#ffffff"));
        mButtonApply.setText("下一步");
        mButtonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList<String> pathList = new ArrayList<>();
                for (int i = 0; i < fragments.size(); i++) {
//                    //没有使用GpuImage过期方法saveToPictures去保存图片
//                    String path = BitmapUtils.saveBitmap(FilterPreviewActivity.this, fragments.get(i).bitmap, "hjc_fb_photo_" + i);
//                    pathList.add(path);
                    //使用GpuImage过期方法saveToPictures去保存图片
                    final int finalI = i;
//                    mSpec.compressSize == 0 ? fragments.get(i).bitmap : BitmapUtils.getZoomImage(fragments.get(i).bitmap, mSpec.compressSize)
                    fragments.get(i).gpuImage.saveToPictures(fragments.get(i).bitmap, "hjc_camera_photo",
                            System.currentTimeMillis() + i + ".jpg",
                            new GPUImage.OnPictureSavedListener() {
                                @Override
                                public void onPictureSaved(final Uri uri) {
                                    pathList.add(PathUtils.getPath(FilterPreviewActivity.this, uri));
                                    if (finalI == fragments.size() - 1) {
                                        Intent result = new Intent();
                                        result.putStringArrayListExtra(PhotoFilterActivity.EXTRA_RESULT_SELECTION_PATH, pathList);
                                        setResult(RESULT_OK, result);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        pageSize.setText(position + 1 + "/" + selected.size());
//        currentItem = selected.get(position);

    }
}
