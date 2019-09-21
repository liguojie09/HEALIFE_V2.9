package gaps.example.gapsforelder;


import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dou361.dialogui.DialogUIUtils;

import java.util.ArrayList;
import java.util.List;

import me.yuqirong.cardswipelayout.CardConfig;
import me.yuqirong.cardswipelayout.CardItemTouchHelperCallback;
import me.yuqirong.cardswipelayout.CardLayoutManager;
import me.yuqirong.cardswipelayout.OnSwipeListener;

import static gaps.example.gapsforelder.MedicalreminderFragment.dip2px;


public class GoalsFragment extends Fragment {


    private List<Integer> list = new ArrayList<>();
    private List<Integer> likes_list = new ArrayList<>();
    private List<Integer> records_list = new ArrayList<>();
    //private int position;
    private Integer position_record;
    private View view_1;
    private List<Integer> list_back = new ArrayList<>();
    private Button reset_btn;
    private DemoAdapter demoAdapter;
    private List<Integer> flag = new ArrayList<>();
    //private ImageView goal_1;
    //private ImageView goal_2;
    private View markView;
    //private ImageView goal_3;
    private ListView listView;
    private RecyclerView recyclerView;
    private SQLiteDatabase db;
    public GoalsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view_1 = inflater.inflate(R.layout.fragment_goals,container,false);
        return view_1;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //rest button and goals
        listView = (ListView) view.findViewById(R.id.goals_reuslt);
        reset_btn = (Button) view.findViewById(R.id.reset_btn);
        recyclerView = (RecyclerView) view.findViewById(R.id.goals_list);
        initData();
        initView();
        // position = 0;
        demoAdapter= new DemoAdapter(getContext(),records_list);
        listView.setAdapter(demoAdapter);
        list_back.add(R.drawable.new_goal_1);
        list_back.add(R.drawable.new_goal_2);
        list_back.add(R.drawable.new_goal_3);
        list_back.add(R.drawable.new_goal_4);
        list_back.add(R.drawable.new_goal_5);
        list_back.add(R.drawable.new_goal_6);
        list_back.add(R.drawable.new_goal_7);
        list_back.add(R.drawable.new_goal_8);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                listView = (ListView) view.findViewById(R.id.goals_reuslt);
                position_record = position;
                markView = View.inflate(getActivity(), R.layout.goals_popup, null);
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
                        flag.add(position_record);
                        DialogUIUtils.dismiss(dialog);
                        demoAdapter.notifyDataSetChanged();
                    }
                });
                markView.findViewById(R.id.delect_one).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delectOneRecord(db,records_list.get(position));
                        flag.remove(position_record);
                        records_list.remove(position);
                        DialogUIUtils.dismiss(dialog);
                        demoAdapter.notifyDataSetChanged();
                    }
                });
                markView.findViewById(R.id.delect_all).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delectData(db);
                        initView();
                        records_list.clear();
                        demoAdapter.notifyDataSetChanged();
                        DialogUIUtils.dismiss(dialog);
                    }
                });
                demoAdapter.notifyDataSetChanged();
            }
        });
        /*
        goal_1 = (ImageView)view.findViewById(R.id.goal_1);
        goal_2 = (ImageView)view.findViewById(R.id.goal_2);
        goal_3 = (ImageView)view.findViewById(R.id.goal_3);
        */
        //onclick and done
        /*
        goal_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goal_1.setImageResource(R.drawable.done_back);
                updateData(db,R.drawable.done_back,records_list.get(0));
                //records_list.add(0,R.drawable.home_model8);
            }
        });
        goal_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goal_2.setImageResource(R.drawable.done_back);
                //records_list.add(1,R.drawable.home_model8);
                updateData(db,R.drawable.done_back,records_list.get(1));
            }
        });
        goal_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goal_3.setImageResource(R.drawable.done_back);
                //records_list.add(2,R.drawable.home_model8);
                updateData(db,R.drawable.done_back,records_list.get(2));
            }
        });
        */
        //reset btn

        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delectData(db);
                initView();
                records_list.clear();
                likes_list.clear();
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int sum = 0;
                        initData();
                        int length = likes_list.size();
                        for (int i = 0; i < length ; i++)
                        {
                            sum += likes_list.get(i) * Math.pow(10,length-i-1);
                        }
                        int total_number = 0;
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }, 1000L);
                demoAdapter.notifyDataSetChanged();
            }
        });
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new MyAdapter());
        CardItemTouchHelperCallback cardCallback = new CardItemTouchHelperCallback(recyclerView.getAdapter(), list);
        cardCallback.setOnSwipedListener(new OnSwipeListener<Integer>() {

            @Override
            public void onSwiping(RecyclerView.ViewHolder viewHolder, float ratio, int direction) {
                MyAdapter.MyViewHolder myHolder = (MyAdapter.MyViewHolder) viewHolder;
                viewHolder.itemView.setAlpha(1 - Math.abs(ratio) * 0.2f);
                if (direction == CardConfig.SWIPING_LEFT) {
                    myHolder.dislikeImageView.setAlpha(Math.abs(ratio));
                } else if (direction == CardConfig.SWIPING_RIGHT) {
                    myHolder.likeImageView.setAlpha(Math.abs(ratio));
                } else {
                    myHolder.dislikeImageView.setAlpha(0f);
                    myHolder.likeImageView.setAlpha(0f);
                }
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, Integer o, int direction) {
                MyAdapter.MyViewHolder myHolder = (MyAdapter.MyViewHolder) viewHolder;
                viewHolder.itemView.setAlpha(1f);
                myHolder.dislikeImageView.setAlpha(0f);
                myHolder.likeImageView.setAlpha(0f);
                if (direction == CardConfig.SWIPING_LEFT)
                {
                    //position+=1;
                    likes_list.add(1);
                    //position+=1;
                    insertData(db,list_back.get(likes_list.size()-1));
                    records_list.add(list_back.get(likes_list.size()-1));
                    demoAdapter.notifyDataSetChanged();
                }
                else {
                    likes_list.add(1);
                }

            }

            @Override
            public void onSwipedClear() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int sum = 0;
                        initData();
                        int length = likes_list.size();
                        for (int i = 0; i < length ; i++)
                        {
                            sum += likes_list.get(i) * Math.pow(10,length-i-1);
                        }
                        int total_number = 0;
                        /*
                        for (int i = 0; i < length; i++)
                        {
                            if (likes_list.get(i) == 1)
                            {
                                total_number+=1;
                                if (total_number == 1)
                                {
                                    //goal_1.setImageResource(list.get(i));
                                    insertData(db,list.get(i));
                                    records_list.add(0,list.get(i));
                                }
                                else if(total_number ==2)
                                {
                                    //goal_2.setImageResource(list.get(i));
                                    records_list.add(1,list.get(i));
                                    insertData(db,list.get(i));
                                }
                                else if(total_number ==3)
                                {
                                    //goal_3.setImageResource(list.get(i));
                                    records_list.add(2,list.get(i));
                                    insertData(db,list.get(i));
                                }
                                else{

                                }
                            }
                        }
                        total_number = 0;
                        for (int i = 0; i < length ; i++)
                        {
                            likes_list.remove(0);
                        }
                        */
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }, 3000L);
            }

        });
        final ItemTouchHelper touchHelper = new ItemTouchHelper(cardCallback);
        final CardLayoutManager cardLayoutManager = new CardLayoutManager(recyclerView, touchHelper);
        recyclerView.setLayoutManager(cardLayoutManager);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    private void initView() {
        /*
        if (records_list.size() != 0)
        {
            goal_1.setImageResource(records_list.get(0));
            goal_2.setImageResource(records_list.get(1));
            goal_3.setImageResource(records_list.get(2));
        }
        */
        //create db if no exit
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        try{
            Cursor cursor = db.rawQuery("select * from goals_info",null);
            if (cursor.moveToFirst() == true)
            {
                for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
                {
                    int goals_records = cursor.getInt(cursor.getColumnIndex("goals"));
                    records_list.add(goals_records);
                }
                //goal_1.setImageResource(records_list.get(0));
                //goal_2.setImageResource(records_list.get(1));
                //goal_3.setImageResource(records_list.get(2));
            }
            else
            {
                //goal_1.setImageResource(R.drawable.set_goals);
                //goal_2.setImageResource(R.drawable.set_goals);
                //goal_3.setImageResource(R.drawable.set_goals);
            }
            cursor.close();
        }
        catch (SQLException SE){
            db.execSQL("create table goals_info(_id integer" +" primary key autoincrement," +
                    " goals integer(50))"
            );
        }
        db.close();
    }

    private void initData() {
        list.add(R.drawable.new_goal_1);
        list.add(R.drawable.new_goal_2);
        list.add(R.drawable.new_goal_3);
        list.add(R.drawable.new_goal_4);
        list.add(R.drawable.new_goal_5);
        list.add(R.drawable.new_goal_6);
        list.add(R.drawable.new_goal_7);
        list.add(R.drawable.new_goal_8);

    }

    private class MyAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ImageView avatarImageView = ((MyViewHolder) holder).avatarImageView;
            avatarImageView.setImageResource(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView avatarImageView;
            ImageView likeImageView;
            ImageView dislikeImageView;

            MyViewHolder(View itemView) {
                super(itemView);
                avatarImageView = (ImageView) itemView.findViewById(R.id.iv_avatar);
                likeImageView = (ImageView) itemView.findViewById(R.id.iv_like);
                dislikeImageView = (ImageView) itemView.findViewById(R.id.iv_dislike);
            }

        }
    }

    class DemoAdapter extends BaseAdapter {
        private Context mContext;
        private List<Integer> records_list;


        public DemoAdapter(Context mContext, List<Integer> records_list) {
            this.mContext = mContext;
            this.records_list = records_list;

        }
        @Override
        public int getCount() {
            return records_list.size();
        }

        @Override
        public Object getItem(int position) {
            return records_list.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = View.inflate(mContext, R.layout.listview_goals, null);
            ImageView img = (ImageView) v.findViewById(R.id.goals_image);
            //Set text for TextView
            img.setImageResource(records_list.get(position));
            int size = flag.size();
            for (int i = 0; i < size;i++)
            {
                if (flag.get(i) == position)
                {
                    ((ImageView) v.findViewById(R.id.goals_image)).setImageResource(R.drawable.new_done);
                    updateData(db,R.drawable.new_done,records_list.get(position));
                    records_list.set(position,R.drawable.new_done);
                    demoAdapter.notifyDataSetChanged();
                }
            }
            return v;
        }
        public void setMeical(int position){

        }

    }
    private void insertData(SQLiteDatabase db, Integer goals)
    {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("insert into goals_info values(null,?)",new Integer[]{goals});
        db.close();
    }
    private void delectData(SQLiteDatabase db)
    {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("delete from goals_info");
        db.close();
    }

    private void updateData(SQLiteDatabase db, Integer new_goal, Integer goals)
    {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("update goals_info set goals = ? where goals = ?",new Integer[]{new_goal,goals});
        db.close();
    }

    private void delectOneRecord(SQLiteDatabase db, Integer goals)
    {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        db.execSQL("delete from goals_info where goals = ?",new Integer[]{goals});
        db.close();
    }

}

