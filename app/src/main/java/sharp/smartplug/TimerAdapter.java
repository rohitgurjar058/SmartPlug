package sharp.smartplug;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Rohit Gurjar on 2/22/2017.
 */

public class TimerAdapter extends BaseAdapter {

    LayoutInflater inflater;
    Context context;

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null)
        {
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_timer, parent, false);
        }

        ImageView timer_img = (ImageView) convertView.findViewById(R.id.timer_img);
        TextView timer_name = (TextView) convertView.findViewById(R.id.timer_name);
        TextView onofftime = (TextView) convertView.findViewById(R.id.onofftime);
        TextView timer_repeated_days = (TextView) convertView.findViewById(R.id.timer_repeated_days);
        SwitchCompat toggle_timer = (SwitchCompat) convertView.findViewById(R.id.toggle_timer);
        return null;
    }
}
