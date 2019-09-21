package gaps.example.gapsforelder;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {


    private Spinner securityQ;
    private String securityString;
    private EditText datapicker;
    private String formattedDate;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSecurityQuestions(view);
        getDatapicker(view);

    }

    public void setSecurityQuestions(View view){
        securityQ = view.findViewById(R.id.security_q);
        List<String> security_Qs = new ArrayList<String>();
        security_Qs.add("Primary School Name");
        security_Qs.add("Mother's Name");
        security_Qs.add("Favorite Movie Star");
        security_Qs.add("Favorite Animal");
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity()
                ,android.R.layout.simple_spinner_item, security_Qs);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item
        );
        securityQ.setAdapter(spinnerAdapter);
    }

    public void getSecurityQuestions(View view)
    {
        securityQ = view.findViewById(R.id.security_q);
        securityQ.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                securityString = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void getDatapicker(View view)
    {
        datapicker = (EditText) view.findViewById(R.id.datapicker);
        datapicker.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showDatePickDlg(v);
                    return true;
                }
                return false;
            }
        });
        datapicker.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickDlg(v);
                }
            }
        });
    }

    //show datapicker dialog
    public void showDatePickDlg(View view) {
        datapicker = (EditText) view.findViewById(R.id.datapicker);
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear += 1;
                datapicker.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        formattedDate = sdf.format(calendar.getTime());
        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        datePickerDialog.show();

    }

}
