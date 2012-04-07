package org.es.uremote;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

public class TVChannel extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tv_dialer);

	    Gallery gallery = (Gallery) findViewById(R.id.channelGallery);
	    gallery.setAdapter(new ImageAdapter(this));

	    OnItemClickListener t = new OnItemClickListener() {

			@Override
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            Toast.makeText(TVChannel.this, "" + position, Toast.LENGTH_SHORT).show();
	        }

	    };
	    gallery.setOnItemClickListener(t);
	}
	
	public class ImageAdapter extends BaseAdapter {
	    int mGalleryItemBackground;
	    private Context mContext;

	    private Integer[] mImageIds = {
	            R.drawable.android_focused,
	            R.drawable.android_normal,
	            R.drawable.android_pressed
	    };

	    public ImageAdapter(Context c) {
	        mContext = c;
	        TypedArray typedArray = obtainStyledAttributes(R.styleable.StylableChannel);
	        mGalleryItemBackground = typedArray.getResourceId(
	                R.styleable.StylableChannel_android_galleryItemBackground, 0);
	        typedArray.recycle();
	    }

	    @Override
		public int getCount() {
	        return mImageIds.length;
	    }

	    @Override
		public Object getItem(int position) {
	        return position;
	    }

	    @Override
		public long getItemId(int position) {
	        return position;
	    }
	    
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			 ImageView i = new ImageView(mContext);

		        i.setImageResource(mImageIds[position]);
		        i.setLayoutParams(new Gallery.LayoutParams(150, 100));
		        i.setScaleType(ImageView.ScaleType.FIT_XY);
		        i.setBackgroundResource(mGalleryItemBackground);

		        return i;
		}
	}
}
