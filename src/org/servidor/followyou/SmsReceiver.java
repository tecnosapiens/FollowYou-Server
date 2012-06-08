package org.servidor.followyou;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;


public class SmsReceiver extends BroadcastReceiver
{
	String str = "";
	//FollowYouActivity ventana = new FollowYouActivity();
	
	
	 @Override
	    public void onReceive(Context context, Intent intent) 
	    {
		 //---get the SMS message passed in---
	        Bundle bundle = intent.getExtras();        
	        SmsMessage[] msgs = null;
	       // String str = "";            
	        if (bundle != null)
	        {
	            //---retrieve the SMS message received---
	            Object[] pdus = (Object[]) bundle.get("pdus");
	            msgs = new SmsMessage[pdus.length];            
	            for (int i=0; i<msgs.length; i++){
	                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
	                str += "SMS from " + msgs[i].getOriginatingAddress();                     
	                str += " :";
	                str += msgs[i].getMessageBody().toString();
	                str += "\n";        
	            }
	            //---display the new SMS message---
	            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	                    
	       
	            // se crea un nuevo Intent con la informacion que vamos a transferir como broadcast
	            // todo: aqui se debe discriminar los mensajes que deben ser procesados ya que se estan cachando todos los que entran
	            Intent i = new Intent("android.intent.action.MAIN").putExtra("mensaje_recibido", str);
	            context.sendBroadcast(i);
	            

	                                 
	           
	        }                         
		 
	    }
	 
	 
}//fin de clase
