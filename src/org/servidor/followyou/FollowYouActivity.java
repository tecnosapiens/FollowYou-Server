package org.servidor.followyou;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FollowYouActivity extends MapActivity
{
	private MapView mapa = null;
	private Button btnSatelite = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //get control map reference
        
        mapa = (MapView) findViewById(R.id.mapa);
        
        //Show map control zoom over the map
        mapa.setBuiltInZoomControls(true);
        
        
        
        btnSatelite = (Button)findViewById(R.id.BtnSatelite);
        
        btnSatelite.setOnClickListener(new OnClickListener() 
        {
        	
        	public void onClick(View arg0)
        	{
	        	if(mapa.isSatellite())
	        	mapa.setSatellite(false);
	        	else
	        	mapa.setSatellite(true);
        	}
       	});
        
    }
    
    @Override
    protected boolean isRouteDisplayed()
    {
    	return false;
    
    }
}//Fin de clase