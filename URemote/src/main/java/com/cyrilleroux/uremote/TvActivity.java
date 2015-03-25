package com.cyrilleroux.uremote;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Activity to control the TV.
 *
 * @author Cyril Leroux
 *         Created before first commit (08/04/12).
 */
public class TvActivity extends ActionBarActivity implements OnClickListener {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_dialer);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // TODO code controls
            default:
                break;
        }
    }

}
