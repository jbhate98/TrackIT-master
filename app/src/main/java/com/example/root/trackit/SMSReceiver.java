
package com.example.root.trackit;
/**
 * Created by root on 11/11/17.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        //---get the SMS message passed in--
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        if (bundle != null)
        {
            //---retrieve the SMS message received--
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                //str += "SMS from " + msgs[i].getOriginatingAddress();
                //str += ":";
                str += msgs[i].getMessageBody().toString();
                str += "\n";
            }
            //---display the new SMS message--
            //Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            //---send a broadcast intent to update the SMS received in the activity--

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("SMS_RECEIVED_ACTION");
                broadcastIntent.putExtra("sms", str);
                broadcastIntent.putExtra("lat", str.substring(0, 0 + 8));
                broadcastIntent.putExtra("log", str.substring(10, 10 + 8));
                broadcastIntent.putExtra("per", str.substring(20, 20 + 2));
                context.sendBroadcast(broadcastIntent);

        }
    }
}