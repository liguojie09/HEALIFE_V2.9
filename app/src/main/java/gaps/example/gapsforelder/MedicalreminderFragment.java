package gaps.example.gapsforelder;


import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.a520wcf.yllistview.YLListView;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.dou361.dialogui.DialogUIUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MedicalreminderFragment extends Fragment {
    private YLListView listView;
    private Button sub_btn;
    private Button del_btn;
    private Integer position_record;
    private View rootView;
    private ArrayList<String> everyday;
    private View markView;
    private EditText input_text;
    private String str;
    private TimePickerView pvCustomTime;
    private TextView btn_CustomTime;
    private SQLiteDatabase db;
    private List<String> medical_list;
    private List<Integer> image_list;
    private Button reminder_back;
    private List<String> time_list;
    private String start_date;
    private String end_date;
    private View delect_view;
    private String time_s_1;
    private String time_s_2;
    private String time_s_3;
    private String prescription;
    private String time_select;
    private EditText start_ui;
    private EditText end_ui;
    private TextView test;
    private DemoAdapter demoAdapter;
    private TextView time_1;
    private TextView time_2;
    private TextView time_3;
    private static final int INTERVAL = 1000 * 60 * 60 * 24;
    private ArrayList<Integer> flag;
    private EditText pre;
    private String formattedDate;
    private Date date_1;
    private TimePickerDialog timePickerDialog;
    private static transient int gregorianCutoverYear = 1582;
    private static final int[] DAYS_P_MONTH_LY= {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final int[] DAYS_P_MONTH_CY= {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final int Y = 0, M = 1, D = 2;
    public MedicalreminderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicalreminder,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        test = (TextView) view.findViewById(R.id.test);
        del_btn = (Button) view.findViewById(R.id.cancel_btn);
        sub_btn = (Button) view.findViewById(R.id.submit_btn);
        listView = (YLListView) view.findViewById(R.id.listView);
        final View topView=View.inflate(getContext(),R.layout.top,null);
        listView.addHeaderView(topView);
        View bottomView=new View(getContext());
        listView.addFooterView(bottomView);
        listView.setFinalBottomHeight(100);
        listView.setFinalTopHeight(100);
        medical_list = new ArrayList<>();
        time_list = new ArrayList<>();
        image_list = new ArrayList<>();
        intial_databse();
        demoAdapter= new DemoAdapter(getContext(),medical_list,time_list,image_list);
        listView.setAdapter(demoAdapter);
        //list view on click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                position_record = position;
                markView = View.inflate(getActivity(), R.layout.medical_reminder_mark_popup, null);
                final Dialog dialog = DialogUIUtils.showCustomAlert(getContext(), markView, Gravity.CENTER, true, true)
                        .show();
                WindowManager.LayoutParams attrs =dialog.getWindow().getAttributes();
                //attrs.setTitle("Title");
                attrs.width = dip2px(getContext(),333);
                attrs.height = dip2px(getContext(),332);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog);
                dialog.getWindow().setAttributes(attrs);
                //cancel
                markView.findViewById(R.id.medical_mark_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUIUtils.dismiss(dialog);
                        demoAdapter.notifyDataSetChanged();
                    }
                });
                markView.findViewById(R.id.medical_mark_done).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flag.add(position_record-1);
                        DialogUIUtils.dismiss(dialog);
                        demoAdapter.notifyDataSetChanged();
                    }
                });
                markView.findViewById(R.id.delect_one).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delectOneRecord(db,medical_list.get(position_record-1),time_list.get(position_record-1));
                        //image_list.remove(position_record-1);
                        //medical_list.remove(position_record-1);
                        //time_list.remove(position_record-1);
                        medical_list.clear();
                        image_list.clear();
                        time_list.clear();
                        intial_databse();
                        DialogUIUtils.dismiss(dialog);
                        demoAdapter.notifyDataSetChanged();
                    }
                });
                markView.findViewById(R.id.delect_all).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delectAllRecord(db,medical_list.get(position_record-1));
                        medical_list.clear();
                        image_list.clear();
                        time_list.clear();
                        intial_databse();
                        DialogUIUtils.dismiss(dialog);
                        demoAdapter.notifyDataSetChanged();
                    }
                });
                demoAdapter.notifyDataSetChanged();
            }
        });
        initCustomTimePicker();
        //show time choose
        //submit
        del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delect_view = View.inflate(getActivity(), R.layout.medical_reminder_delect_database_popup, null);
                final Dialog dialog = DialogUIUtils.showCustomAlert(getContext(), delect_view, Gravity.CENTER, true, true)
                        .show();
                WindowManager.LayoutParams attrs =dialog.getWindow().getAttributes();
                //attrs.setTitle("Title");
                attrs.width = dip2px(getContext(),333);
                attrs.height = dip2px(getContext(),332);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog);
                dialog.getWindow().setAttributes(attrs);
                delect_view.findViewById(R.id.medical_database_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int size = medical_list.size();
                        for (int i =0 ; i < size; i++)
                        {
                            medical_list.remove(0);
                            time_list.remove(0);
                        }
                        demoAdapter.notifyDataSetChanged();
                        delectData(db);
                        DialogUIUtils.dismiss(dialog);
                    }
                });
                delect_view.findViewById(R.id.medical_database_confir).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUIUtils.dismiss(dialog);
                    }
                });
            }
        });
        sub_btn.setOnClickListener(new View.OnClickListener() { //this part is dialog
            @Override
            public void onClick(View v) {
                /*
                String text = input_text.getText().toString();
                String time = btn_CustomTime.getText().toString();
                medical_list.add(text);
                time_list.add(time);
                insertData(db,text,time);
                */
                rootView = View.inflate(getActivity(), R.layout.medical_reminder_popup, null);
                final Dialog dialog = DialogUIUtils.showCustomAlert(getContext(), rootView, Gravity.CENTER, true, true)
                        .show();

                WindowManager.LayoutParams attrs =dialog.getWindow().getAttributes();
                //attrs.setTitle("Title");
                attrs.width = dip2px(getContext(),333);
                attrs.height = dip2px(getContext(),332);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog);
                dialog.getWindow().setAttributes(attrs);
                //cancel
                rootView.findViewById(R.id.medical_submit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUIUtils.dismiss(dialog);
                    }
                });
                //submit
                rootView.findViewById(R.id.medical_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prescription = pre.getText().toString();
                        start_date = start_ui.getText().toString();
                        end_date = end_ui.getText().toString();
                        time_s_1 = time_1.getText().toString();
                        time_s_2 = time_2.getText().toString();
                        time_s_3 = time_3.getText().toString();
                        if ((time_s_1.equals(time_s_2) && (!time_s_1.equals("") && !time_s_2.equals("")))
                                || (time_s_2.equals(time_s_3) && (!time_s_2.equals("") && !time_s_3.equals("")))
                                ||(time_s_3.equals(time_s_1) && (!time_s_3.equals("") && !time_s_1.equals(""))))
                        {
                            Toast toast = Toast.makeText(getActivity(),"TIME CANNOT BE SAME",Toast.LENGTH_LONG);
                            toast.show();
                        }
                        else if (end_date.equals("")||start_date.equals("")||prescription.equals(""))
                        {
                            Toast toast = Toast.makeText(getActivity(),"START DATE, END DATE, AND PRESCRIPTION CANNOT BE BLANK",Toast.LENGTH_LONG);
                            toast.show();
                        }
                        else if (time_s_1.equals("")&&time_s_2.equals("")&&time_s_3.equals(""))
                        {
                            Toast toast = Toast.makeText(getActivity(),"YOU MUST SELECT AT LEAST ONE TIME",Toast.LENGTH_LONG);
                            toast.show();
                        }
                        else
                            {
                                submit_reminder();
                                DialogUIUtils.dismiss(dialog);
                            }
                        //Toast toast = Toast.makeText(getActivity(),start_date+" " +time_s_1,Toast.LENGTH_LONG);
                        //toast.show();
                       // DialogUIUtils.dismiss(dialog);
                        if (!time_s_1.equals(""))
                        {
                            Bundle bundle = new Bundle();
                            Intent alarmintent = new Intent(getActivity(), Alarm.class);
                            bundle.putString("pre",prescription);
                            alarmintent.putExtras(bundle);
                            int min_1 = Integer.valueOf(time_s_1.split(":")[0]);
                            int sec_1 = Integer.valueOf(time_s_1.split(":")[1]);
                            PendingIntent sender = PendingIntent.getBroadcast(getContext(),
                                    0, alarmintent, PendingIntent.FLAG_CANCEL_CURRENT);
                            // Schedule the alarm!
                            AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, min_1);
                            calendar.set(Calendar.MINUTE,sec_1);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, sender);
                        }
                        if (!time_s_2.equals(""))
                        {
                            Bundle bundle = new Bundle();
                            Intent alarmintent = new Intent(getActivity(), Alarm.class);
                            bundle.putString("pre",prescription);
                            alarmintent.putExtras(bundle);
                            int min_2 = Integer.valueOf(time_s_2.split(":")[0]);
                            int sec_2 = Integer.valueOf(time_s_2.split(":")[1]);
                            PendingIntent sender = PendingIntent.getBroadcast(getContext(),
                                    0, alarmintent, PendingIntent.FLAG_CANCEL_CURRENT);
                            // Schedule the alarm!
                            AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, min_2);
                            calendar.set(Calendar.MINUTE,sec_2);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, sender);
                        }
                        if (!time_s_3.equals(""))
                        {
                            Bundle bundle = new Bundle();
                            Intent alarmintent = new Intent(getActivity(), Alarm.class);
                            bundle.putString("pre",prescription);
                            alarmintent.putExtras(bundle);
                            int min_3 = Integer.valueOf(time_s_3.split(":")[0]);
                            int sec_3 = Integer.valueOf(time_s_3.split(":")[1]);
                            PendingIntent sender = PendingIntent.getBroadcast(getContext(),
                                    0, alarmintent, PendingIntent.FLAG_CANCEL_CURRENT);
                            // Schedule the alarm!
                            AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, min_3);
                            calendar.set(Calendar.MINUTE,sec_3);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, sender);

                        }
                    }
                });
                //start date
                start_ui = (EditText) rootView.findViewById(R.id.medical_start_date) ;
                rootView.findViewById(R.id.medical_start_date).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            showDatePickDlg(v,R.id.medical_start_date,0);
                            return true;
                        }
                        return false;
                    }
                });
                //end date
                end_ui = (EditText) rootView.findViewById(R.id.medical_end_date) ;
                rootView.findViewById(R.id.medical_start_date).setOnFocusChangeListener(new View.OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            showDatePickDlg(v,R.id.medical_start_date,0);
                        }
                    }
                });
                rootView.findViewById(R.id.medical_end_date).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            start_date = start_ui.getText().toString();
                            showDatePickDlg(v,R.id.medical_end_date,1);
                            return true;
                        }
                        return false;
                    }
                });
                rootView.findViewById(R.id.medical_end_date).setOnFocusChangeListener(new View.OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            start_date = start_ui.getText().toString();
                            showDatePickDlg(v,R.id.medical_end_date,1);
                        }
                    }
                });
                /*
                rootView.findViewById(R.id.medical_time1).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            return true;

                        }
                        return false;
                    }
                });
                rootView.findViewById(R.id.medical_time1).setOnFocusChangeListener(new View.OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {


                        }
                    }
                });
                */
                //time 1
                time_1 = (TextView) rootView.findViewById(R.id.medical_time1);
                rootView.findViewById(R.id.medical_time1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                               time_1.setText(String.format("%d:%d",hourOfDay,minute));
                            }
                        },0,0,false).show();
                    }
                });
                // time 2
                time_2 = (TextView) rootView.findViewById(R.id.medical_time2);
                rootView.findViewById(R.id.medical_time2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                time_2.setText(String.format("%d:%d",hourOfDay,minute));
                            }
                        },0,0,false).show();
                    }
                });

                //time 3
                time_3 = (TextView) rootView.findViewById(R.id.medical_time3);
                rootView.findViewById(R.id.medical_time3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                time_3.setText(String.format("%d:%d",hourOfDay,minute));
                            }
                        },0,0,false).show();
                    }
                });
                pre = (EditText) rootView.findViewById(R.id.medical_subscription);
            }
        });

    }

    // submit through dialog
    public void submit_reminder(){
        String date_1 = convert_format_date(start_date);
        String date_2 = convert_format_date(end_date);
        everyday = getEveryday(date_1,date_2);
        String infor = String.valueOf(everyday.size());
        Toast toast = Toast.makeText(getActivity(),infor + " Days Added" ,Toast.LENGTH_LONG);
        toast.show();
        int size = everyday.size();
        if (!time_s_1.equals(""))
        {
            for(int i = 0; i < size; i++)
            {
                int num = i%3;
                int image_id;
                if (num == 0)
                {
                    image_id = R.drawable.bg_remidner_back;
                }
                else if (num ==1)
                {
                    image_id = R.drawable.bg_remidner_back;
                }
                else {
                    image_id = R.drawable.bg_remidner_back;
                }
                String date_time = everyday.get(i) + " " + time_s_1;
                insertData(db,prescription,date_time,image_id);
                //medical_list.add(prescription);
                //time_list.add(date_time);
                //image_list.add(image_id);
            }
        }
        if (!time_s_2.equals(""))
        {
            for(int i = 0; i < size; i++)
            {
                int num = i%3;
                int image_id;
                if (num == 0)
                {
                    image_id = R.drawable.bg_remidner_back;
                }
                else if (num ==1)
                {
                    image_id = R.drawable.bg_remidner_back;
                }
                else {
                    image_id = R.drawable.bg_remidner_back;
                }
                String date_time = everyday.get(i) + " " + time_s_2;
                insertData(db,prescription,date_time,image_id);
                //medical_list.add(prescription);
                //time_list.add(date_time);
                //image_list.add(image_id);
            }
        }
        if (!time_s_3.equals(""))
        {
            for(int i = 0; i < size; i++)
            {
                int num = i%3;
                int image_id;
                if (num == 0)
                {
                    image_id = R.drawable.bg_remidner_back;
                }
                else if (num ==1)
                {
                    image_id = R.drawable.bg_remidner_back;
                }
                else {
                    image_id = R.drawable.bg_remidner_back;
                }
                String date_time = everyday.get(i) + " " + time_s_3;
                insertData(db,prescription,date_time,image_id);
                //medical_list.add(prescription);
                //time_list.add(date_time);
                //image_list.add(image_id);
            }
        }
        medical_list.clear();
        image_list.clear();
        time_list.clear();
        intial_databse();
        demoAdapter.notifyDataSetChanged();
    }

    public int timeCompare(String startTime, String endTime) {
        int i = 0;
        String start = convert_format_date(startTime);
        String end = convert_format_date(endTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try{
            Date date1 = dateFormat.parse(start);

        }
        catch (Exception e)
        {

        }
        return i;
    }
    public String convert_format_date(String date){
        String final_date;
        String[] result = date.split("-");
        String month = result[1];
        String day = result[2];
        String year = result[0];
        int num_month = Integer.parseInt(month);
        int num_day = Integer.parseInt(day);
        if (num_month < 10 && num_day <10)
        {
            final_date = year +"-0" +String.valueOf(num_month) +"-0" + String.valueOf(num_day);
        }
        else if (num_month < 10 && num_day >= 10)
        {
            final_date = year +"-0" +String.valueOf(num_month) +"-" + String.valueOf(num_day);
        }
        else if (num_month >= 10 && num_day < 10)
        {
            final_date = year + "-" + String.valueOf(num_month) + "-0" +String.valueOf(num_day);
        }
        else
        {
            final_date = date;

        }
        return final_date;
    }
    public static ArrayList<String> getEveryday(String beginDate , String endDate){
        long days = countDay(beginDate, endDate);
        int[] ymd = splitYMD( beginDate );
        ArrayList<String> everyDays = new ArrayList<String>();
        everyDays.add(beginDate);
        for(int i = 0; i < days; i++){
            ymd = addOneDay(ymd[Y], ymd[M], ymd[D]);
            everyDays.add(formatYear(ymd[Y])+"-"+formatMonthDay(ymd[M])+"-"+formatMonthDay(ymd[D]));
        }
        return everyDays;
    }

    public static String formatYear(int decimal){
        DecimalFormat df = new DecimalFormat("0000");
        return df.format( decimal );
    }

    public static String formatMonthDay(int decimal){
        DecimalFormat df = new DecimalFormat("00");
        return df.format( decimal );
    }

    public static boolean isLeapYear(int year) {
        return year >= gregorianCutoverYear ?
                ((year%4 == 0) && ((year%100 != 0) || (year%400 == 0))) : (year%4 == 0);
    }

    private static int[] addOneDay(int year, int month, int day){
        if(isLeapYear( year )){
            day++;
            if( day > DAYS_P_MONTH_LY[month -1 ] ){
                month++;
                if(month > 12){
                    year++;
                    month = 1;
                }
                day = 1;
            }
        }else{
            day++;
            if( day > DAYS_P_MONTH_CY[month -1 ] ){
                month++;
                if(month > 12){
                    year++;
                    month = 1;
                }
                day = 1;
            }
        }
        int[] ymd = {year, month, day};
        return ymd;
    }


    public static long countDay(String begin,String end){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate , endDate;
        long day = 0;
        try {
            beginDate= format.parse(begin);
            endDate=  format.parse(end);
            day=(endDate.getTime()-beginDate.getTime())/(24*60*60*1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }

    public static int[] splitYMD(String date){
        date = date.replace("-", "");
        int[] ymd = {0, 0, 0};
        ymd[Y] = Integer.parseInt(date.substring(0, 4));
        ymd[M] = Integer.parseInt(date.substring(4, 6));
        ymd[D] = Integer.parseInt(date.substring(6, 8));
        return ymd;
    }

    public void intial_databse() {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        try{
            Cursor cursor = db.rawQuery("select * from medical_info ORDER BY time ASC",null);
            if (cursor.moveToFirst() == true)
            {
                for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
                {
                    String medical = cursor.getString(cursor.getColumnIndex("medical"));
                    String time = cursor.getString(cursor.getColumnIndex("time"));
                    Integer image = cursor.getInt(cursor.getColumnIndex("image"));
                    medical_list.add(medical);
                    time_list.add(time);
                    image_list.add(image);
                }
            }
            flag = new ArrayList<>();
        }
        catch (SQLException SE){
            db.execSQL("create table medical_info(_id integer" +" primary key autoincrement," +
                    " medical varchar(50)," +
                    " time varchar(50)," +
                    " image int(10))"
            );
        }
        db.close();
    }
    private void initCustomTimePicker() {
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        startDate.set(year, month, day);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2027, 2, 28);
        pvCustomTime = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                time_select = getTime(date);
            }
        })
                .setCancelText("Cancel")
                .setSubmitText("Sure")
                .setContentTextSize(18)
                .setTitleSize(20)
                .setTitleText("Title")
                .setTitleColor(Color.BLACK)
                .setDividerColor(Color.WHITE)
                .setTextColorCenter(Color.LTGRAY)
                .setLineSpacingMultiplier(1.6f)
                .setTitleBgColor(Color.DKGRAY)
                .setBgColor(Color.BLACK)
                .setSubmitColor(Color.WHITE)
                .setCancelColor(Color.WHITE)
                //.animGravity(Gravity.RIGHT)// default is center*/
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.returnData();
                                pvCustomTime.dismiss();
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.dismiss();
                            }
                        });
                    }
                })
                .setContentTextSize(18)
                .setType(new boolean[]{false, false, false, true, true, true})
                .setLabel("", "", "", "hours", "minutes", "seconds")
                .setLineSpacingMultiplier(1.2f)
                .setTextXOffset(0, 0, 0, 40, 0, -40)
                .isCenterLabel(false)
                .setDividerColor(0xFF24AD9D)
                .build();


    }

    private String getTime(Date date) {
        Log.d("getTime()", "choice date millis: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    class DemoAdapter extends BaseAdapter {
        private Context mContext;
        private List<String> medical_list;
        private List<String> time_list;
        private List<Integer> image_list;

        public DemoAdapter(Context mContext, List<String> medical_list,List<String> time_list, List<Integer> image_list) {
            this.mContext = mContext;
            this.medical_list = medical_list;
            this.time_list = time_list;
            this.image_list = image_list;
        }
        @Override
        public int getCount() {
            return medical_list.size();
        }

        @Override
        public Object getItem(int position) {
            return medical_list.get(position);
        }

        public Object getTime(int position) {
            return time_list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = View.inflate(mContext, R.layout.linear_adapter_step, null);
            TextView step = (TextView)v.findViewById(R.id.step_adapter);
            //Set text for TextView
            step.setText(medical_list.get(position));
            TextView date = (TextView)v.findViewById(R.id.step_date);
            date.setText(time_list.get(position));
            int size = flag.size();
            ImageView imageView = (ImageView) v.findViewById(R.id.medical_reminder_btn);
            imageView.setBackgroundResource(image_list.get(position));
            if (image_list.get(position) == R.drawable.medical_reminder_done)
            {
                TextView t = (TextView) v.findViewById(R.id.step_adapter);
                t.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }            for (int i = 0; i < size;i++)
            {
                if (flag.get(i) == position)
                {
                    v.findViewById(R.id.medical_reminder_btn).setBackgroundResource(R.drawable.medical_reminder_done);
                    TextView t = (TextView) v.findViewById(R.id.step_adapter);
                    t.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    updateData(db,R.drawable.medical_reminder_done,medical_list.get(position),time_list.get(position));
                    //image_list.set(position,R.drawable.medical_reminder_done);
                }
            }
            return v;
        }
        public void setMeical(int position){

        }

    }


    //show datapicker dialog
    public void showDatePickDlg(View view, Integer id, Integer condition) {
        final EditText date = view.findViewById(id);
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear += 1;
                if (monthOfYear < 10 && dayOfMonth <10)
                {
                    date.setText(year + "-0" + monthOfYear + "-0" + dayOfMonth);
                    str = year + "-0" + monthOfYear + "-0" + dayOfMonth;
                }
                else if (dayOfMonth < 10 && dayOfMonth>=10)
                {
                    date.setText(year + "-0" + monthOfYear + "-" + dayOfMonth);
                    str = year + "-0" + monthOfYear + "-" + dayOfMonth;
                }
                else if (dayOfMonth >= 10 && dayOfMonth<10)
                {
                    date.setText(year + "-" + monthOfYear + "-0" + dayOfMonth);
                    str = year + "-" + monthOfYear + "-0" + dayOfMonth;
                }
                else
                {
                    date.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                    str = year + "-" + monthOfYear + "-" + dayOfMonth;
                }

            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (condition == 0)
        {
            formattedDate = sdf.format(calendar.getTime());
            datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        }
        else
        {
            end_date = str;
            try {
                date_1 = sdf.parse(convert_format_date(start_date));
            }
            catch (Exception e){

            }
            datePickerDialog.getDatePicker().setMinDate(date_1.getTime());
        }
        datePickerDialog.show();

    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void insertData(SQLiteDatabase db, String medical, String time, Integer image)
    {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("insert into medical_info values(null,?,?,?)",new Object[]{medical,time,image});
        db.close();
    }
    private void delectData(SQLiteDatabase db)
    {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("delete from medical_info");
        db.close();
    }

    private void delectOneRecord(SQLiteDatabase db, String medical, String time)
    {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("delete from medical_info where medical = ? and time = ?",new String[]{medical,time});
        db.close();
    }

    private void delectAllRecord(SQLiteDatabase db, String medical)
    {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("delete from medical_info where medical = ?",new String[]{medical});
        db.close();
    }


    private void updateData(SQLiteDatabase db, Integer new_image, String medical, String time)
    {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("update medical_info set image = ? where medical = ? and time =?",new Object[]{new_image,medical,time});
        db.close();
    }

}
