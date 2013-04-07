package com.webresponsive.surfapps.surfstoked.gallery;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.webresponsive.surfapps.surfstoked.R;

public class GalleryImageAdapter extends BaseAdapter
{
    private int mGalleryItemBackground;

    private Context mContext;

    public Bitmap[] bitmaps;

    public GalleryImageAdapter(Context c)
    {
	mContext = c;
	TypedArray a = c.obtainStyledAttributes(R.styleable.default_gallery);

	mGalleryItemBackground = a.getResourceId(R.styleable.default_gallery_android_galleryItemBackground, 0);
	a.recycle();
    }

    public int getCount()
    {
	return bitmaps.length;
    }

    public Object getItem(int position)
    {
	return position;
    }

    public long getItemId(int position)
    {
	return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
	ImageView i = new ImageView(mContext);

	i.setImageBitmap(bitmaps[position]);

	i.setLayoutParams(new Gallery.LayoutParams(377, 265));
	i.setScaleType(ImageView.ScaleType.FIT_XY);
	i.setBackgroundResource(mGalleryItemBackground);

	return i;
    }
}