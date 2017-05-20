package sharp.smartplug;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rohit Gurjar on 1/24/2017.
 */

public class ProbeListAdapter extends BaseAdapter {

    String deviceName;
    String Mac;
    LayoutInflater inflater;
    private String TAG = "ProbListAdapter";
    private int resourceId;
    Context context;
    int count;

    public ProbeListAdapter(ProbeListActivity context, String deviceName, String Mac, int resourceId,int count) {
        this.context = context;
        this.resourceId = resourceId;
        this.deviceName = deviceName;
        this.Mac = Mac;
        this.count = count;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {

        if(convertview==null)
        {
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertview = inflater.inflate(R.layout.list_item_card, parent, false);
        }

        ImageView device_img = (ImageView) convertview.findViewById(R.id.device_img);
        TextView device_name = (TextView) convertview.findViewById(R.id.device_name);
        TextView device_mac = (TextView) convertview.findViewById(R.id.device_mac);
        ImageView device_status = (ImageView) convertview.findViewById(R.id.status);

        Log.d(TAG, String.valueOf(resourceId));
        if(CheckNetworkStatus.isInternetAvailable(context))
        device_status.setImageResource(resourceId);
        else
        device_status.setImageResource(R.drawable.img_offline);

        device_name.setText(deviceName);
        device_mac.setText(Mac);

        return convertview;
    }
}
