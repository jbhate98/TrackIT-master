package com.example.root.trackit;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity implements OnMapReadyCallback {
    Button btnSendSMS;
    IntentFilter intentFilter;
    private GoogleMap mMap;
    String latd=null,lon=null,perc=null;
    Double lt,ln;
    String p;
    Double lat;
    Double log;
    Double per;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("latitude");
    DatabaseReference myRef2 = database.getReference("longitude");
    DatabaseReference myRef3 = database.getReference("Garbage-percentage");

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    private static final String phoneNo="+918668259587";
    private static final String message="es";
    private ProgressBar spinner;
    public String[] dat= new String[3];

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            spinner.setVisibility(View.GONE);
            TextView SMSes = (TextView) findViewById(R.id.textView1);
            SMSes.setText("Location:"+intent.getExtras().getString("sms"));
            Double lat = Double.parseDouble(intent.getExtras().getString("lat"));
            Double log = Double.parseDouble(intent.getExtras().getString("log"));
            Double per = Double.parseDouble(intent.getExtras().getString("per"));
            LatLng sydney = new LatLng(lat,log);
          //  mMap.addMarker(new MarkerOptions().position(sydney).title("This bin is Full!")).setSnippet("Please empty this bin!");
            latd = lat.toString();
            lon = log.toString();
            perc = per.toString();
            myRef.setValue(latd);
            myRef2.setValue(lon);
            myRef3.setValue(perc);
            if(per == 40.0) {
                mMap.addMarker(new MarkerOptions()
                        .position(sydney)
                        .title("This bin is 40% Full!")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18.0f));
            }
            else if(per == 80.0)
            {
                mMap.addMarker(new MarkerOptions()
                        .position(sydney)
                        .title("This bin is 80% Full!")
                        .snippet("Please empty this bin!")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18.0f));
            }
        }
    };
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView SMSes = (TextView) findViewById(R.id.textView3);


        String msg=getIntent().getExtras().getString("link");
        SMSes.setText(msg);
        dat=msg.split("-");
         lat = Double.parseDouble(dat[0]);
         log = Double.parseDouble(dat[1]);
         per = Double.parseDouble(dat[2]);

        //---intent to filter for SMS messages received--
        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*spinner= (ProgressBar) findViewById(R.id.progressBar);

        spinner.setVisibility(View.GONE);
       btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        btnSendSMS.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                //sendSMS("+919000466890", "{\"lat\":17.475195,\"log\":78.386600}");
                sendSMS();
                spinner.setVisibility(View.VISIBLE);
            }
        });*/

        // Write a message to the database


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                latd = value;
              //  Log.d(TAG, "Value is: " + value);
                lt = Double.parseDouble(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

      /*  myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the i dat=data.split("-");nitial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
                ln = Double.parseDouble(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        myRef3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
                p = value;
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });*/

        Toast.makeText(this, latd, Toast.LENGTH_SHORT).show();

      /*  LatLng sydney = new LatLng(lt,ln);
        mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title(p)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18.0f));  */
    }
    @Override
    protected void onResume() {
        //---register the receiver--
        registerReceiver(intentReceiver, intentFilter);
        super.onResume();
    }
    @Override
    protected void onPause() {
        //---unregister the receiver--
        unregisterReceiver(intentReceiver);
        super.onPause();
    }
    //---sends an SMS message to another device--
    protected void sendSMS() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
        else
        {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(),"SMS Sent",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        LatLng sydney = new LatLng(lat,log);
        Log.d("lat",lat.toString());
        Log.d("lan",log.toString());
        Log.d("per",per.toString());


        if(per == 40.0) {
            mMap.addMarker(new MarkerOptions()
                    .position(sydney)
                    .title("This bin is 40% Full!")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18.0f));
        }
        else if(per == 80.0)
        {
            mMap.addMarker(new MarkerOptions()
                    .position(sydney)
                    .title("This bin is 80% Full!")
                    .snippet("Please empty this bin!")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18.0f));
        }

    }
}