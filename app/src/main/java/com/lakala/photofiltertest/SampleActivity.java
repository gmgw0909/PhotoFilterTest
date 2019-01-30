package com.lakala.photofiltertest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lakala.appcomponent.photofilter.MimeType;
import com.lakala.appcomponent.photofilter.PhotoFilter;
import com.lakala.appcomponent.photofilter.engine.impl.Glide4Engine;
import com.lakala.appcomponent.photofilter.engine.impl.GlideEngine;
import com.lakala.appcomponent.photofilter.listener.OnGetPathListListener;

import java.io.File;
import java.util.List;

public class SampleActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_CHOOSE = 23;
    private static final int MY_PERMISSIONS_REQUEST_CODE = 1001;

    private UriAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.zhihu).setOnClickListener(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter = new UriAdapter());
    }

    @Override
    public void onClick(final View v) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CODE);
        } else {
            //权限已经被授予
            openPhotoFilter();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            if (PhotoFilter.obtainPathResult(data) != null && PhotoFilter.obtainPathResult(data).size() > 0) {
//                mAdapter.setData(PhotoFilter.obtainPathResult(data));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openPhotoFilter();
            } else {
                Toast.makeText(SampleActivity.this, R.string.permission_request_denied, Toast.LENGTH_LONG)
                        .show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void openPhotoFilter() {
        PhotoFilter.from(SampleActivity.this)
                .choose(MimeType.ofImage())//显示类型
                .countable(false) //选中是否显示数字
                .capture(true) //相机
//                                            .captureStrategy(new CaptureStrategy(true, BuildConfig.APPLICATION_ID + ".file_provider", "Photo"))//相机储存路径
                .maxSelectable(9) //最大选择多少张
                .spanCount(4) //相册一行显示几张
                .imageEngine(new Glide4Engine())    //使用Glide4作为图片加载引擎
                .setFilter(true)//开启滤镜
                .setOnGetPathListListener(new OnGetPathListListener() {
                    @Override
                    public void OnGetPathList(@NonNull List<String> pathList) {
                        mAdapter.setData(pathList);
                    }
                })
                .go();
    }


    private class UriAdapter extends RecyclerView.Adapter<UriAdapter.UriViewHolder> {

        private List<String> mPaths;

        void setData(List<String> paths) {
            mPaths = paths;
            notifyDataSetChanged();
        }

        @Override
        public UriViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UriViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.uri_item, parent, false));
        }

        @Override
        public void onBindViewHolder(UriViewHolder holder, int position) {
            holder.mPath.setText(mPaths.get(position));
            holder.mPath.setAlpha(position % 2 == 0 ? 1.0f : 0.54f);
            Glide.with(SampleActivity.this).load(new File(mPaths.get(position))).into(holder.iv);
        }

        @Override
        public int getItemCount() {
            return mPaths == null ? 0 : mPaths.size();
        }

        class UriViewHolder extends RecyclerView.ViewHolder {

            private TextView mUri;
            private TextView mPath;
            private ImageView iv;

            UriViewHolder(View contentView) {
                super(contentView);
                mUri = (TextView) contentView.findViewById(R.id.uri);
                mPath = (TextView) contentView.findViewById(R.id.path);
                iv = (ImageView) contentView.findViewById(R.id.image_view);
            }
        }
    }

}
