package com.ns.mutiphotochoser.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageBean implements Parcelable {

	private String path = null;
	private boolean isSeleted = false;

	public ImageBean(String path, boolean selected) {
		this.path = path;
		this.isSeleted = selected;
	}

	public ImageBean() {
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(path);
		dest.writeInt(isSeleted ? 1 : 0);
	}

	public static final Creator<ImageBean> CREATOR = new Creator<ImageBean>() {

		@Override
		public ImageBean createFromParcel(Parcel source) {
			return new ImageBean(source.readString(), source.readInt() == 1 ? true : false);
		}

		@Override
		public ImageBean[] newArray(int size) {
			return new ImageBean[size];
		}
	};

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the isSeleted
	 */
	public boolean isSeleted() {
		return isSeleted;
	}

	/**
	 * @param isSeleted the isSeleted to set
	 */
	public void setSeleted(boolean isSeleted) {
		this.isSeleted = isSeleted;
	}
}
