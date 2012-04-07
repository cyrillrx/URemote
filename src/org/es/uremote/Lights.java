package org.es.uremote;
import android.app.Activity;
import android.os.Bundle;


public class Lights extends Activity {
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.robot);
	    
        // Click listener pour tous les boutons
//	    View homeView = this.findViewById(R.id.btnHome);
//	    homeView.setOnClickListener(this);
    }
}
