package org.servidor.followyou;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import android.content.BroadcastReceiver;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import android.os.Bundle;
import android.os.Handler;
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
            p.setColor(Color.BLUE);
 
            Point scrnPoint = new Point();
            mapView.getProjection().toPixels(this.point, scrnPoint);
            
               	Bitmap marker = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                canvas.drawBitmap(marker,
                		scrnPoint.x - marker.getWidth() / 2,
                		scrnPoint.y - marker.getHeight() / 2, null);
                
             
                // imprime el ID del Movil
                canvas.drawText(informacion[0], 
                		(scrnPoint.x + marker.getWidth() / 2),
                		(scrnPoint.y)-10, p);
                // imprime el Tipo de mensaje
                canvas.drawText(informacion[1], 
                		(scrnPoint.x + marker.getWidth() / 2),
                		(scrnPoint.y), p);
                // imprime la hora de creacion del mensaje en el cliente
                canvas.drawText(informacion[2], 
                		(scrnPoint.x + marker.getWidth() / 2),
                		(scrnPoint.y) + 10, p);
                // imprime la edad de actualizacion del movil
                canvas.drawText(informacion[7], 
                		(scrnPoint.x + marker.getWidth() / 2),
                		(scrnPoint.y) + 20, p);
            
 
            
            return true;
        }
	}//Fin clase Overlay	
	
	
	private MapView mapa = null;
	private BroadcastReceiver mReceiver;
	
	double latitud; 
    double longitud;
    String[] infoTemp;
	
	
	Timer t = new Timer();
	private Handler mHandler = new Handler();
	String edadMovil;
	
	String segundos = "00";
	String minutos = "00";
	String hora = "00";
	
	int edadSegundos = 0;
	int edadMinutos = 0;
	int edadHora = 0;
	
	GeoPoint puntoGeoMovil;
	
	
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
        edadMovil = "00:00:00";
        
      
    	infoTemp = new String[8];
    	infoTemp[0] = "ID";
    	infoTemp[1] = "tipo";
    	infoTemp[2] = "hora";
    	infoTemp[3] = "fecha";
    	infoTemp[4] = "latitud";
    	infoTemp[5] = "longitud";
    	infoTemp[6] = "provider";
    	infoTemp[7] = "edadMovil";
    	
    	puntoGeoMovil = new GeoPoint((int) (19.1945 * 1E6), (int) (-96.135607 * 1E6));
        		
        //punto inicial en el mapa
        setPointoverMap(19.1945, -96.135607, infoTemp);
        
//        List<Overlay> mapOverlays = mapa.getOverlays();
//        MyOverlay marker = new MyOverlay(pointTemp, infoTemp);
//        mapOverlays.add(marker); 
//        mapa.invalidate();
        
        Log.i("FollowYou", "Inicio");
        

        
    }
    
    @Override
    protected boolean isRouteDisplayed()
    {
    	return false;
    
    }
    
    
    
    protected void updateLocation(Location location, String[] informacionPos)
    {
		//mapa = (MapView) findViewById(R.id.mapview);
        MapController mapController = mapa.getController();
        GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
        puntoGeoMovil = point;
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
        mapOverlays.clear();  // esta instruccion borra todos los objetos del mapa sin dejar historia
        mapOverlays.add(marker);
        mapa.postInvalidate();
        //mapa.invalidate();	
       
	}
    
    protected void updateLabelMovilLocation(GeoPoint point, String[] informacionPos)
    {
		//mapa = (MapView) findViewById(R.id.mapview);
        MapController mapController = mapa.getController();
        //GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
//        mapController.animateTo(point);        
//        mapController.setZoom(10);
//        
//        int zoomActual = mapa.getZoomLevel();
//        
//        for(int i=zoomActual; i<10; i++)
//        {
//        	mapController.zoomIn();
//        }
        
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
        mapOverlays.clear();  // esta instruccion borra todos los objetos del mapa sin dejar historia
        mapOverlays.add(marker);
        mapa.postInvalidate();
        //mapa.invalidate();	
       
	}
    public void procesarMensajeFollowMe(String mensaje)
    {
    	Log.i("FollowYou", "Llego el mensaje -- procesarMensajeFollowMe");
    	
    	
    	      
    	Time now = new Time();
    	now.setToNow();
    	
    	
    	
    	String[] mensajeParseado = mensaje.split(",");
    	infoTemp [0] = mensajeParseado[0];// ---->  $+id
    	infoTemp [1] = mensajeParseado[1];// ---->  tipo de mensaje SOS o OK
    	infoTemp [2] =  mensajeParseado[2];// ---->  Hora de creacion del mensaje
    	infoTemp [3] =  mensajeParseado[3];// ---->  Fecha de creacion del Mensaje
    	infoTemp [4] = mensajeParseado[4];// ---->  Latitud
    	infoTemp [5] = mensajeParseado[5];//---->  Longitud
    	infoTemp [6] =  mensajeParseado[6];// ---->  Proveedor Servicio
		infoTemp[7] = "00:00:00"; 			// ---> Edad del movil desde su ultima actualizacion
    	    	
        latitud =  Double.parseDouble(infoTemp[4]);
        longitud = Double.parseDouble(infoTemp[5]);
        
        /*  DATOS EN EL MENSAJE PARSEADO
           mensajeParseado[0] ---->  $+id
		   mensajeParseado[1] ---->  tipo de mensaje SOS o OK
		   mensajeParseado[2] ---->  Hora de creacion del mensaje
		   mensajeParseado[3] ---->  Fecha de creacion del Mensaje
		   mensajeParseado[4] ---->  Latitud
		   mensajeParseado[5] ---->  Longitud
		   mensajeParseado[6] ---->  Proveedor Servicio
		   
		   
	   */	   
        Log.i("FollowYou", " Mensaje Parseado: [" + mensajeParseado[0] + "] - [" 
        					   + mensajeParseado[1] + "] - [" 
        					   + mensajeParseado[2] + "] - ["
        					   + mensajeParseado[3] + "] - ["
        					   + mensajeParseado[4] + "] - ["
        					   + mensajeParseado[5] + "] - ["
        					   + mensajeParseado[6] + "]");
        
        Log.i("FollowYou", "Var infoTemp: [" + infoTemp[0] + "] - [" 
				   + infoTemp[1] + "] - [" 
				   + infoTemp[2] + "] - ["
				   + infoTemp[3] + "] - ["
				   + infoTemp[4] + "] - ["
				   + infoTemp[5] + "] - ["
				   + infoTemp[6] + "] - ["
				   + infoTemp[7] + "]");
        
//        informacion[0] = "ID";
//        informacion[1] = "00:00:00";//now.format2445();
//        informacion[2] = "provider";
        
        setPointoverMap(latitud, longitud, infoTemp);
        
        Log.i("FollowYou", puntoGeoMovil.toString());
       
        if(mHandler != null)
    	{
    	      Log.d("FolowYou", "timer canceled");
    	      mHandler.removeCallbacks(mMuestraMensaje);
    	      
    	    	segundos = "00";
    	    	minutos = "00";
    	    	hora = "00";
    	    	
    	    	edadSegundos = 0;
    	    	edadMinutos = 0;
    	    	edadHora = 0;
    	      
    	    	mHandler.postDelayed(mMuestraMensaje, 1000);
    	    	Log.i("FollowYou", "dentro del timer task");
    	      
    	}
        
      
       
        
        
        
    	
    }
    
    private void setPointoverMap(double latitud, double longitud, String[] infoPos)
    {
    	Location puntoGeo = new Location("gps"); // la etiqueta GPS es solo para que acepte la construccion el obj Location
    	
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
        
        if(mHandler != null)
    	{
    	      Log.d("FolowYou", "timer canceled");
    	      mHandler.removeCallbacks(mMuestraMensaje);
    	      
    	      
    	}
    }
    
    
   
    
    private Runnable mMuestraMensaje = new Runnable()
    {
    	
    	
        public void run()
        {
        	
        	
        	//timeCounter++;
			if(edadSegundos == 59)
			{
				edadSegundos = 0;
				edadMinutos = edadMinutos + 1;
				
				if(edadMinutos == 59)
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
			
			infoTemp[7] = hora + ":" + minutos + ":" + segundos; 
			
 			
			//setPointoverMap(latitud, longitud, informacion);
			Log.i("FollowYou", "TimerTask run: " + infoTemp[7]);
        
        	updateLabelMovilLocation(puntoGeoMovil, infoTemp);
        	
           mHandler.removeCallbacks(mMuestraMensaje);
           mHandler.postDelayed(this, 1000);
        }
      };
   
}//Fin de clase