package org.es.uremote;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;

/**
 * Activity to control robots and electronic devices.
 *
 * @author Cyril Leroux
 */
public class RobotControl extends Activity implements OnClickListener {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Replace layout
		// TODO Code the activity behavior
		setContentView(R.layout.activity_robot);

		// Set up a click listener for each button
		((ImageButton) findViewById(R.id.robotControlLeft)).setOnClickListener(this);
		((ImageButton) findViewById(R.id.robotControlUp)).setOnClickListener(this);
		((ImageButton) findViewById(R.id.robotControlRight)).setOnClickListener(this);
		((ImageButton) findViewById(R.id.robotControlDown)).setOnClickListener(this);
		((Button) findViewById(R.id.robotControl1)).setOnClickListener(this);
		((Button) findViewById(R.id.robotControl2)).setOnClickListener(this);
		((Button) findViewById(R.id.robotControl3)).setOnClickListener(this);
		((Button) findViewById(R.id.robotControl4)).setOnClickListener(this);
		((Button) findViewById(R.id.robotControl5)).setOnClickListener(this);
		((Button) findViewById(R.id.robotControl6)).setOnClickListener(this);
	}

	private void fireToast(String message) {
		Toast.makeText(RobotControl.this, message, Toast.LENGTH_SHORT).show();
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
