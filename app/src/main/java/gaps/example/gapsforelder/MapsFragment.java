package gaps.example.gapsforelder;


import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {


    private GoogleMap mGoogleMap;
    private SQLiteDatabase db;
    MapView mMapView;
    View mView;
    private ArrayList<String> id_w_group;
    private ArrayList<String> name_w_group;
    private ArrayList<String> add_w_group;
    private ArrayList<String> sub_w_group;
    private ArrayList<String> lat_w_group;
    private ArrayList<String> lng_w_group;
    private ArrayList<String> id_y_group;
    private ArrayList<String> name_y_group;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> suburb_list;
    private ArrayList<String> add_y_group;
    private ArrayList<String> sub_y_group;
    private ArrayList<Double> last_lat;
    private ArrayList<Double> last_lng;
    private Button search;
    private boolean sub_in;
    private ArrayList<String> lat_y_group;
    private ArrayList<String> lng_y_group;
    private String type;
    private String search_suburb;
    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_maps,container,false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.auto_text);
        init();
        search = (Button) view.findViewById(R.id.map_add_btn);

    }

    public void init()
    {
        //delectData_W(db);
        //delectData_Y(db);
        id_w_group = new ArrayList<>();
        name_w_group = new ArrayList<>();
        add_w_group = new ArrayList<>();
        sub_w_group = new ArrayList<>();
        lat_w_group = new ArrayList<>();
        lng_w_group = new ArrayList<>();
        id_y_group = new ArrayList<>();
        lat_y_group = new ArrayList<>();
        lng_y_group = new ArrayList<>();
        last_lat = new ArrayList<>();
        last_lng = new ArrayList<>();
        name_y_group = new ArrayList<>();
        add_y_group = new ArrayList<>();
        sub_y_group = new ArrayList<>();
        type = "walking";
        sub_in = false;
        suburb_list = new ArrayList<>();
        intial_databse_walkinggroup();
        intial_databse_yogagroup();
        intial_databse_suburb();
        search_suburb = "text";
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        //double lat = Double.valueOf(((MainActivity) getActivity()).getLat());
        //double lon = Double.valueOf(((MainActivity) getActivity()).getLon());
        double lat = Double.valueOf("-37.877010");
        double lon = Double.valueOf("145.044266");
        LatLng position = new LatLng(lat, lon);
        googleMap.addMarker(new MarkerOptions().position(position).title("Your Position").snippet("GPS")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location))
               );
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_suburb = autoCompleteTextView.getText().toString();
                int len_suburb = suburb_list.size();
                for (int i =0; i< len_suburb;i++)
                {
                    if (search_suburb.equals(suburb_list.get(i)))
                    {
                        sub_in = true;
                        break;
                    }
                }
                if (id_w_group.size() == 0 || id_y_group.size() ==0)
                {
                    Toast toast = Toast.makeText(getActivity(), "Data is Loading Please wait",Toast.LENGTH_LONG);
                    toast.show();
                }
                else if (sub_in)
                {
                    int length = id_w_group.size();
                    int length_y = id_y_group.size();
                    mGoogleMap.clear();
                    last_lng.clear();
                    last_lat.clear();
                    for (int i = 0; i < length_y; i++) {
                        if (search_suburb.equals(sub_y_group.get(i)))
                        {
                            String name = name_y_group.get(i);
                            String add = add_y_group.get(i);
                            double lat2 = Double.valueOf(lat_y_group.get(i));
                            double lng2 = Double.valueOf(lng_y_group.get(i));
                            LatLng newlocation = new LatLng(lat2, lng2);
                            mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.yoga)).
                                    position(newlocation).title(name).snippet(add)
                            );
                            last_lat.add(lat2);
                            last_lng.add(lng2);
                        }

                    }

                    for (int i = 0; i<length; i++) {
                        if (search_suburb.equals(sub_w_group.get(i)))
                        {
                            String name = name_w_group.get(i);
                            String add = add_w_group.get(i);
                            double lat2 = Double.valueOf(lat_w_group.get(i));
                            double lng2 = Double.valueOf(lng_w_group.get(i));
                            LatLng newlocation = new LatLng(lat2, lng2);
                            mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.walking)).
                                    position(newlocation).title(name).snippet(add)
                            );
                            last_lat.add(lat2);
                            last_lng.add(lng2);
                        }
                    }
                    LatLng new_position = new LatLng(last_lat.get(0),last_lng.get(0) );
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new_position));
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
                    sub_in = false;
                }
                else {
                    Toast toast = Toast.makeText(getActivity(), "Suburb Does no Exit",Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });





        /*

        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.map_popup,null);
                TextView add = (TextView) view.findViewById(R.id.map_address);
                TextView name = (TextView) view.findViewById(R.id.map_name);
                add.setText("test");
                name.setText("aaaa");
                return null;
            }
        });

         */

    }




    public double getLat(String address) {

        Geocoder coder = new Geocoder(getContext());
        try {
            ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(address, 50);
            for (Address add : adresses) {
                double longitude = add.getLongitude();
                double latitude = add.getLatitude();
                return latitude;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public void intial_databse_walkinggroup() {
        db = SQLiteDatabase.openOrCreateDatabase("/data/data/gaps.example.gapsforelder/files/demo.bd3",null);
        try{
            Cursor cursor = db.rawQuery("select * from walking_group",null);
            if (cursor.moveToFirst() == true)
            {
                for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
                {
                    String id = cursor.getString(cursor.getColumnIndex("id"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String address = cursor.getString(cursor.getColumnIndex("address"));
                    String suburb = cursor.getString(cursor.getColumnIndex("suburb"));
                    String lat = cursor.getString(cursor.getColumnIndex("lat"));
                    String lng = cursor.getString(cursor.getColumnIndex("lng"));
                    id_w_group.add(id);
                    name_w_group.add(name);
                    add_w_group.add(address);
                    sub_w_group.add(suburb);
                    lat_w_group.add(lat);
                    lng_w_group.add(lng);
                    Log.v("W_DB",name);
                }
            }
            else
            {
                type = "walking";
                getItem_W();
            }
        }
        catch (SQLException SE){
            db.execSQL("create table walking_group(_id integer" +" primary key autoincrement," +
                    "id varchar(50)," +
                    "name varchar(50)," +
                    "address varchar(50)," +
                    "suburb varchar(50)," +
                    "lat varchar(50)," +
                    "lng varchar(50))"
            );
        }
        db.close();
    }

    public void intial_databse_yogagroup() {
        db = SQLiteDatabase.openOrCreateDatabase("/data/data/gaps.example.gapsforelder/files/demo.bd3",null);
        try{
            Cursor cursor = db.rawQuery("select * from yoga_group",null);
            if (cursor.moveToFirst() == true)
            {
                for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
                {
                    String id = cursor.getString(cursor.getColumnIndex("id"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String address = cursor.getString(cursor.getColumnIndex("address"));
                    String suburb = cursor.getString(cursor.getColumnIndex("suburb"));
                    String lat = cursor.getString(cursor.getColumnIndex("lat"));
                    String lng = cursor.getString(cursor.getColumnIndex("lng"));
                    id_y_group.add(id);
                    name_y_group.add(name);
                    add_y_group.add(address);
                    sub_y_group.add(suburb);
                    lat_y_group.add(lat);
                    lng_y_group.add(lng);
                    Log.v("Y_DB",lat);
                }
            }
            else
            {
                type = "yoga";
                getItem_Y();
            }
        }
        catch (SQLException SE){
            db.execSQL("create table yoga_group(_id integer" +" primary key autoincrement," +
                    "id varchar(50)," +
                    "name varchar(50)," +
                    "address varchar(50)," +
                    "suburb varchar(50),"+
                    "lat varchar(50)," +
                    "lng varchar(50))"
            );
            type = "yoga";
            getItem_Y();
        }
        db.close();
    }

    public void intial_databse_suburb() {
        db = SQLiteDatabase.openOrCreateDatabase("/data/data/gaps.example.gapsforelder/files/demo.bd3",null);
        try{
            Cursor cursor = db.rawQuery("select distinct suburb from yoga_group",null);
            if (cursor.moveToFirst() == true)
            {
                for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
                {
                    String suburb = cursor.getString(cursor.getColumnIndex("suburb"));
                    suburb_list.add(suburb);
                }
            }
            else
            {
                type = "yoga";
                getItem_Y();
            }
            adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,suburb_list);
            autoCompleteTextView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        catch (SQLException SE){
            db.execSQL("create table yoga_group(_id integer" +" primary key autoincrement," +
                    "id varchar(50)," +
                    "name varchar(50)," +
                    "address varchar(50)," +
                    "suburb varchar(50),"+
                    "lat varchar(50)," +
                    "lng varchar(50))"
            );
            type = "yoga";
            getItem_Y();
        }
        db.close();
    }

    public double getLng(String address) {

        Geocoder coder = new Geocoder(getContext());
        try {
            ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(address, 50);
            for (Address add : adresses) {
                // if (statement) {//Controls to ensure it is right address such as country etc.
                double longitude = add.getLongitude();
                double latitude = add.getLatitude();
                return longitude;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String findItem_W() {
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        try {
            url = new URL("https://58a9a9da.ngrok.io/walking" );
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

    public void getItem_W() {
        new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... params)
            {
                try
                {
                    JSONArray res = new JSONArray(findItem_W());
                    return res;
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
                        String id =  obj.getString("id");
                        String name = obj.getString("Name");
                        String add = obj.getString("Address");
                        String sub = obj.getString("Suburb");
                        String lat = obj.getString("lat");
                        String lng = obj.getString("lng");
                        id_w_group.add(id);
                        name_w_group.add(name);
                        add_w_group.add(add);
                        sub_w_group.add(sub);
                        lat_w_group.add(lat);
                        lng_w_group.add(lng);
                        insertData_W(db,id,name,add,sub,lat,lng);
                        Log.v("Working",name);

                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


            }
        }.execute();
    }

    public String findItem_Y() {
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        try {
            url = new URL("https://58a9a9da.ngrok.io/yoga");
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

    public void getItem_Y() {
        new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... params)
            {
                try
                {
                    JSONArray res = new JSONArray(findItem_Y());
                    return res;
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
                        String id =  obj.getString("id");
                        String name = obj.getString("Name");
                        String add = obj.getString("Address");
                        String sub = obj.getString("Suburb");
                        String lat = obj.getString("lat");
                        String lng = obj.getString("lng");
                        id_y_group.add(id);
                        name_y_group.add(name);
                        add_y_group.add(add);
                        sub_y_group.add(sub);
                        lat_y_group.add(lat);
                        lng_y_group.add(lng);
                        insertData_Y(db,id,name,add,sub,lat,lng);
                        Log.v("Y_INSERT",name);
                    }
                    intial_databse_suburb();

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


            }
        }.execute();
    }

    private void insertData_W(SQLiteDatabase db, String id, String name, String add, String sub,String lat,String lng)
    {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("insert into walking_group values(null,?,?,?,?,?,?)",new String[]{id,name,add,sub,lat,lng});
        db.close();
    }

    private void insertData_Y(SQLiteDatabase db, String id, String name, String add, String sub, String lat,String lng)
    {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("insert into yoga_group values(null,?,?,?,?,?,?)",new String[]{id,name,add,sub,lat,lng});
        db.close();
    }

    private void delectData_W(SQLiteDatabase db)
    {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("drop table walking_group");
        db.close();
    }
    private void delectData_Y(SQLiteDatabase db)
    {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("drop table yoga_group");
        db.close();
    }

    private void selectSuburbY(SQLiteDatabase db, String suburb)
    {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        Cursor cursor = db.rawQuery("select * from yoga_group where suburb ="+suburb,null);
        if (cursor.moveToFirst() == true)
        {
            for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
            {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String address = cursor.getString(cursor.getColumnIndex("address"));
                String sub = cursor.getString(cursor.getColumnIndex("suburb"));
                String lat = cursor.getString(cursor.getColumnIndex("lat"));
                String lng = cursor.getString(cursor.getColumnIndex("lng"));
                id_y_group.add(id);
                name_y_group.add(name);
                add_y_group.add(address);
                sub_y_group.add(sub);
                lat_y_group.add(lat);
                lng_y_group.add(lng);
            }
        }
        db.close();
    }
    private void selectSuburbW(SQLiteDatabase db, String suburb)
    {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("delete from walking_group where suburb = ?",new String[]{suburb});
        db.close();
    }


}
