package org.es.uremote;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.Toast;

/**
 * Activity to control the TV.
 *
 * @author Cyril Leroux
 *
 */
public class TvDialer extends Activity implements OnClickListener {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tv_dialer);

		// Do a different treatment depending of the orientation
		if (isLandscape()) {

			Gallery channelGallery = (Gallery)this.findViewById(R.id.channelGallery);

			OnItemClickListener itemClick = new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
					Toast.makeText(TvDialer.this, "" + position, Toast.LENGTH_SHORT).show();
				}
			};
			channelGallery.setOnItemClickListener(itemClick);
		}

	}

	private boolean isLandscape() {
		Point screen = new Point();
		getWindowManager().getDefaultDisplay().getSize(screen);
		return (screen.x > screen.y) ? true : false;
	}

	@Override
	public void onClick(View _v) {
		switch (_v.getId()) {
		// TODO code controls
		default:
			break;
		}
	}

}
