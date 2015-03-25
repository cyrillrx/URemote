package com.cyrilleroux.uremote;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;

/**
 * Activity to control robots and electronic devices.
 *
 * @author Cyril Leroux
 *         Created before first commit (08/04/12).
 */
public class RobotActivity extends ActionBarActivity implements OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO Replace layout
        // TODO Code the activity behavior
        setContentView(R.layout.activity_robot);

        // Set up a click listener for each button
        findViewById(R.id.robotControlLeft).setOnClickListener(this);
        findViewById(R.id.robotControlUp).setOnClickListener(this);
        findViewById(R.id.robotControlRight).setOnClickListener(this);
        findViewById(R.id.robotControlDown).setOnClickListener(this);
        findViewById(R.id.robotControl1).setOnClickListener(this);
        findViewById(R.id.robotControl2).setOnClickListener(this);
        findViewById(R.id.robotControl3).setOnClickListener(this);
        findViewById(R.id.robotControl4).setOnClickListener(this);
        findViewById(R.id.robotControl5).setOnClickListener(this);
        findViewById(R.id.robotControl6).setOnClickListener(this);
    }

    private void fireToast(String message) {
        Toast.makeText(RobotActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        view.performHapticFeedback(VIRTUAL_KEY);
        switch (view.getId()) {

            case R.id.robotControlLeft:
                // TODO code the button behavior
                fireToast("Todo : code robotControlLeft");
                break;

            case R.id.robotControlUp:
                // TODO code the button behavior
                fireToast("Todo : code robotControlUp");
                break;

            case R.id.robotControlRight:
                // TODO code the button behavior
                fireToast("To todo : code robotControlRight");
                break;

            case R.id.robotControlDown:
                // TODO code the button behavior
                fireToast("Todo : code robotControlDown");
                break;

            case R.id.robotControl1:
                // TODO code the button behavior
                fireToast("Todo : code robotControl1");
                break;

            case R.id.robotControl2:
                // TODO code the button behavior
                fireToast("Todo : code robotControl2");
                break;

            case R.id.robotControl3:
                // TODO code the button behavior
                fireToast("Todo : code robotControl3");
                break;

            case R.id.robotControl4:
                // TODO code the button behavior
                fireToast("Todo : code robotControl4");
                break;

            case R.id.robotControl5:
                // TODO code the button behavior
                fireToast("Todo : code robotControl5");
                break;

            case R.id.robotControl6:
                // TODO code the button behavior
                fireToast("Todo : code robotControl6");
                break;

            default:
                break;
        }
    }
}
