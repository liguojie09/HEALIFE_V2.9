package gaps.example.gapsforelder;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReminderFragment extends Fragment {

    private ImageButton medical_reminder;
    private ImageButton shopping_reminder;
    private ImageButton event_reminder;
    private SQLiteDatabase db;
    private ImageButton thing_reminder;

    public ReminderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        medical_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MedicalreminderFragment medicalreminderFragment = new MedicalreminderFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        medicalreminderFragment,"medical reminder").commit();
            }
        });

        shopping_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shoppingFragment shopping_reminder = new shoppingFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                       shopping_reminder,"shopping reminder").commit();

            }
        });
        /*
        event_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment homeFragment = new HomeFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        homeFragment,"home").commit();
            }
        });
        */

    }

    public void init(View view){
        medical_reminder = (ImageButton) view.findViewById(R.id.reminder_model1);
        shopping_reminder = (ImageButton) view.findViewById(R.id.reminder_model2);
        //db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        //delectData(db);
        //db.execSQL("Drop table shoping_list_info");
        //event_reminder = (ImageButton) view.findViewById(R.id.reminder_model3);
        //thing_reminder = (ImageButton) view.findViewById(R.id.reminder_model4);
    }
    private void delectData(SQLiteDatabase db)
    {
        db.execSQL("delete from shoping_list_info");
    }
}
