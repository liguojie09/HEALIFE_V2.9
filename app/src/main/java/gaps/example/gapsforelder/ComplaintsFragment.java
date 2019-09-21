package gaps.example.gapsforelder;


import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ComplaintsFragment extends Fragment {


    private SQLiteDatabase db;
    public ComplaintsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complaints,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/demo.bd3",null);
        try{
            Cursor cursor = db.rawQuery("select * from goals_info",null);
            if (cursor.moveToFirst() == true)
            {
                delectData(db);

            }
        }
        catch (SQLException SE){
            db.execSQL("create table goals_info(_id integer" +" primary key autoincrement," +
                    " goals integer(50))"
            );
        }

    }
    private void delectData(SQLiteDatabase db)
    {
        db.execSQL("delete from goals_info");
    }

}
