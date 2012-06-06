package org.servidor.followyou;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import android.os.Bundle;

public class FollowYouActivity extends MapActivity
{
	private MapView mapa = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //get control map reference
        
        mapa = (MapView) findViewById(R.id.mapa);
        
        //Show map control zoom over the map
        mapa.setBuiltInZoomControls(true);
        
    }
    
    @Override
    protected boolean isRouteDisplayed()
    {
    	return false;
    
    }
}//Fin de clase