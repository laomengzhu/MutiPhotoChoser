package com.ns.mutiphotochoser.sample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.GridView;

import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiscCache;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.ns.mutiphotochoser.constant.CacheConstant;
import com.ns.mutiphotochoser.constant.Constant;
import com.ns.mutiphotochoser.utils.DisplayUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_PHOTO = 1;
    private ImageGridAdapter mAdaper = null;

    @InjectView(R.id.gridView)
    GridView gridView;

    @OnClick(R.id.button_add)
    void addPhotos() {
        Intent intent = new Intent("com.ns.mutiphotochoser.sample.action.CHOSE_PHOTOS");
        //指定图片选择数
        intent.putExtra(Constant.EXTRA_PHOTO_LIMIT, 5);
        startActivityForResult(intent, REQUEST_PICK_PHOTO);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        initImageLoader();
        int numColumns = (getResources().getDisplayMetrics().widthPixels - DisplayUtils.dip2px(12, this)) / DisplayUtils.dip2px(116, this);
        gridView.setNumColumns(numColumns);
        mAdaper = new ImageGridAdapter(getApplication());
        gridView.setAdapter(mAdaper);
    }

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

    private void initImageLoader() {
        if (!ImageLoader.getInstance().isInited()) {
            DisplayImageOptions.Builder displayBuilder = new DisplayImageOptions.Builder();
            displayBuilder.cacheInMemory(true);
            displayBuilder.cacheOnDisk(true);
            displayBuilder.showImageOnLoading(com.ns.mutiphotochoser.R.drawable.default_photo);
            displayBuilder.showImageForEmptyUri(com.ns.mutiphotochoser.R.drawable.default_photo);
            displayBuilder.considerExifParams(true);
            displayBuilder.bitmapConfig(Bitmap.Config.RGB_565);
            displayBuilder.imageScaleType(ImageScaleType.EXACTLY);
            displayBuilder.displayer(new FadeInBitmapDisplayer(300));

            ImageLoaderConfiguration.Builder loaderBuilder = new ImageLoaderConfiguration.Builder(getApplication());
            loaderBuilder.defaultDisplayImageOptions(displayBuilder.build());
            loaderBuilder.memoryCacheSize(getMemoryCacheSize());

            try {
                File cacheDir = new File(getExternalCacheDir() + File.separator + CacheConstant.IMAGE_CACHE_DIRECTORY);
                loaderBuilder.diskCache(new LruDiscCache(cacheDir, DefaultConfigurationFactory.createFileNameGenerator(), 500 * 1024 * 1024));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageLoader.getInstance().init(loaderBuilder.build());
        }

    }

    private int getMemoryCacheSize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        // 4 bytes per pixel
        return screenWidth * screenHeight * 4 * 3;
    }
}
