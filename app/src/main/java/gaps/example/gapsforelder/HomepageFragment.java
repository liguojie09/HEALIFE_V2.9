package gaps.example.gapsforelder;


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
public class HomepageFragment extends Fragment {

    // eight buttons of funtions in homepage
    private ImageButton fun1_btn;
    private ImageButton fun2_btn;
    private ImageButton fun3_btn;
    private ImageButton fun4_btn;
    private ImageButton fun5_btn;
    private ImageButton fun6_btn;
    private ImageButton fun7_btn;
    private ImageButton fun8_btn;

    public HomepageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activities_page,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view); //init UI
        //set up on click events of eight bottons
        /*
        fun1_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsFragment mapsFragment = new MapsFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        mapsFragment,"maps").commit();
            }
        });
        fun2_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookeventFragment bookeventFragment = new BookeventFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        bookeventFragment,"events").commit();
            }
        });
        fun3_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AgedserviceFragment agedserviceFragment = new AgedserviceFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        agedserviceFragment,"aged service").commit();
            }
        });
        */
        fun4_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderFragment reminderFragment = new ReminderFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        reminderFragment,"reminder").commit();
            }
        });
        /*
        fun5_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakefriendsFragment makefriendsFragment = new MakefriendsFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        makefriendsFragment,"make friends").commit();
            }
        });
        fun6_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JokesFragment jokesFragment = new JokesFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        jokesFragment,"jokes").commit();
            }
        });
        */

        fun7_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsFragment mapsFragment = new MapsFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        mapsFragment,"maps").commit();
            }
        });


        fun8_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoalsFragment goalsFragment = new GoalsFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        goalsFragment,"maps").commit();
            }
        });
    }

    // init the UI
    public void init(View view)
    {
        //fun1_btn = (ImageButton) view.findViewById(R.id.home_model1);
        //fun2_btn = (ImageButton) view.findViewById(R.id.home_model2);
        //fun3_btn = (ImageButton) view.findViewById(R.id.home_model3);
        fun4_btn = (ImageButton) view.findViewById(R.id.home_model4);
        //fun5_btn = (ImageButton) view.findViewById(R.id.home_model5);
        //fun6_btn = (ImageButton) view.findViewById(R.id.home_model6);
        fun7_btn = (ImageButton) view.findViewById(R.id.home_model7);
        fun8_btn = (ImageButton) view.findViewById(R.id.home_model8);
    }

}
