### 扫惠拍照相册滤镜模块
### Usage

1. Add it in your root build.gradle ：
 ```java

 allprojects {

 }
 ```

2. Add the dependency
```java
dependencies {

}
```

### 在Activity或Fragment中开启PhotoFilter
```java
       PhotoFilter.from(SampleActivity.this)
                  .choose(MimeType.ofImage())          //显示类型
                  .countable(true)                     //选中是否显示数字
                  .capture(true)                       //相机
                  .captureStrategy(new CaptureStrategy(true, "com.lakala.photo_filter.sample.file_provider", "Photo"))//相机储存路径
                  .maxSelectable(9)                    //最大选择多少张
                  .spanCount(4)                        //相册一行显示几张
                  .imageEngine(new Glide4Engine())     //使用Glide4作为图片加载引擎
                  .setFilter(true)                     //开启滤镜选择
                  .setOnGetPathListListener(new OnGetPathListListener() {
                            @Override
                            public void OnGetPathList(@NonNull List<String> pathList) {
                                   ...    //pathList为返回的图片路径集合
                                   }
                            })
                  .forResult(REQUEST_CODE_CHOOSE);

    注意：开启时Manifest.permission.WRITE_EXTERNAL_STORAGE权限授权
```