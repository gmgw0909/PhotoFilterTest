package com.lakala.appcomponent.photofilter.internal.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.lakala.appcomponent.photofilter.FilterDataSet;
import com.lakala.appcomponent.photofilter.R;
import com.lakala.appcomponent.photofilter.internal.entity.FilterInfo;
import com.lakala.appcomponent.photofilter.internal.entity.SelectionSpec;
import com.lakala.appcomponent.photofilter.internal.ui.adapter.FilterTypeAdapter;
import com.lakala.appcomponent.photofilter.internal.utils.BitmapUtils;
import com.lakala.appcomponent.photofilter.internal.utils.CameraHelper;
import com.lakala.appcomponent.photofilter.internal.utils.PathUtils;
import com.lakala.appcomponent.photofilter.ui.PhotoFilterActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImage.OnPictureSavedListener;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

public class CameraActivity extends AppCompatActivity implements OnClickListener {

    private GPUImage mGPUImage;
    private CameraHelper mCameraHelper;
    private CameraLoader mCamera;
    private GPUImageFilter mFilter;
    private GLSurfaceView mGLSurfaceView;
    private RecyclerView mRecyclerView;
    private FilterTypeAdapter adapter;
    private Bitmap bitmap;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        findViewById(R.id.button_capture).setOnClickListener(this);
        mGLSurfaceView = findViewById(R.id.surfaceView);
        mFilter = new GPUImageFilter();
        mGPUImage = new GPUImage(this);
        mGPUImage.setGLSurfaceView(mGLSurfaceView);

        mCameraHelper = new CameraHelper(this);
        mCamera = new CameraLoader();

        View cameraSwitchView = findViewById(R.id.img_switch_camera);
        mRecyclerView = findViewById(R.id.filter_rv);
        if (!SelectionSpec.getInstance().setFilter) {
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        cameraSwitchView.setOnClickListener(this);
        if (!mCameraHelper.hasFrontCamera() || !mCameraHelper.hasBackCamera()) {
            cameraSwitchView.setVisibility(View.GONE);
        }
        //设置滤镜类型数据集合
        final List<FilterInfo> data = FilterDataSet.initFilterData();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(CameraActivity.this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(adapter = new FilterTypeAdapter(CameraActivity.this, data));
        adapter.setOnItemClickListener(new FilterTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                switchFilterTo(data.get(position).type);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCamera.onResume();
    }

    @Override
    protected void onPause() {
        mCamera.onPause();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_capture) {
            if (mCamera.mCameraInstance.getParameters().getFocusMode().equals(
                    Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                takePicture();
            } else {
                mCamera.mCameraInstance.autoFocus(new Camera.AutoFocusCallback() {

                    @Override
                    public void onAutoFocus(final boolean success, final Camera camera) {
                        takePicture();
                    }
                });
            }
        } else if (i == R.id.img_switch_camera) {
            mCamera.switchCamera();
        }
    }

    private void takePicture() {
//        // TODO get a size that is about the size of the screen

//        Parameters parameters = mCamera.mCameraInstance.getParameters();
//        int PreviewWidth = 0;
//        int PreviewHeight = 0;
//        // 选择合适的预览尺寸
//        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
//        // 如果sizeList只有一个我们也没有必要做什么了，因为就他一个别无选择
//        if (sizeList.size() > 1) {
//            Iterator<Camera.Size> iterator = sizeList.iterator();
//            while (iterator.hasNext()) {
//                Camera.Size cur = iterator.next();
//                if (cur.width >= PreviewWidth
//                        && cur.height >= PreviewHeight) {
//                    PreviewWidth = cur.width;
//                    PreviewHeight = cur.height;
//                    break;
//                }
//            }
//        }
//        parameters.setPreviewSize(PreviewWidth, PreviewHeight); // 获得摄像区域的大小
//        parameters.setPictureSize(PreviewWidth, PreviewHeight); // 获得保存图片的大小
//        // the best one for screen size (best fill screen)
//        if (parameters.getSupportedFocusModes().contains(
//                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
//            parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//        }
//        mCamera.mCameraInstance.setParameters(parameters);

        mCamera.mCameraInstance.takePicture(null, null,
                new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, final Camera camera) {
//                        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                        mGPUImage.setImage(bitmap);
//                        mGPUImage.setFilter(mFilter);
//                        Matrix matrix = new Matrix();
//                        matrix.setRotate(90);
//                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//                        mGPUImage.saveToPictures("hjc_camera_photo",
//                                System.currentTimeMillis() + ".jpg",
//                                new OnPictureSavedListener() {
//
//                                    @Override
//                                    public void onPictureSaved(final Uri uri) {
//                                        Intent result = new Intent();
//                                        ArrayList<String> pathList = new ArrayList<>();
//                                        pathList.add(PathUtils.getPath(CameraActivity.this, uri));
//                                        result.putStringArrayListExtra(PhotoFilterActivity.EXTRA_RESULT_SELECTION_PATH, pathList);
//                                        setResult(RESULT_OK, result);
//                                        finish();
//                                    }
//                                });


                        final File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                        if (pictureFile == null) {
                            Log.d("ASDF",
                                    "Error creating media file, check storage permissions");
                            return;
                        }

                        try {
                            FileOutputStream fos = new FileOutputStream(pictureFile);
                            fos.write(data);
                            fos.close();
                        } catch (FileNotFoundException e) {
                            Log.d("ASDF", "File not found: " + e.getMessage());
                        } catch (IOException e) {
                            Log.d("ASDF", "Error accessing file: " + e.getMessage());
                        }

                        data = null;
                        Bitmap bitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
                        mGPUImage.setImage(bitmap);
                        mGPUImage.setFilter(mFilter);
                        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                        bitmap = BitmapUtils.rotateBp(bitmap, 90);
                        mGPUImage.saveToPictures(bitmap, "hjc_camera_photo_",
                                System.currentTimeMillis() + ".jpg",
                                new OnPictureSavedListener() {

                                    @Override
                                    public void onPictureSaved(final Uri uri) {
                                        pictureFile.delete();
                                        Intent result = new Intent();
                                        ArrayList<String> pathList = new ArrayList<>();
                                        pathList.add(PathUtils.getPath(CameraActivity.this, uri));
                                        result.putStringArrayListExtra(PhotoFilterActivity.EXTRA_RESULT_SELECTION_PATH, pathList);
                                        setResult(RESULT_OK, result);
                                        finish();
                                    }
                                });

//                                Intent result = new Intent();
//                                ArrayList<String> pathList = new ArrayList<>();
//                                pathList.add(BitmapUtils.saveBitmap(CameraActivity.this, bp, "hjc_camera_photo_" + System.currentTimeMillis()));
//                                result.putStringArrayListExtra(PhotoFilterActivity.EXTRA_RESULT_SELECTION_PATH, pathList);
//                                setResult(RESULT_OK, result);
//                                finish();
                    }
                });
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private static File getOutputMediaFile(final int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "hjc_fb_photo");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void switchFilterTo(final FilterDataSet.FilterType type) {
        mFilter = FilterDataSet.createFilterForType(CameraActivity.this, type);
        mGPUImage.setFilter(mFilter);
    }

    private class CameraLoader {

        private int mCurrentCameraId = 0;
        private Camera mCameraInstance;

        public void onResume() {
            setUpCamera(mCurrentCameraId);
        }

        public void onPause() {
            releaseCamera();
        }

        public void switchCamera() {
            releaseCamera();
            mCurrentCameraId = (mCurrentCameraId + 1) % mCameraHelper.getNumberOfCameras();
            setUpCamera(mCurrentCameraId);
        }

        private void setUpCamera(final int id) {
            mCameraInstance = getCameraInstance(id);
            Parameters parameters = mCameraInstance.getParameters();
            int PreviewWidth = 0;
            int PreviewHeight = 0;
            // 选择合适的预览尺寸
            List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
            // 如果sizeList只有一个我们也没有必要做什么了，因为就他一个别无选择
            if (sizeList.size() > 1) {
                Iterator<Camera.Size> iterator = sizeList.iterator();
                while (iterator.hasNext()) {
                    Camera.Size cur = iterator.next();
                    if (cur.width >= PreviewWidth
                            && cur.height >= PreviewHeight) {
                        PreviewWidth = cur.width;
                        PreviewHeight = cur.height;
                        break;
                    }
                }
            }
            parameters.setPreviewSize(PreviewWidth, PreviewHeight); // 获得摄像区域的大小
            parameters.setPictureSize(PreviewWidth, PreviewHeight); // 获得保存图片的大小
            // the best one for screen size (best fill screen)
            if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            mCameraInstance.setParameters(parameters);
            // parameters.setFlashMode(Parameters.FLASH_MODE_TORCH); //开启闪光灯,支持
            int orientation = mCameraHelper.getCameraDisplayOrientation(
                    CameraActivity.this, mCurrentCameraId);
            CameraHelper.CameraInfo2 cameraInfo = new CameraHelper.CameraInfo2();
            mCameraHelper.getCameraInfo(mCurrentCameraId, cameraInfo);
            boolean flipHorizontal = cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT;
            mGPUImage.setUpCamera(mCameraInstance, orientation, flipHorizontal, false);
        }

        /**
         * A safe way to get an instance of the Camera object.
         */
        private Camera getCameraInstance(final int id) {
            Camera c = null;
            try {
                c = mCameraHelper.openCamera(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return c;
        }

        private void releaseCamera() {
            mCameraInstance.setPreviewCallback(null);
            mCameraInstance.stopPreview();//停掉原来摄像头的预览
            mCameraInstance.lock();
            mCameraInstance.release();
            mCameraInstance = null;
        }
    }
}
