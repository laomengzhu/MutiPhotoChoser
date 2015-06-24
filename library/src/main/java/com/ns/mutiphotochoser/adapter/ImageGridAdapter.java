package com.ns.mutiphotochoser.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ns.mutiphotochoser.R;
import com.ns.mutiphotochoser.model.ImageBean;
import com.ns.mutiphotochoser.utils.ChoseImageListener;

import java.util.ArrayList;

public class ImageGridAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<ImageBean> mDatas = null;
    private DisplayImageOptions options = null;
    private ChoseImageListener mChoseImageListener = null;

    public ImageGridAdapter(Context c, DisplayImageOptions options) {
        inflater = LayoutInflater.from(c);
        this.options = options;
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
    public ImageBean getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void swapDatas(ArrayList<ImageBean> images) {

        if (this.mDatas != null) {
            this.mDatas.clear();
            this.mDatas = null;
        }
        this.mDatas = images;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.image_grid_item, null);
            holder = new ViewHolder();
            holder.imgQueue = (ImageView) convertView.findViewById(R.id.imgQueue);
            holder.imgQueueMultiSelected = (ImageView) convertView.findViewById(R.id.cb_select_tag);
            holder.layerView = convertView.findViewById(R.id.v_selected_frame);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.imgQueueMultiSelected.setOnClickListener(null);
        }

        try {

            ImageLoader.getInstance().displayImage("file://" + mDatas.get(position).getPath(), holder.imgQueue, options);
            if (mDatas.get(position).isSeleted()) {
                holder.imgQueueMultiSelected.setImageResource(R.drawable.image_check_on);
                holder.layerView.setBackgroundResource(R.color.image_selected_color);
            } else {
                holder.imgQueueMultiSelected.setImageResource(R.drawable.image_check_off);
                holder.layerView.setBackgroundResource(R.color.transparent);
            }

            holder.imgQueueMultiSelected.setOnClickListener(new MultiSelectListener(position));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public class ViewHolder {
        ImageView imgQueue;
        ImageView imgQueueMultiSelected;
        View layerView;
    }

    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    private class MultiSelectListener implements OnClickListener {

        private int position = -1;

        public MultiSelectListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (mChoseImageListener == null) {
                return;
            }
            ImageBean image = getItem(position);
            View layerView = null;
            if (v.getParent() instanceof ViewGroup) {
                layerView = (View) v.getParent();
            }
            if (image.isSeleted()) {
                if (!mChoseImageListener.onCancelSelect(getItem(position))) {
                    return;
                }
                ((ImageView) v).setImageResource(R.drawable.image_check_off);
                if (layerView != null) {
                    layerView.setBackgroundResource(R.color.transparent);
                }

            } else {
                if (!mChoseImageListener.onSelected(getItem(position))) {
                    return;
                }
                ((ImageView) v).setImageResource(R.drawable.image_check_on);
                if (layerView != null) {
                    layerView.setBackgroundResource(R.color.image_selected_color);
                }

            }
        }

    }

    public void setChoseImageListener(ChoseImageListener listener) {
        this.mChoseImageListener = listener;
    }
}
