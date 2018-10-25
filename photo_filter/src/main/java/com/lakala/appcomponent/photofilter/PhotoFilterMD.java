package com.lakala.appcomponent.photofilter;

import android.app.Activity;
import android.content.Intent;

import com.lakala.appcomponent.photofilter.engine.impl.Glide4Engine;
import com.lakala.appcomponent.photofilter.internal.entity.CaptureStrategy;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PhotoFilterMD extends UZModule {

    public PhotoFilterMD(UZWebView webView) {
        super(webView);
    }

    private UZModuleContext mJsCallback;

    private static int ACTIVITY_REQUEST_CODE = 25;

    public void jsmethod_startActivityForResult(UZModuleContext moduleContext) {
        mJsCallback = moduleContext;
        PhotoFilter.from(getContext())
                .choose(MimeType.ofImage())//显示类型
                .countable(false) //选中是否显示数字
                .capture(true) //相机
                .captureStrategy(new CaptureStrategy(true, "com.lakala.photo_filter.sample.file_provider", "Photo"))//相机储存路径
                .maxSelectable(moduleContext.optInt("maxSelectCount")) //最大选择多少张
                .spanCount(4) //相册一行显示几张
                .imageEngine(new Glide4Engine())    //使用Glide4作为图片加载引擎
                .setFilter(true)      //开启滤镜
                .forResult(ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == ACTIVITY_REQUEST_CODE) {
            List<String> pathList = PhotoFilter.obtainPathResult(data);
            if (pathList != null && pathList.size() > 0) {
                try {
                    JSONObject ret = new JSONObject();
                    ret.put("pathList", pathList.toArray(new String[pathList.size()]));
                    mJsCallback.success(ret, false);
                    mJsCallback = null;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
