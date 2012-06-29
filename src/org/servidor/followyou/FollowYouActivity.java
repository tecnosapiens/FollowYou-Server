package org.servidor.followyou;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.content.BroadcastReceiver;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import android.os.Bundle;
import android.os.Handler;
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
        public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when)
        {
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
	
	
	//Timer t = new Timer();
    Thread hilo;
	private Handler mHandler = new Handler();
	String edadMovil;
	
	String segundos = "00";
	String minutos = "00";
	String hora = "00";
	
	int edadSegundos = 0;
	int edadMinutos = 0;
	int edadHora = 0;
	
	GeoPoint puntoGeoMovil;
	
	Map<String, String[]> listaMoviles = new HashMap<String, String[]>();
	Map<String, String[]> listaMovilesTemp = new HashMap<String, String[]>();
	
	
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
        
      
    	infoTemp = new String[9];
    	infoTemp[0] = "ID";
    	infoTemp[1] = "tipo";
    	infoTemp[2] = "hora";
    	infoTemp[3] = "fecha";
    	infoTemp[4] = "latitud";
    	infoTemp[5] = "longitud";
    	infoTemp[6] = "provider";
    	infoTemp[7] = "edadMovil";
    	infoTemp[8] = "0";
    	
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
    
    
    
    //protected void updateLocation(Location location, String[] informacionPos)
    protected void updateLocation(String[] informacionPos)
    {
    	 MapController mapController = mapa.getController();
    	
    	latitud =  Double.parseDouble(informacionPos[4]);
        longitud = Double.parseDouble(informacionPos[5]);
    	 		
    	
       
        //GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
        GeoPoint point = new GeoPoint((int) (latitud * 1E6), (int) (longitud * 1E6));
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
       // mapOverlays.clear();  // esta instruccion borra todos los objetos del mapa sin dejar historia
        mapOverlays.add(marker);
        mapa.postInvalidate();
        //mapa.invalidate();	
       
	}
    
    protected void updateLabelMovilLocation(String[] informacionPos)
    {
    	latitud =  Double.parseDouble(informacionPos[4]);
        longitud = Double.parseDouble(informacionPos[5]);
    	 		
    	
       
        //GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
        GeoPoint pointLabel = new GeoPoint((int) (latitud * 1E6), (int) (longitud * 1E6));
        
		//mapa = (MapView) findViewById(R.id.mapview);
//        MapController mapController = mapa.getController();
//        //GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
//       mapController.animateTo(pointLabel);        
//       mapController.setZoom(10);
////        
//        int zoomActual = mapa.getZoomLevel();
//        
//        for(int i=zoomActual; i<10; i++)
//        {
//        	mapController.zoomIn();
//        }
        
//        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
//
//        try {
//            List<Address> addresses = geoCoder.getFromLocation(
//                point.getLatitudeE6()  / 1E6, 
//                point.getLongitudeE6() / 1E6, 1);
//
//            String address = "";
//            if (addresses.size() > 0) {
//                for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++)
//                   address += addresses.get(0).getAddressLine(i) + "\n";
//            }
//
//            Toast.makeText(getBaseContext(), address, Toast.LENGTH_SHORT).show();
//        }
//        catch (IOException e) {                
//            e.printStackTrace();
//        }           

                
        List<Overlay> mapOverlays = mapa.getOverlays();
        MyOverlay marker = new MyOverlay(pointLabel, informacionPos);
        mapOverlays.clear();  // esta instruccion borra todos los objetos del mapa sin dejar historia
        mapOverlays.add(marker);
        //mapa.postInvalidate();
        //mapa.invalidate();	
       
	}
    
    protected void updateLabelListaMovilLocation()
    {
    	List<Overlay> mapOverlays = mapa.getOverlays();
    	mapOverlays.clear();
    	
    	for( Iterator<String> it = listaMoviles.keySet().iterator(); it.hasNext();)
        {
            String s = it.next();
            String[] infoMovil = listaMoviles.get(s);
            
            latitud =  Double.parseDouble(infoMovil[4]);
            longitud = Double.parseDouble(infoMovil[5]);
            
            GeoPoint pointLabel = new GeoPoint((int) (latitud * 1E6), (int) (longitud * 1E6));
           
            MyOverlay marker = new MyOverlay(pointLabel, infoMovil);
            mapOverlays.add(marker);
            
            listaMoviles.put(infoMovil[0], infoMovil);
            	
            }
    	
    	mapa.postInvalidate();
        //mapa.invalidate();	
    	    	
    	
       
	}
    
    public void procesarMensajeFollowMe(String mensaje)
    {
    	
    	String[] mensajeParseado = mensaje.split(",");
    	
    	Log.i("FollowYou", "Llego el mensaje -- procesarMensajeFollowMe: " + mensajeParseado[0]);
    	
    	if(listaMoviles.containsKey(mensajeParseado[0]))
    	{
    		String[] infoMovil = listaMoviles.get(mensajeParseado[0]);
    		
    		//infoMovil [0] = mensajeParseado[0];// ---->  $+id
        	infoMovil [1] = mensajeParseado[1];// ---->  tipo de mensaje SOS o OK
        	infoMovil [2] =  mensajeParseado[2];// ---->  Hora de creacion del mensaje
        	infoMovil [3] =  mensajeParseado[3];// ---->  Fecha de creacion del Mensaje
        	infoMovil [4] = mensajeParseado[4];// ---->  Latitud
        	infoMovil [5] = mensajeParseado[5];//---->  Longitud
        	infoMovil [6] =  mensajeParseado[6];// ---->  Proveedor Servicio
        	infoMovil[7] = "00:00:00"; 			// ---> Edad del movil desde su ultima actualizacion
        	infoMovil[8] = "1";					// 1 = movil actualizado --- 0 = movil sin actualizacion 
        	listaMoviles.put(infoMovil[0], infoMovil);
        	
        	Log.i("FollowYou", "ListaMoviles: " + Integer.toString(listaMoviles.size()));
        	
        	updateLocation(infoMovil);
    	}
    	else
    	{
    		String[] infoMovil = new String[9];
        	infoMovil [0] = mensajeParseado[0];// ---->  $+id
        	infoMovil [1] = mensajeParseado[1];// ---->  tipo de mensaje SOS o OK
        	infoMovil [2] =  mensajeParseado[2];// ---->  Hora de creacion del mensaje
        	infoMovil [3] =  mensajeParseado[3];// ---->  Fecha de creacion del Mensaje
        	infoMovil [4] = mensajeParseado[4];// ---->  Latitud
        	infoMovil [5] = mensajeParseado[5];//---->  Longitud
        	infoMovil [6] =  mensajeParseado[6];// ---->  Proveedor Servicio
        	infoMovil[7] = "00:00:00"; 			// ---> Edad del movil desde su ultima actualizacion
        	infoMovil[8] = "1";
        
    		listaMoviles.put(infoMovil[0], infoMovil);
    		Log.i("FollowYou", "ListaMoviles: " + Integer.toString(listaMoviles.size()));
    		
    		updateLocation(infoMovil);
    		
    	}
    	
    	
        
        
        /*  DATOS EN EL MENSAJE PARSEADO
           mensajeParseado[0] ---->  $+id
		   mensajeParseado[1] ---->  tipo de mensaje SOS o OK
		   mensajeParseado[2] ---->  Hora de creacion del mensaje
		   mensajeParseado[3] ---->  Fecha de creacion del Mensaje
		   mensajeParseado[4] ---->  Latitud
		   mensajeParseado[5] ---->  Longitud
		   mensajeParseado[6] ---->  Proveedor Servicio
		   
		   
	   */	   
//        Log.i("FollowYou", " Mensaje Parseado: [" + mensajeParseado[0] + "] - [" 
//        					   + mensajeParseado[1] + "] - [" 
//        					   + mensajeParseado[2] + "] - ["
//        					   + mensajeParseado[3] + "] - ["
//        					   + mensajeParseado[4] + "] - ["
//        					   + mensajeParseado[5] + "] - ["
//        					   + mensajeParseado[6] + "]");
//        
//        Log.i("FollowYou", "Var infoTemp: [" + infoTemp[0] + "] - [" 
//				   + infoTemp[1] + "] - [" 
//				   + infoTemp[2] + "] - ["
//				   + infoTemp[3] + "] - ["
//				   + infoTemp[4] + "] - ["
//				   + infoTemp[5] + "] - ["
//				   + infoTemp[6] + "] - ["
//				   + infoTemp[7] + "]");
//        
//        informacion[0] = "ID";
//        informacion[1] = "00:00:00";//now.format2445();
//        informacion[2] = "provider";
        
        //setPointoverMap(latitud, longitud, infoTemp);
        
        Log.i("FollowYou", "Antes de llamar al Hilo");
        
//        if(hilo.isAlive())
//	      {
//	    	  hilo.interrupt();
//	    	  Log.i("FolowYou", "se interrumpio el hilo");
//	    	  
//	    	  segundos = "00";
//  	    	minutos = "00";
//  	    	hora = "00";
//  	    	
//  	    	edadSegundos = 0;
//  	    	edadMinutos = 0;
//  	    	edadHora = 0;
//  	      
//  	    	
//  	    	
//  	    	//mHandler.postDelayed(mMuestraMensaje, 1000);
//  	    	threadActualizaEtiqueta();
//  	    	Log.i("FollowYou", "dentro del hilo task");
//	      }
       
        if(mHandler != null)
    	{
    	      Log.i("FolowYou", "timer canceled");
    	      mHandler.removeCallbacks(mMuestraMensaje);
    	    
    	      
    	      Log.i("FolowYou", "se borro la pila del handler");
    	          	      
    	    	segundos = "00";
    	    	minutos = "00";
    	    	hora = "00";
    	    	
    	    	edadSegundos = 0;
    	    	edadMinutos = 0;
    	    	edadHora = 0;
    	      
    	    	
    	    	
    	    	mHandler.postDelayed(mMuestraMensaje, 1000);
    	    	//threadActualizaEtiqueta();
    	    	Log.i("FollowYou", "dentro del hilo task");
    	      
    	}
        
      
       
        
        
        
    	
    }
    
    private void setPointoverMap(double latitud, double longitud, String[] infoPos)
    {
    	Location puntoGeo = new Location("gps"); // la etiqueta GPS es solo para que acepte la construccion el obj Location
    	
    	puntoGeo.setLatitude(latitud);
        puntoGeo.setLongitude(longitud);
    
        //updateLocation(puntoGeo, infoPos);
        
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
//        if(hilo.isAlive())
//	      {
//	    	  hilo.interrupt();
//	      }
        if(mHandler != null)
    	{
    	      Log.d("FolowYou", "timer canceled");
    	      mHandler.removeCallbacks(mMuestraMensaje);
    	      
    	     // mHandler.removeCallbacks(ejecutarAccionConteoEdadMovil);
    	      //threadActualizaEtiqueta();
    	     
    	      
    	      
    	}
    }
    
    
   
    
    private Runnable mMuestraMensaje = new Runnable()
    {
    	
    	
        public void run()
        {
        	
        	
        	//timeCounter++;
			
			
			//infoTemp[7] = hora + ":" + minutos + ":" + segundos; 
			
			 // Definir Iterator para extraer o imprimir valores
	        for( Iterator<String> it = listaMoviles.keySet().iterator(); it.hasNext();)
	        {
	            String s = it.next();
	            String[] infoMovil = listaMoviles.get(s);
	            
	            segundos = "00";
    	    	minutos = "00";
    	    	hora = "00";
    	    	
    	    	edadSegundos = 0;
    	    	edadMinutos = 0;
    	    	edadHora = 0;
	            
	            if(infoMovil[8] == "1")
	            {
	            	infoMovil[7] = "00:00:00"; 			// ---> Edad del movil desde su ultima actualizacion
	            	infoMovil[8] = "0";					// 1 = movil actualizado --- 0 = movil sin actualizacion
	            	updateLabelMovilLocation(infoMovil);
	            	 Log.i("FollowYou", "Actualizo Etiqueta: " + infoMovil[0] + "-" + infoMovil[7]);
	            	listaMoviles.put(infoMovil[0], infoMovil);
	            }
	            else
	            {
	            	
	            	String[] horaParseada = infoMovil[7].split(":");
	            	edadHora = Integer.parseInt(horaParseada[0]);
	            	edadMinutos = Integer.parseInt(horaParseada[1]);
	            	edadSegundos = Integer.parseInt(horaParseada[2]);
	            	
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
	    			infoMovil[7] = hora + ":" + minutos + ":" + segundos;  			// ---> Edad del movil desde su ultima actualizacion
	            	//updateLabelMovilLocation(infoMovil);
	            	Log.i("FollowYou", "Actualizo Etiqueta: " + infoMovil[0] + "-" + infoMovil[7]);
	            	listaMoviles.put(infoMovil[0], infoMovil);
	            	
	            }
	            
	            
	            
	        }// fin del for de actualizacion de etiqueta de tiempo
			
			 updateLabelListaMovilLocation();
    		Log.i("FollowYou", "Se repite ejecucion del HILO");
	        mHandler.removeCallbacks(mMuestraMensaje);
           mHandler.postDelayed(mMuestraMensaje, 1000);
        } // fin run
      };// fin runnable mMuestraMensaje
   
      public void threadActualizaEtiqueta() 
      {
    	  hilo = new Thread()
    	  {
    		  @Override
			public void run()
    		  {
    			  try
    			  {
    				  Thread.sleep(10000);
    				  Log.i("Hilo", "hilo iniciado");
    			  }
    			  catch (InterruptedException e)
    			  {
					// TODO: handle exception
    				  Log.i("Hilo", e.getMessage());
    			  }
    			  
    			  mHandler.post(ejecutarAccionConteoEdadMovil);
    		  }
    	  };
    	  hilo.start();
      }
      
      final Runnable ejecutarAccionConteoEdadMovil = new Runnable() {
		
		public void run() {
			// TODO Auto-generated method stub
			 // Definir Iterator para extraer o imprimir valores
	        for( Iterator<String> it = listaMoviles.keySet().iterator(); it.hasNext();)
	        {
	            String s = it.next();
	            String[] infoMovil = listaMoviles.get(s);
	            
	            segundos = "00";
    	    	minutos = "00";
    	    	hora = "00";
    	    	
    	    	edadSegundos = 0;
    	    	edadMinutos = 0;
    	    	edadHora = 0;
	            
	            if(infoMovil[8] == "1")
	            {
	            	infoMovil[7] = "00:00:00"; 			// ---> Edad del movil desde su ultima actualizacion
	            	infoMovil[8] = "0";					// 1 = movil actualizado --- 0 = movil sin actualizacion
	            	updateLabelMovilLocation(infoMovil);
	            	listaMoviles.put(infoMovil[0], infoMovil);
	            }
	            else
	            {
	            	
	            	String[] horaParseada = infoMovil[7].split(":");
	            	edadHora = Integer.parseInt(horaParseada[0]);
	            	edadMinutos = Integer.parseInt(horaParseada[1]);
	            	edadSegundos = Integer.parseInt(horaParseada[2]);
	            	
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
	    			infoMovil[7] = hora + ":" + minutos + ":" + segundos;  			// ---> Edad del movil desde su ultima actualizacion
	            	updateLabelMovilLocation(infoMovil);
	            	listaMoviles.put(infoMovil[0], infoMovil);
	            	
	            }
	            
	        }
			
			
		}
	};

      
}//Fin de clase