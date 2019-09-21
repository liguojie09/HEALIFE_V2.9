package gaps.example.gapsforelder;


import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dou361.dialogui.DialogUIUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static android.content.ContentValues.TAG;
import static gaps.example.gapsforelder.App.CHANNEL_1_ID;
import static gaps.example.gapsforelder.MedicalreminderFragment.dip2px;


/**
 * A simple {@link Fragment} subclass.
 */
public class shoppingFragment extends Fragment {


    private ArrayList<String> shopping_list;
    private ArrayList<Integer> num_list;
    private ListView list_view;
    private List<Integer> flag;
    private ArrayAdapter<String> adapter;
    private Button reminder;
    private View markView;
    private Integer position_record;
    private String shopping_name;
    private Button add_list;
    private AutoCompleteTextView autoCompleteTextView;
    private ShoppingAdapter shoppingAdapter;
    private ArrayList<String> shopping_item;
    private SQLiteDatabase db;
    public shoppingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping,container,false);
        return view;
    }

    //https://maps.googleapis.com/maps/api/place/nearbysearch/json?&location=-37.908660,145.125250&radius=200&type=supermarket&key=AIzaSyAmJe9z96qhYrX3h1DUUzWUJqdK66I5sak
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view); //get element and init
        intial_databse(); //init SQLite database
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                shoppingItemFragment shoppingItemFragment = new shoppingItemFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        shoppingItemFragment,"shoppingItem").commit();
                        */
                position_record = position;
                markView = View.inflate(getActivity(), R.layout.shopping_list_popup, null);
                final Dialog dialog = DialogUIUtils.showCustomAlert(getContext(), markView, Gravity.CENTER, true, true)
                        .show();
                //cancel
                WindowManager.LayoutParams attrs =dialog.getWindow().getAttributes();
                //attrs.setTitle("Title");
                attrs.width = dip2px(getContext(),333);
                attrs.height = dip2px(getContext(),332);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog);
                dialog.getWindow().setAttributes(attrs);
                markView.findViewById(R.id.medical_mark_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUIUtils.dismiss(dialog);
                        shoppingAdapter.notifyDataSetChanged();
                    }
                });
                markView.findViewById(R.id.medical_mark_done).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flag.add(position_record);
                        DialogUIUtils.dismiss(dialog);
                        shoppingAdapter.notifyDataSetChanged();
                    }
                });
                markView.findViewById(R.id.delect_one).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delectOneRecord(db,shopping_list.get(position_record));
                        shopping_list.clear();
                        num_list.clear();
                        flag.clear();
                        intial_databse();
                        shoppingAdapter.notifyDataSetChanged();
                        DialogUIUtils.dismiss(dialog);
                    }
                });
                markView.findViewById(R.id.delect_all).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delectData(db);
                        flag.clear();
                        num_list.clear();
                        shopping_list.clear();
                        intial_databse();
                        shoppingAdapter.notifyDataSetChanged();
                        DialogUIUtils.dismiss(dialog);
                    }
                });
                shoppingAdapter.notifyDataSetChanged();
            }
        });
    }


    class ShoppingAdapter extends BaseAdapter {
        private Context mContext;
        private List<String> shopping_list_name;

        public ShoppingAdapter(Context mContext, List<String> shopping_list_name) {
            this.mContext = mContext;
            this.shopping_list_name = shopping_list_name;
        }

        @Override
        public int getCount() {
            return shopping_list_name.size();
        }

        @Override
        public Object getItem(int position) {
            return shopping_list_name.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = View.inflate(mContext, R.layout.listview_shopping, null);
            TextView shopping = (TextView) v.findViewById(R.id.shopping_list_name);
            //Set text for TextView
            shopping.setText(shopping_list_name.get(position));
            TextView date = (TextView) v.findViewById(R.id.step_date);
            int size = flag.size();
            ImageView imageView = (ImageView) v.findViewById(R.id.shopping_reminder_btn);
            if (num_list.get(position) == 10000)
            {
                TextView t = (TextView) v.findViewById(R.id.shopping_list_name);
                t.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                flag.add(position);
                //v.findViewById(R.id.shopping_reminder_btn).setBackgroundResource(R.drawable.medical_reminder_done);
            }
            for (int i = 0; i < size;i++)
            {
                if (flag.get(i) == position)
                {
                    TextView t = (TextView) v.findViewById(R.id.shopping_list_name);
                    t.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    updateData(db,num_list.get(position),10000);
                    //image_list.set(position,R.drawable.medical_reminder_done);
                }
            }
            return v;
        }
    }

    public void init(View view){
        flag = new ArrayList<>();
        shopping_list = new ArrayList<>(); //set adapter and add
        num_list = new ArrayList<>();
        // shopping auto match setting
        list_view = (ListView) view.findViewById(R.id.shopping_list);
        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.auto_text);
        shoppingAdapter = new ShoppingAdapter(getContext(),shopping_list);
        shopping_item = new ArrayList<>();
        list_view.setAdapter(shoppingAdapter);
        add_list = (Button) view.findViewById(R.id.shopping_add_btn);
        getItem();
        // go back to reminder part
        /*
        reminder = (Button) view.findViewById(R.id.btn_reminder_back);
        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderFragment reminderFragment = new ReminderFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        reminderFragment,"reminder").commit();
            }
        });
        */
        // add new list
        add_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int item_num = shopping_list.size();
                num_list.add(item_num+1);
                shopping_name = autoCompleteTextView.getText().toString();
                Boolean in = false;
                for (int i = 0; i < item_num; i++)
                {
                    if (shopping_list.get(i).equals(shopping_name))
                    {
                        in = true;
                        break;
                    }
                }
                if (shopping_name.equals(""))
                {
                    Toast toast = Toast.makeText(getActivity(),"Item cannot be blank",Toast.LENGTH_LONG);
                    toast.show();
                }
                else if(in == true)
                {
                    Toast toast = Toast.makeText(getActivity(),shopping_name + " is already in the shopping list",Toast.LENGTH_LONG);
                    toast.show();
                }
                else {
                    shopping_list.add(shopping_name);
                    insertData(db,shopping_name,item_num+1);
                    shoppingAdapter.notifyDataSetChanged();
                }
                updateGpsData(db,"No","Yes");

            }
        });
        /*
            list on click popup
         */
    }



    public void intial_databse() {
        db = SQLiteDatabase.openOrCreateDatabase("/data/data/gaps.example.gapsforelder/files/demo.bd3",null);
        try{
            Cursor cursor = db.rawQuery("select * from shoping_list_info",null);
            if (cursor.moveToFirst() == true)
            {
                for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
                {
                    String shopping = cursor.getString(cursor.getColumnIndex("shoppingList"));
                    Integer num = cursor.getInt(cursor.getColumnIndex("item"));
                    shopping_list.add(shopping);
                    num_list.add(num);
                    Log.d(TAG, "intial_databse: "+String.valueOf(num));
                }
            }
            else
            {
            }
        }
        catch (SQLException SE){
            db.execSQL("create table shoping_list_info(_id integer" +" primary key autoincrement," +
                    " shoppingList varchar(50)," +"item int(10))"
            );
        }
        db.close();
    }

    public String findItem() {
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        try {  //https://maps.googleapis.com/maps/api/place/nearbysearch/json?&location=-37.908660,145.125250&radius=200&type=supermarket&key=AIzaSyAmJe9z96qhYrX3h1DUUzWUJqdK66I5sak
            url = new URL("https://58a9a9da.ngrok.io/items");
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

    public void getItem() {
        new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... params)
            {
                try
                {
                    JSONArray res = new JSONArray(findItem());
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
                        String name =  obj.getString("Items");
                        Log.v("Items", name);
                        shopping_item.add(name);
                    }
                    shopping_item.add("apple");
                    shopping_item.add("banana");
                    shopping_item.add("apple juice");
                    adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,shopping_item);
                    autoCompleteTextView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


            }
        }.execute();
    }


    private void insertData(SQLiteDatabase db, String shoppingList, Integer item)
    {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("insert into shoping_list_info values(null,?,?)",new Object[]{shoppingList, item});
        db.close();
    }
    private void delectData(SQLiteDatabase db)
    {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("delete from shoping_list_info");
        db.close();
    }

    private void updateData(SQLiteDatabase db, Integer old_num, Integer new_num)
    {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("update shoping_list_info set item = ? where item = ?",new Integer[]{new_num,old_num});
        db.close();
    }

    private void delectOneRecord(SQLiteDatabase db, String shoppingList)
    {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("delete from shoping_list_info where shoppingList = ?",new String[]{shoppingList});
        db.close();
    }

    private void updateGpsData(SQLiteDatabase db, String newGps, String oldGps)
    {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("update gps set gps = ? where gps = ?",new String[]{newGps,oldGps});
        db.close();
    }
}
