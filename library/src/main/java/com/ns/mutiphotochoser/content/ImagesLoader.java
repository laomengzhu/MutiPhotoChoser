package com.ns.mutiphotochoser.content;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.content.AsyncTaskLoader;

import com.ns.mutiphotochoser.model.ImageBean;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author xiaolf1
 */
public class ImagesLoader extends AsyncTaskLoader<ArrayList<ImageBean>> {

    private ArrayList<ImageBean> mImages = null;

    /**
     * @param context
     */
    public ImagesLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<ImageBean> loadInBackground() {
        ArrayList<ImageBean> imageList = new ArrayList<ImageBean>();

        Cursor imageCursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID}, null, null, MediaStore.Images.Media._ID);

        if (imageCursor != null && imageCursor.getCount() > 0) {

            while (imageCursor.moveToNext()) {
                ImageBean item = new ImageBean(imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA)), false);
                imageList.add(item);
            }
        }

        if (imageCursor != null) {
            imageCursor.close();
        }

        // show newest photo at beginning of the list
        Collections.reverse(imageList);
        return imageList;
    }

    /* Runs on the UI thread */
    @Override
    public void deliverResult(ArrayList<ImageBean> images) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            if (images != null) {
                images.clear();
                images = null;
            }
            return;
        }
        ArrayList<ImageBean> oldImages = mImages;
        mImages = images;

        if (isStarted()) {
            super.deliverResult(images);
        }

        if (oldImages != null && oldImages != mImages) {
            oldImages.clear();
            oldImages = null;
        }
    }

    @Override
    protected void onStartLoading() {
        if (mImages != null && mImages.size() > 0) {
            deliverResult(mImages);
        }

        if (takeContentChanged() || mImages == null) {
            forceLoad();
        }
    }

    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(ArrayList<ImageBean> images) {
        if (images != null) {
            images.clear();
            images = null;
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        if (mImages != null) {
            mImages.clear();
            mImages = null;
        }
    }
}
