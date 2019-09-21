package gaps.example.gapsforelder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class JokesAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> steplist;
    private List<String> datelist;


    //Constructor

    public JokesAdapter(Context mContext, List<String> steplist,List<String> datelist) {
        this.mContext = mContext;
        this.steplist = steplist;
        this.datelist = datelist;
    }

    @Override
    public int getCount() {
        return steplist.size();
    }

    @Override
    public String  getItem(int position) {
        return steplist.get(position);
    }

    public String getDate(int position){
        return  datelist.get(position);

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
        step.setText(steplist.get(position));
        TextView date = (TextView)v.findViewById(R.id.step_date);
        date.setText(datelist.get(position));
        return v;
    }

    public void setText(int position,String edit_text)
    {
        steplist.set(position,edit_text);
    }

}
