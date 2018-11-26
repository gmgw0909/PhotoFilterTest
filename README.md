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
                       .choose(MimeType.ofImage())//显示类型
                       .countable(false) //选中是否显示数字
                       .capture(true) //相机
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

    注意：开启时Manifest.permission.WRITE_EXTERNAL_STORAGE权限授权
```

### 需要拍照功能的话需要在app的清单文件application节点下加入
```java
        <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="${applicationId}.file_provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths_public" />
        </provider>
```