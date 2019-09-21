package gaps.example.gapsforelder;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.Toast;

import java.nio.channels.Channel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static gaps.example.gapsforelder.App.CHANNEL_1_ID;

public class Alarm extends BroadcastReceiver {

    private SQLiteDatabase db;
    private String title;
    private Boolean whe_notify;
    private String text;
    private NotificationManagerCompat notificationManager;
    private String prescription;
    private ArrayList<String> date_array;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        whe_notify = false;
        date_array = new ArrayList<>();
        prescription = bundle.getString("pre");
        text = "It's time to take "+ prescription+"!";
        title = "Hope Medical Reminder";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String time1 = format.format(Calendar.getInstance().getTime());
        //Toast toast = Toast.makeText(context,time1,Toast.LENGTH_LONG);
        //toast.show();
        db = SQLiteDatabase.openOrCreateDatabase(context.getFilesDir().toString() + "/demo.bd3",null);
        try{
            Cursor cursor = searchData(db,prescription);
            if (cursor.moveToFirst() == true)
            {
                for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
                {
                    String date_str = cursor.getString(cursor.getColumnIndex("time"));
                    date_array.add(date_str);
                }
            }
            cursor.close();

        }
        catch (SQLException SE){
            db.execSQL("create table medical_info(_id integer" +" primary key autoincrement," +
                    " medical varchar(50)," +
                    " time varchar(50)," +
                    " image int(10))"
            );
        }
        db.close();
        int size = date_array.size();
        if (size != 0)
        {
            for (int i = 0; i < size; i++)
            {
                if (date_array.get(i).split(" ")[0].equals(time1))
                {
                    whe_notify = true;
                    break;
                }
            }
        }
        if (whe_notify)
        {
            notificationManager = NotificationManagerCompat.from(context);
            //sendOnChannel1(context);
            Notification notification = new NotificationCompat.Builder(context,CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.logo_test)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .build();
            notificationManager.notify(1,notification);
        }
    }

    /*
    public void sendOnChannel1(Context context){
        Notification notification = new NotificationCompat.Builder(context,CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_menu_gallery)
                .setContentTitle("Test")
                .setContentText("message")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .build();
        notificationManager.notify(1,notification);
    }*/
    private void delectData(SQLiteDatabase db)
    {
        db.execSQL("delete from medical_info");
        db.close();
    }
    private Cursor searchData(SQLiteDatabase db,String prescription)
    {
        Cursor cursor = db.rawQuery("select * from medical_info Where medical = ? ORDER BY time ASC",new String[]{prescription
        });
        return cursor;
    }




}