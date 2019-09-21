package gaps.example.gapsforelder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class LocationRevicer extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        String test = intent.getExtras().get("lat").toString();
        Log.v("Reviver", test);
    }
}
