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

3. 简便写法
```java
       PhotoFilter.from(SampleActivity.this)
                  .choose(MimeType.ofImage())          //显示类型
                  .countable(true)                     //选中是否显示数字
                  .capture(true)                       //相机
                  .captureStrategy(new CaptureStrategy(true, "com.lakala.photo_filter.sample.file_provider", "Photo"))//相机储存路径
                  .maxSelectable(9)                    //最大选择多少张
                  .spanCount(4)                        //相册一行显示几张
                  .imageEngine(new Glide4Engine())     //使用Glide4作为图片加载引擎
                  .setFilter(true)                     //开启滤镜
                  .forResult(REQUEST_CODE_CHOOSE);

    ##注意开启时Manifest.permission.WRITE_EXTERNAL_STORAGE权限授权
```

### 在Activity或Fragment的onActivityResult里面接收返回的图片路径集合
```java
 @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            if (PhotoFilter.obtainPathResult(data) != null && PhotoFilter.obtainPathResult(data).size() > 0) {
                mAdapter.setData(PhotoFilter.obtainPathResult(data));
            }
        }
    }

     ##注意requestCode == REQUEST_CODE_CHOOSE为开启PhotoFilter传入forResult方法中的参数
```