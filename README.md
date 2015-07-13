# MutiPhotoChoser

一款支持多选的图片选择器，支持Android2.0+

![Screenshot](https://raw.githubusercontent.com/xiaolifan/MutiPhotoChoser/master/ScreenShot/2015-06-24_172813.jpg)

## 使用（详见app目录）

### 配置AndroidManifest.xml

1、添加权限：
``` xml
 <!--SD卡读写权限-->
 <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 <uses-permission android:name="ANDROID.PERMISSION.WRITE_EXTERNAL_STORAGE" />
```

2、声明GalleryActivity：
``` xml
<activity android:name="com.ns.mutiphotochoser.GalleryActivity">
    <intent-filter>
        <!--***改成应用的包名-->
        <action android:name="***.action.CHOSE_PHOTOS" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
</activity>
```

### 调起图片选择页面选择图片

``` java
//***改成应用的包名
Intent intent = new Intent("***.action.CHOSE_PHOTOS");
//指定图片最大选择数
intent.putExtra(Constant.EXTRA_PHOTO_LIMIT, 5);
startActivityForResult(intent, REQUEST_PICK_PHOTO);
```

### 处理选择结果

``` java
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != Activity.RESULT_OK) {
        return;
    }

    switch (requestCode) {
        case REQUEST_PICK_PHOTO:
            ArrayList<String> images = data.getStringArrayListExtra(Constant.EXTRA_PHOTO_PATHS);
            mAdaper.swapDatas(images);
            break;
    }
}
```

## 项目依赖

	该库使用了"Android-Universal-Image-Loader"处理图片缓存，github地址：[https://github.com/nostra13/Android-Universal-Image-Loader](https://github.com/nostra13/Android-Universal-Image-Loader);

## License

    Mozilla Public License, version 2.0
