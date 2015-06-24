package com.ns.mutiphotochoser.sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ns.mutiphotochoser.utils.DisplayUtils;

import java.util.ArrayList;

public class ImageGridAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> mDatas = null;

    public ImageGridAdapter(Context c) {
        this.context = c;
        inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        if (mDatas == null) {
            return 0;
        } else {
            return mDatas.size();
        }
    }

    @Override
    public String getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void swapDatas(ArrayList<String> images) {

        if (this.mDatas != null) {
            this.mDatas.clear();
            this.mDatas = null;
        }
        this.mDatas = images;
        notifyDataSetChanged();
    }

    public void addData(ArrayList<String> data) {
        if (data == null) {
            return;
        }
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        }
        mDatas.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ImageView(context);
            ((ImageView) convertView).setScaleType(ImageView.ScaleType.CENTER_CROP);
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(DisplayUtils.dip2px(110, context), DisplayUtils.dip2px(110, context));
            convertView.setLayoutParams(lp);
        }
        ImageLoader.getInstance().displayImage("file://" + mDatas.get(position), (ImageView) convertView);
        return convertView;
    }

    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

}
