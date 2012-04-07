package org.es.uremote;

import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class RobotControl extends Activity implements OnClickListener {
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.robot);
	 
        // Click listener pour tous les boutons
//	    View homeView = this.findViewById(R.id.btnHome);
//	    homeView.setOnClickListener(this);
    }
    
	@Override
	public void onClick(View _view) {
		_view.performHapticFeedback(VIRTUAL_KEY);
		switch (_view.getId()) {
//		case R.id.btnHome:
//			Intent i = new Intent(this, Home.class);
//			startActivity(i);
//			break;
//	
		default:
			break;
		}
	}
}
