package gaps.example.gapsforelder;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import static gaps.example.gapsforelder.App.CHANNEL_1_ID;

public class GPSService extends Service {
    private static final long minTime = 1000;
    private static final float minDistance = 0;
    String tag = this.toString();
    private LocationManager locationManager;
    private NotificationManager mNM;
    public int GPSCurrentStatus;
    private LocationListener locationListener;
    private double lat;
    private double lon;
    private SQLiteDatabase db;
    private String newAdd;
    private Boolean hasShopping = false;
    private ArrayList<String> name_list = new ArrayList<>();
    private NotificationManagerCompat notificationManager;
    private final IBinder mBinder = new GPSServiceBinder();
    private String title = "It's time to empty your shopping cart!";

    public void startService() {
        Log.v("Kathy", "service start");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
    }

    public void endService() {

        if (locationManager != null && locationListener != null) {

            locationManager.removeUpdates(locationListener);

        }

    }

    @Override

    public IBinder onBind(Intent arg0) {

        // TODO Auto-generated method stub

        return mBinder;

    }

    @Override

    public void onCreate() {
        locationListener = new LocationListener(){
            private static final String tag = "GPSServiceListener";
            private static final float minAccuracyMeters = 1;

            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                Intent i = new Intent("location_update");
                i.putExtra("lat",lat);
                i.putExtra("lon",lon);
                String test = String.valueOf(lat);
                sendBroadcast(i);
                if (hasShopping == true && name_list.size() != 0 && newAdd.equals("Yes")) {
                    notificationManager = NotificationManagerCompat.from(getApplicationContext());
                    //sendOnChannel1(context);
                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.logo_test)
                            .setContentTitle(title)
                            .setContentText("AAAA")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_ALARM)
                            .build();
                    notificationManager.notify(1, notification);
                    stopSelf();
                }
            }

            private void doGet(String string) {

                // TODO Auto-generated method stub

                //

            }

            @Override

            public void onProviderDisabled(String provider) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            }



            @Override

            public void onProviderEnabled(String provider) {

                // TODO Auto-generated method stub



            }

            @Override

            public void onStatusChanged(String provider, int status, Bundle extras) {

                // TODO Auto-generated method stub

                GPSCurrentStatus = status;

            }

        };

        startService();

        Log.v(tag, "GPSService Started.");

    }

    @Override

    public void onDestroy() {

        endService();

        Log.v(tag, "GPSService Ended.");

    }

    public class GPSServiceBinder extends Binder {

        GPSService getService() {

            return GPSService.this;

        }

    }

    public void init_database()
    {
        db = SQLiteDatabase.openOrCreateDatabase("/data/data/gaps.example.gapsforelder/files/demo.bd3",null);
        try{
            Cursor cursor = db.rawQuery("select * from gps",null);
            if (cursor.moveToFirst() == false)
            {
                insertData(db,"No");
            }
            else
            {
                for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
                {
                    newAdd = cursor.getString(cursor.getColumnIndex("gps"));
                }
            }
        }
        catch (SQLException SE){
            db.execSQL("create table gps(_id integer" +" primary key autoincrement," +
                    "gps varchar(50))"
            );
        }
        try{
            Cursor cursor = db.rawQuery("select * from shoping_list_info",null);
            if (cursor.moveToFirst() == true)
            {
                hasShopping = true;
            }
        }
        catch (SQLException SE){
            db.execSQL("create table shoping_list_info(_id integer" +" primary key autoincrement," +
                    " shoppingList varchar(50)," +"item int(10))"
            );
        }
        db.close();
    }

    private void insertData(SQLiteDatabase db, String gps)
    {
        db = SQLiteDatabase.openOrCreateDatabase("/data/data/gaps.example.gapsforelder/files/demo.bd3",null);
        db.execSQL("insert into gps values(null,?)",new String[]{gps});
        db.close();
    }

}
