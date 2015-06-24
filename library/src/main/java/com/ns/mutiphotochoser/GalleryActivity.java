package com.ns.mutiphotochoser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiscCache;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.ns.mutiphotochoser.constant.CacheConstant;
import com.ns.mutiphotochoser.constant.Constant;
import com.ns.mutiphotochoser.content.ImagesLoader;
import com.ns.mutiphotochoser.fragment.ImageGridFragment;
import com.ns.mutiphotochoser.fragment.ImagePagerFragment;
import com.ns.mutiphotochoser.model.ImageBean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<ImageBean>>, ImageGridFragment.ViewImageListener, FragmentManager.OnBackStackChangedListener {

    private DisplayImageOptions options = null;
    private ArrayList<ImageBean> mImages = null;
    private RelativeLayout btnLayout = null;
    private LinearLayout numberLayout = null;
    private TextView previewTextView = null;
    private ImageView useButton = null;
    private int selectedCount = 0;
    private int limit = Integer.MAX_VALUE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_images);
        getSupportActionBar().setIcon(R.drawable.empty_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.please_choose_pic);

        btnLayout = (RelativeLayout) findViewById(R.id.llBottomContainer);

        numberLayout = (LinearLayout) findViewById(R.id.ll_picture_count);
        numberLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCount <= 0) {
                    return;
                }
                openImagePager(false, 0);
            }
        });

        previewTextView = (TextView) findViewById(R.id.tv_preview_image);
        useButton = (ImageView) findViewById(R.id.btn_ok);
        useButton.setEnabled(false);
        useButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra(Constant.EXTRA_PHOTO_PATHS, getSelectedImagePaths());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        limit = getIntent().getIntExtra(Constant.EXTRA_PHOTO_LIMIT, Integer.MAX_VALUE);

        initImageLoader();
        ImageGridFragment fragment = ImageGridFragment.newInstance(options);
        fragment.setViewImageListener(this);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft.replace(R.id.content, fragment).commit();
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportLoaderManager().destroyLoader(0);
        getSupportFragmentManager().removeOnBackStackChangedListener(this);
    }

    @Override
    public Loader<ArrayList<ImageBean>> onCreateLoader(int arg0, Bundle arg1) {
        return new ImagesLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<ImageBean>> arg0, ArrayList<ImageBean> arg1) {
        this.mImages = arg1;
        swapDatas(arg1);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ImageBean>> arg0) {
        swapDatas(null);
    }

    private void swapDatas(ArrayList<ImageBean> arg1) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
        if (fragment instanceof ImagePagerFragment) {
            ((ImagePagerFragment) fragment).swapDatas(arg1);
        } else if (fragment instanceof ImageGridFragment) {
            ((ImageGridFragment) fragment).swapDatas(arg1);
        }
    }

    @Override
    public void viewImage(int position) {
        openImagePager(true, position);
    }

    private void openImagePager(boolean all, int position) {
        ImagePagerFragment fragment = ImagePagerFragment.newInstance(options);
        fragment.setChoseImageListener(this);
        Bundle args = new Bundle();
        args.putBoolean("all", all);
        if (all) {
            args.putParcelableArrayList("datas", mImages);
            args.putInt("position", position);
        } else {
            args.putParcelableArrayList("datas", getSelectedImages());
        }
        fragment.setArguments(args);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft.replace(R.id.content, fragment);
        ft.addToBackStack(null);
        ft.commit();
        btnLayout.setVisibility(View.GONE);
    }

    private ArrayList<ImageBean> getSelectedImages() {
        ArrayList<ImageBean> selectedImages = new ArrayList<ImageBean>();
        for (ImageBean image : mImages) {
            if (image.isSeleted()) {
                selectedImages.add(image);
            }
        }
        return selectedImages;
    }

    private ArrayList<String> getSelectedImagePaths() {
        ArrayList<String> selectedImages = new ArrayList<String>();
        for (ImageBean image : mImages) {
            if (image.isSeleted()) {
                selectedImages.add(image.getPath());
            }
        }
        return selectedImages;
    }

    @Override
    public boolean onSelected(ImageBean image) {
        if (selectedCount >= limit) {
            Toast.makeText(getApplicationContext(), R.string.arrive_limit_count, Toast.LENGTH_SHORT).show();
            return false;
        }
        image.setSeleted(true);
        selectedCount++;
        refreshPreviewTextView();
        return true;
    }

    @Override
    public boolean onCancelSelect(ImageBean image) {
        image.setSeleted(false);
        selectedCount--;
        refreshPreviewTextView();
        return true;
    }

    private void refreshPreviewTextView() {
        if (selectedCount <= 0) {
            numberLayout.setVisibility(View.GONE);
            previewTextView.setText("");
            useButton.setEnabled(false);
        } else {
            numberLayout.setVisibility(View.VISIBLE);
            previewTextView.setText(selectedCount + "");
            useButton.setEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            btnLayout.setVisibility(View.GONE);
        } else {
            btnLayout.setVisibility(View.VISIBLE);
        }

    }

    private void initImageLoader() {
        if (options == null) {
            DisplayImageOptions.Builder displayBuilder = new DisplayImageOptions.Builder();
            displayBuilder.cacheInMemory(true);
            displayBuilder.cacheOnDisk(true);
            displayBuilder.showImageOnLoading(R.drawable.default_photo);
            displayBuilder.showImageForEmptyUri(R.drawable.default_photo);
            displayBuilder.considerExifParams(true);
            displayBuilder.bitmapConfig(Bitmap.Config.RGB_565);
            displayBuilder.imageScaleType(ImageScaleType.EXACTLY);
            displayBuilder.displayer(new FadeInBitmapDisplayer(300));
            options = displayBuilder.build();
        }

        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration.Builder loaderBuilder = new ImageLoaderConfiguration.Builder(getApplication());
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
