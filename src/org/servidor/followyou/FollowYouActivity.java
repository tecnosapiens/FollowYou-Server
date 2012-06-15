package org.servidor.followyou;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;



import android.content.BroadcastReceiver;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import android.os.Bundle;
import android.text.format.Time;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;
import android.util.Log;


public class FollowYouActivity extends MapActivity
{
	class MyOverlay extends Overlay 
	{
    	GeoPoint point;
    	String[] informacion;
    	
    	public MyOverlay(GeoPoint point, String[] info)
    	{
    		super();
    		this.point = point;
    		this.informacion = info;
    	}
    	
        @Override
        public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
            super.draw(canvas, mapView, shadow);   
            
          //Definimos el pincel de dibujo
            Paint p = new Paint();
            p.setColor(Color.RED);
 
            Point scrnPoint = new Point();
            mapView.getProjection().toPixels(this.point, scrnPoint);
 
            Bitmap marker = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            canvas.drawBitmap(marker,
            		scrnPoint.x - marker.getWidth() / 2,
            		scrnPoint.y - marker.getHeight() / 2, null);
            
         
            
            canvas.drawText(informacion[0], 
            		(scrnPoint.x + marker.getWidth() / 2),
            		(scrnPoint.y)-10, p);
            canvas.drawText(informacion[1], 
            		(scrnPoint.x + marker.getWidth() / 2),
            		(scrnPoint.y), p);
            canvas.drawText(informacion[2], 
            		(scrnPoint.x + marker.getWidth() / 2),
            		(scrnPoint.y) + 10, p);
            return true;
        }
	}//Fin clase Overlay	
	
	
	private MapView mapa = null;
	private BroadcastReceiver mReceiver;
	
	double latitud; 
    double longitud;
    String[] informacion;
	
	
	private TimerTask mTimerTask;	
	private int timeCounter;
	Timer t = new Timer();
	
	String segundos = "00";
	String minutos = "00";
	String hora = "00";
	
	int edadSegundos = 0;
	int edadMinutos = 0;
	int edadHora = 0;
	
	
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
        
        Time now = new Time();
    	now.setToNow();
        
        informacion = new String[3];
        informacion[0] = "ID";
        informacion[1] = now.format2445();
        informacion[2] = "provider";
        		
        //punto inicial en el mapa
        setPointoverMap(19.1945, -96.135607, informacion);
        
        Log.i("FollowYou", "Inicio");
        

        
    }
    
    @Override
    protected boolean isRouteDisplayed()
    {
    	return false;
    
    }
    
    
    
    protected void updateLocation(Location location, String[] informacionPos){
		//mapa = (MapView) findViewById(R.id.mapview);
        MapController mapController = mapa.getController();
        GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
        mapController.animateTo(point);        
        mapController.setZoom(10);
        
        int zoomActual = mapa.getZoomLevel();
        
        for(int i=zoomActual; i<10; i++)
        {
        	mapController.zoomIn();
        }
        
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
        MyOverlay marker = new MyOverlay(point, informacionPos);
        mapOverlays.add(marker);  
        mapa.invalidate();	
        //mapa.postInvalidate();
	}
    
    public void procesarMensajeFollowMe(String mensaje)
    {
    	Log.i("FollowYou", "Llego el mensaje");
    	
    	if(mTimerTask!=null)
    	{
    	      Log.d("TIMER", "timer canceled");
    	      mTimerTask.cancel();
    	}
    	      
    	Time now = new Time();
    	now.setToNow();
    	
    	
    	
    	String[] mensajeParseado = mensaje.split(",");
    	
    	
        latitud =  Double.parseDouble(mensajeParseado[3]);
        longitud = Double.parseDouble(mensajeParseado[4]);
        Log.i("FollowYou", mensajeParseado[3] + " - " + mensajeParseado[4] );
        
        
        informacion[0] = "ID";
        informacion[1] = "00:00:00";//now.format2445();
        informacion[2] = "provider";
       
        //setPointoverMap(latitud, longitud, informacion);
        doTimerTask();
       
        
        
    	
    }
    
    private void setPointoverMap(double latitud, double longitud, String[] infoPos)
    {
    	Location puntoGeo = new Location("gps");
    	
    	puntoGeo.setLatitude(latitud);
        puntoGeo.setLongitude(longitud);
    
        updateLocation(puntoGeo, infoPos);
        
    }
    
    
    
    
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
 
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");
 
        mReceiver = new BroadcastReceiver() {
 
            @Override
            public void onReceive(Context context, Intent intent) {
                //extract our message from intent
                String msg_for_me = intent.getStringExtra("mensaje_recibido");
                procesarMensajeFollowMe(msg_for_me);
                //log our message value
                Log.i("FollowYou", msg_for_me);
 
            }
        };
        //registering our receiver
        this.registerReceiver(mReceiver, intentFilter);
    }
 
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        //unregister our receiver
        this.unregisterReceiver(this.mReceiver);
    }
    
    
    public void doTimerTask()//double latitud, double longitud, String[] info)
    {
    	timeCounter = 0;
    	segundos = "00";
    	minutos = "00";
    	hora = "00";
    	
    	edadSegundos = 0;
    	edadMinutos = 0;
    	edadHora = 0;

    	mTimerTask = new TimerTask() 
    	{
    		
        	
    		@Override
			public void run()
    		{
    			
    			timeCounter++;
    			if(timeCounter == 60)
    			{
    				edadSegundos = 0;
    				edadMinutos = edadMinutos + 1;
    				
    				if(edadMinutos == 60)
    				{
    					edadMinutos = 0;
    					edadHora = edadHora + 1;
    				}
    			}
    			else
    			{
    				edadSegundos = edadSegundos + 1;
    			}
    			
    			if(edadHora < 10)
    			{
    				hora = "0" + Integer.toString(edadHora);
    			}
    			else
    			{
    				hora = Integer.toString(edadHora);
    				
    			}
    			
    			if(edadMinutos < 10)
    			{
    				minutos = "0" + Integer.toString(edadMinutos);
    			}
    			else
    			{
    				minutos = "0" + Integer.toString(edadMinutos);
    				
    			}
    			
    			if(edadSegundos < 10)
    			{
    				segundos = "0" + Integer.toString(edadSegundos);
    			}
    			else
    			{
    				segundos = Integer.toString(edadSegundos);
    			}
    			
    			informacion[1] = hora + ":" + minutos + ":" + segundos; 
     			
    			setPointoverMap(latitud, longitud, informacion);
    			Log.i("FollowYou", "TimerTask run: " + informacion[1]);
    		}

    	};

    	// public void schedule (TimerTask task, long delay, long period) 
    	t.schedule(mTimerTask, 1000);  // 

    }
   
}//Fin de clase