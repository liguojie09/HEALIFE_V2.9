package gaps.example.gapsforelder;

import android.app.Notification;
import android.app.NotificationManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.TimeZone;

import static android.app.Application.getProcessName;
import static android.content.ContentValues.TAG;
import static android.content.Context.NOTIFICATION_SERVICE;
import static gaps.example.gapsforelder.App.CHANNEL_1_ID;

public class GPSServiceListener implements LocationListener {
    private static final String tag = "GPSServiceListener";
    private static final float minAccuracyMeters = 35;
    private NotificationManager mNM;
    public int GPSCurrentStatus;
    private double lat;
    private double lon;
    private SQLiteDatabase db;
    private String newAdd;
    private Boolean hasShopping = false;
    private ArrayList<String> name_list = new ArrayList<>();
    private NotificationManagerCompat notificationManager;

    @Override

    public void onLocationChanged(Location location) {

        // TODO Auto-generated method stub

        if (location != null) {

            if (location.hasAccuracy() && location.getAccuracy() <= minAccuracyMeters) {

                lat = location.getLatitude();
                lon = location.getLongitude();
                getSupermarket(); //get Google places api
                init_database(); // get Whether new add
                if (hasShopping == true && name_list.size()!=0 && newAdd.equals("Yes"))
                {
                }
            }

        }

    }

    private void doGet(String string) {

        // TODO Auto-generated method stub

        //

    }

    @Override

    public void onProviderDisabled(String provider) {

        // TODO Auto-generated method stub

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

    public String findSupermarket() {
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        try {  //https://maps.googleapis.com/maps/api/place/nearbysearch/json?&location=-37.908660,145.125250&radius=200&type=supermarket&key=AIzaSyAmJe9z96qhYrX3h1DUUzWUJqdK66I5sak
            url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?&location="+String.valueOf(lat)+","+String.valueOf(lon)+"&radius=500&type=park&key=AIzaSyAmJe9z96qhYrX3h1DUUzWUJqdK66I5sak");
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return textResult;
    }

    public void getSupermarket() {
        new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... params)
            {
                try
                {
                    JSONObject res = new JSONObject(findSupermarket());
                    JSONArray  results = res.getJSONArray("results");
                    return results;
                }
                catch (Exception e){
                    e.printStackTrace();
                    return null;
                }

            }

            @Override
            protected void onPostExecute( JSONArray all)
            {

                try{

                    for (int i = 0 ; i <  all.length(); i++) {
                        JSONObject obj =  all.getJSONObject(i);
                        String name =  obj.getString("name");
                        name_list.add(name);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        }.execute();
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
