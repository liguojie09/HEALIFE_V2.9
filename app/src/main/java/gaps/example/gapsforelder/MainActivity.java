package gaps.example.gapsforelder;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.karan.churi.PermissionManager.PermissionManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import static android.content.ContentValues.TAG;
import static gaps.example.gapsforelder.App.CHANNEL_1_ID;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private NotificationManagerCompat notificationManager;
    private BroadcastReceiver broadcastReceiver;
    private ArrayList<String> name_list;
    private String lat;
    private String title = "It's time to empty your shopping cart!";
    private String lon;
    private String text;
    private Boolean haslog = false;
    private Boolean hasShopping = false;
    private PermissionManager permissionManager;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    HomeFragment homefragment = new HomeFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            homefragment,"homepage").commit();
                    return true;
                case R.id.navigation_dashboard:
                    HomepageFragment homepageFragment = new HomepageFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            homepageFragment,"homepage").commit();
                    return true;
                case R.id.navigation_notifications:
                    ReminderFragment reminderFragment = new ReminderFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            reminderFragment,"reminder").commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jundge_database_log();
        Log.v("Kathy", "add clicked");
        if (haslog == false)
        {
            Intent intent= new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            insertLogData(db,"yes");
        }
       else{
           delectData(db);
            permissionManager = new PermissionManager(){};
            permissionManager.checkAndRequestPermissions(this);
            Intent intent2 = new Intent(this, GPSService.class);
            startService(intent2);
            BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            //setSupportActionBar(toolbar);
            //final Button btn_back = (Button) findViewById(R.id.btn_back);

        /*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomepageFragment homepageFragment = new HomepageFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        homepageFragment,"homepage").commit();
            }

        });
        */

            init_database();
            // permissions

            //Set the first home page fragment

        /*
        HomepageFragment homepageFragment = new HomepageFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                homepageFragment,"homepage").commit();
        */
            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    homeFragment,"home").commit();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        permissionManager.checkResult(requestCode,permissions, grantResults);
    }



    @Override
    protected void onResume(){
        super.onResume();
        if (broadcastReceiver == null)
        {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    lat = intent.getExtras().get("lat").toString();
                    lon = intent.getExtras().get("lon").toString();
                    Log.v("Lat", lat);
                    Log.v("Lon",lon);
                    name_list = new ArrayList<>();
                    getSupermarket();
                    notificationManager = NotificationManagerCompat.from(getApplicationContext());
                    //sendOnChannel1(context);
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    public String findSupermarket() {
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        try {  //https://maps.googleapis.com/maps/api/place/nearbysearch/json?&location=-37.908660,145.125250&radius=200&type=supermarket&key=AIzaSyAmJe9z96qhYrX3h1DUUzWUJqdK66I5sak
            url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?&location="+String.valueOf(lat)+","+String.valueOf(lon)+"&radius=200&type=supermarket&key=AIzaSyAmJe9z96qhYrX3h1DUUzWUJqdK66I5sak");
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
                        Log.v("Name", name);
                        name_list.add(name);
                    }
                    String supermarket_name = "";
                    for (int i = 0;i < name_list.size();i++)
                    {
                        supermarket_name = supermarket_name +" " +name_list.get(i);
                    }
                    text = "You are less than 200 metres from " + supermarket_name + "!";
                    Log.v("Name", text);
                    jundge_database();
                    if (hasShopping == true && name_list.size()!=0)
                    {
                        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                                .setSmallIcon(R.drawable.logo_test)
                                .setContentTitle(title)
                                .setContentText(text)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_ALARM)
                                .build();
                        notificationManager.notify(1, notification);
                        Intent intent2 = new Intent(MainActivity.this, GPSService.class);
                        stopService(intent2);
                    }
                }
                catch (Exception e)
                {
                }


            }
        }.execute();
    }

    public void jundge_database() {
        db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + "/demo.bd3", null);
        try {
            Cursor cursor = db.rawQuery("select * from shoping_list_info", null);
            if (cursor.moveToFirst() == true) {
                hasShopping = true;
            }
        } catch (SQLException SE) {
            db.execSQL("create table shoping_list_info(_id integer" + " primary key autoincrement," +
                    " shoppingList varchar(50)," + "item int(10))"
            );
        }
        db.close();
    }

    public void jundge_database_log() {
        db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + "/demo.bd3", null);
        try {
            Cursor cursor = db.rawQuery("select * from login_info", null);
            if (cursor.moveToFirst() == true) {
                haslog = true;
            }
        } catch (SQLException SE) {
            db.execSQL("create table login_info(_id integer" + " primary key autoincrement," +
                    "log varchar(50))"
            );
        }
        db.close();
    }

    public void init_database()
    {
        db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + "/demo.bd3",null);
        try{
            Cursor cursor = db.rawQuery("select * from gps",null);
            if (cursor.moveToFirst() == false)
            {
                insertData(db,"No");
            }
        }
        catch (SQLException SE){
            db.execSQL("create table gps(_id integer" +" primary key autoincrement," +
                    "gps varchar(50))"
            );
        }
        db.close();
    }

    private void insertData(SQLiteDatabase db, String gps)
    {
        db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("insert into gps values(null,?)",new String[]{gps});
        db.close();
    }

    private void insertLogData(SQLiteDatabase db, String gps)
    {
        db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("insert into login_info values(null,?)",new String[]{gps});
        db.close();
    }

    private void delectData(SQLiteDatabase db)
    {
        db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("delete from login_info");
        db.close();
    }

    public String getLon()
    {
        return lon;
    }

    public String getLat()
    {
        return lat;

    }}
