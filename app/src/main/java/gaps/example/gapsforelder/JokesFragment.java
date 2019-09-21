package gaps.example.gapsforelder;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class JokesFragment extends Fragment {

    private JokesAdapter jokesAdapter; //step adapter
    private ListView listView; //list of records
    private List<String> step_array;
    private List<String> date_array;

    public JokesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jokes,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        step_array = new ArrayList<>(); // step array
        step_array.add("1111");
        step_array.add("2222");
        step_array.add("3333");
        step_array.add("1111");
        step_array.add("2222");
        step_array.add("3333");
        date_array = new ArrayList<>();  // date arry
        date_array.add("a");
        date_array.add("b");
        date_array.add("c");
        date_array.add("a");
        date_array.add("b");
        date_array.add("c");
        listView = (ListView) view.findViewById(R.id.jokes_list); //get the list
        jokesAdapter = new JokesAdapter(getActivity(),step_array,date_array);//create a adapter of step
        listView.setAdapter(jokesAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String edit_result = "";
                Toast.makeText(getActivity(),jokesAdapter.getItem(position), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
