package com.ns.mutiphotochoser.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ns.mutiphotochoser.R;
import com.ns.mutiphotochoser.model.ImageBean;
import com.ns.mutiphotochoser.utils.ChoseImageListener;

import java.util.ArrayList;

/**
 * @author xiaolf1
 */
public class ImagePagerFragment extends Fragment implements OnPageChangeListener {

    private ArrayList<ImageBean> mImages = null;
    private ImagePagerAdapter mAdapter = null;
    private DisplayImageOptions options = null;
    private ViewPager mImagePager = null;
    private ChoseImageListener mChoseImageListener = null;
    private boolean all = false;

    public static ImagePagerFragment newInstance(DisplayImageOptions options) {
        ImagePagerFragment fragment = new ImagePagerFragment();
        fragment.setImageLoader(options);
        return fragment;
    }

    private void setImageLoader(DisplayImageOptions options) {
        this.options = options;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mImagePager = new ViewPager(getActivity());
        mImages = getArguments().getParcelableArrayList("datas");
        all = getArguments().getBoolean("all");
        int position = getArguments().getInt("position");
        mAdapter = new ImagePagerAdapter();
        mImagePager.setAdapter(mAdapter);
        mImagePager.setCurrentItem(position, true);
        mImagePager.setOnPageChangeListener(this);
        setHasOptionsMenu(true);
        return mImagePager;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        choseMenuItem = menu.add(0, 0, 1, "");
        MenuItemCompat.setShowAsAction(choseMenuItem, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        refreshSelectBtn();
    }

    private MenuItem choseMenuItem = null;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ImageBean image = mImages.get(mImagePager.getCurrentItem());
        if (image.isSeleted()) {
            if (!mChoseImageListener.onCancelSelect(image)) {
                return true;
            }
            item.setIcon(R.drawable.image_check_off);

            if (!all) {
                mImages.remove(image);
                if (mImages.size() <= 0) {
                    this.getFragmentManager().popBackStack();
                    return true;
                }
                mImagePager.removeAllViews();
                mAdapter.notifyDataSetChanged();
            }
        } else {
            if (!mChoseImageListener.onSelected(image)) {
                return true;
            }
            item.setIcon(R.drawable.image_check_on);

        }
        return true;
    }

    public void swapDatas(ArrayList<ImageBean> images) {
        if (mImages != null) {
            mImages.clear();
            mImages = null;
        }
        mImages = images;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(View container, int position, Object object) {
            ImageView itemView = (ImageView) object;
            ((ViewGroup) container).removeView(itemView);
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ImageBean image = mImages.get(position);
            ImageView itemView = new ImageView(getActivity());
            itemView.setScaleType(ScaleType.CENTER);
            itemView.setImageResource(R.drawable.default_photo);
            ImageLoader.getInstance().displayImage("file://" + image.getPath(), itemView, options);
            ((ViewGroup) container).addView(itemView);
            return itemView;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            refreshSelectBtn();
        }

        @Override
        public int getCount() {
            if (mImages == null) {
                return 0;
            } else {
                return mImages.size();
            }
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        if (arg0 != ViewPager.SCROLL_STATE_IDLE) {
            ImageLoader.getInstance().pause();
        } else {
            ImageLoader.getInstance().resume();
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        refreshSelectBtn();
    }

    private void refreshSelectBtn() {
        ImageBean image = mImages.get(mImagePager.getCurrentItem());
        if (image.isSeleted()) {
            choseMenuItem.setIcon(R.drawable.image_check_on);
        } else {
            choseMenuItem.setIcon(R.drawable.image_check_off);
        }
    }

    public void setChoseImageListener(ChoseImageListener listener) {
        this.mChoseImageListener = listener;
    }
}
