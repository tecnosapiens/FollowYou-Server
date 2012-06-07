package org.servidor.followyou;

import java.io.IOException;
import java.util.List;
import java.util.Locale;



import android.content.BroadcastReceiver;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.widget.Toast;


public class FollowYouActivity extends MapActivity
{
	class MyOverlay extends Overlay 
	{
    	GeoPoint point;
    	
    	public MyOverlay(GeoPoint point) {
    		super();
    		this.point = point;
    	}
    	
        @Override
        public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
            super.draw(canvas, mapView, shadow);                   
 
            Point scrnPoint = new Point();
            mapView.getProjection().toPixels(this.point, scrnPoint);
 
            Bitmap marker = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            canvas.drawBitmap(marker,
            		scrnPoint.x - marker.getWidth() / 2,
            		scrnPoint.y - marker.getHeight() / 2, null);
            return true;
        }
	}//Fin clase Overlay	
	
	private MapView mapa = null;
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //get control map reference
        
        mapa = (MapView) findViewById(R.id.mapa);
        
        //Show map control zoom over the map
        mapa.setBuiltInZoomControls(true);
        
//        Bundle extras = this.getIntent().getExtras();
//        String mensaje = extras.getString("mensaje_recibido");
//        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
        
        //procesarMensajeFollowMe(extras.getString("mensaje_recibido"));
        //Toast.makeText(getApplicationContext(), extras.getString("mesaje_recibido"), Toast.LENGTH_SHORT).show();

        
    }
    
    @Override
    protected boolean isRouteDisplayed()
    {
    	return false;
    
    }
    
    protected void updateLocation(Location location){
		//mapa = (MapView) findViewById(R.id.mapview);
        MapController mapController = mapa.getController();
        GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
        mapController.animateTo(point);        
        mapController.setZoom(15);
        
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geoCoder.getFromLocation(
                point.getLatitudeE6()  / 1E6, 
                point.getLongitudeE6() / 1E6, 1);

            String address = "";
            if (addresses.size() > 0) {
                for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++)
                   address += addresses.get(0).getAddressLine(i) + "\n";
            }

            Toast.makeText(getBaseContext(), address, Toast.LENGTH_SHORT).show();
        }
        catch (IOException e) {                
            e.printStackTrace();
        }           

        
        List<Overlay> mapOverlays = mapa.getOverlays();
        MyOverlay marker = new MyOverlay(point);
        mapOverlays.add(marker);  
        mapa.invalidate();		
	}
    
    public void procesarMensajeFollowMe(String mensaje)
    {
    	
    	String[] mensajeParseado = mensaje.split(",");
    	
    	Location puntoGeo = new Location("gps");
        double latitud =  Double.parseDouble(mensajeParseado[2]);
        double longitud = Double.parseDouble(mensajeParseado[3]);
        
       
        puntoGeo.setLatitude(latitud);
        puntoGeo.setLongitude(longitud);
        //updateLocation(puntoGeo);
        
       
        
        
    	
    }
   
}//Fin de clase