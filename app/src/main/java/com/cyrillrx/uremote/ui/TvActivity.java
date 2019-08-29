package com.cyrillrx.uremote.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.cyrillrx.uremote.R;

/**
 * Activity to control the TV.
 *
 * @author Cyril Leroux
 *         Created before first commit (08/04/12).
 */
public class TvActivity extends AppCompatActivity implements OnClickListener {

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
