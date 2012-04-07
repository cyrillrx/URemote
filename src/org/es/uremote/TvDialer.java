package org.es.uremote;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.Toast;

public class TvDialer extends Activity implements OnClickListener {
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

   	 	setContentView(R.layout.tv_dialer);	
        Toast.makeText(TvDialer.this, "orientation type : "+getWindowManager().getDefaultDisplay().getRotation(), Toast.LENGTH_SHORT).show();
        
        // Récupération de l'affichage 
        if(getWindowManager().getDefaultDisplay().getRotation() == 1) {
        	
        	Gallery channelGallery = (Gallery)this.findViewById(R.id.channelGallery);
        	//ImageAdapter imageAdapter = new ImageAdapter(this);
	        //channelGallery.setAdapter(imageAdapter);
	        
	        OnItemClickListener itemClick = new OnItemClickListener() {
	        	@Override
	        	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        		Toast.makeText(TvDialer.this, "" + position, Toast.LENGTH_SHORT).show();
	        	}
	        };
	        channelGallery.setOnItemClickListener(itemClick);
        } 
       
        // Click listener pour tous les boutons
//	    View homeView = this.findViewById(R.id.btnHome);
//	    homeView.setOnClickListener(this);
    }
    
	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.btnHome:
//			Intent i = new Intent(this, Home.class);
//			startActivity(i);
//			break;
//	
//		default:
//			break;
//		}
	}

}
