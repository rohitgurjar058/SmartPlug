package sharp.smartplug;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Calendar;
import cn.com.broadlink.blnetwork.BLNetwork;


public class FragmentOnOff extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    static BLNetwork mBlNetwork;
    ImageButton on,off,light_on,light_off;
    Context context;
    TextView time_txt;
    int status;
    Intent intent;
    private static String TAG = "FragmentOnOff";
    ArrayList<String> mac_add = new ArrayList<String>();
    private int count;
    private int position;

    public FragmentOnOff() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentOnOff.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentOnOff newInstance(String param1, String param2) {
        FragmentOnOff fragment = new FragmentOnOff();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        intent = new Intent(getActivity(),BroadcastService.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().startService(intent);
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(BroadcastService.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
        getActivity().stopService(intent);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            status = updateUI(intent);

            if(status == 0) {
                on.setVisibility(View.GONE);
                off.setVisibility(View.VISIBLE);
                light_on.setVisibility(View.GONE);
                light_off.setVisibility(View.VISIBLE);
            }
            else if(status == 1) {
                on.setVisibility(View.VISIBLE);
                off.setVisibility(View.GONE);
                light_on.setVisibility(View.GONE);
                light_off.setVisibility(View.VISIBLE);
            }
            else if(status == 2){
                on.setVisibility(View.GONE);
                off.setVisibility(View.VISIBLE);
                light_on.setVisibility(View.VISIBLE);
                light_off.setVisibility(View.GONE);
            }
            else if(status == 3){
                on.setVisibility(View.VISIBLE);
                off.setVisibility(View.GONE);
                light_on.setVisibility(View.VISIBLE);
                light_off.setVisibility(View.GONE);
            }
        }
    };

    private int updateUI(Intent intent) {
        int status = intent.getIntExtra("statusSwitch",-1);
        Log.d(TAG, String.valueOf(status));
        return status;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_on_off, container, false);

        intent = new Intent(getActivity(),BroadcastService.class);
        mBlNetwork = BLNetwork.getInstanceBLNetwork(getActivity());
        context = container.getContext();

        on = (ImageButton)view.findViewById(R.id.on);
        off = (ImageButton)view.findViewById(R.id.off);
        light_off = (ImageButton)view.findViewById(R.id.img_minilight_off);
        light_on =  (ImageButton)view.findViewById(R.id.img_minilight_on);
        time_txt = (TextView)view.findViewById(R.id.hour_min);

        on.setOnClickListener(this);
        off.setOnClickListener(this);
        light_off.setOnClickListener(this);
        light_on.setOnClickListener(this);

       /* Calendar clnder = Calendar.getInstance();
        int hours = dt.getHours();
        int minutes = dt.getMinutes();
        int seconds = dt.getSeconds();
        String time_str = hours + ":" + minutes + ":" + seconds;
        time_txt.setText(time_str);*/
      //  dt = new Date();
       // int seconds = dt.getSeconds();

      /*  Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                time_txt.setText(DateFormat.format("hh:mm:ss",Calendar.getInstance().getTime()));
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();*/
        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id)
        {
            case R.id.on:
                if(CheckNetworkStatus.isInternetAvailable(context)) {
                    on.setVisibility(View.GONE);
                    sp_off();
                    off.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(getActivity(),"Please Connect to Network Connection",Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.off:
                if(CheckNetworkStatus.isInternetAvailable(context)) {
                    on.setVisibility(View.VISIBLE);
                    sp_on();
                    off.setVisibility(View.GONE);
                }
                else{
                    Toast.makeText(getActivity(),"Please Connect to Network Connection",Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.img_minilight_off :
                light_on.setVisibility(View.VISIBLE);
                sp_lighton();
                light_off.setVisibility(View.GONE);
                break;

            case R.id.img_minilight_on :
                light_off.setVisibility(View.VISIBLE);
                sp_lightoff();
                light_on.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = getActivity();
        mac_add = ((FragmentActivity)context).mac_addr;
        count = ((FragmentActivity)context).count;
        position = ((FragmentActivity) context).position;
    }

    private void sp_on() {

        JsonObject in = new JsonObject();
        JsonObject out = new JsonObject();

        in.addProperty(APIConstants.API_ID, 83);
        in.addProperty(APIConstants.COMMAND,"sp3_control");
        in.addProperty(APIConstants.MAC,mac_add.get(position));
        in.addProperty(APIConstants.STATUS,1);
        in.addProperty(APIConstants.MASK,2);

        String string = in.toString();
        Log.d(TAG, string);

        String outString = mBlNetwork.requestDispatch(string);
        Log.d(TAG, outString);

        out = new JsonParser().parse(outString).getAsJsonObject();

        int code = out.get(APIConstants.CODE).getAsInt();
        Log.d(TAG, Integer.toString(code));

        final String msg = out.get(APIConstants.MSG).getAsString();
        Log.d(TAG, msg);

        if (0 == code) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, "On Success", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void sp_off() {

        JsonObject in = new JsonObject();
        JsonObject out = new JsonObject();

        in.addProperty(APIConstants.API_ID, 83);
        in.addProperty(APIConstants.COMMAND,"sp3_control");
        in.addProperty(APIConstants.MAC,mac_add.get(position));
        in.addProperty(APIConstants.STATUS,0);
        in.addProperty(APIConstants.MASK,2);

        String string = in.toString();
        Log.d(TAG, string);

        String outString = mBlNetwork.requestDispatch(string);
        Log.d(TAG, outString);

        out = new JsonParser().parse(outString).getAsJsonObject();

        int code = out.get(APIConstants.CODE).getAsInt();
        Log.d(TAG, Integer.toString(code));

        final String msg = out.get(APIConstants.MSG).getAsString();
        Log.d(TAG, msg);

        if (0 == code) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, "Off Success", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void sp_lighton() {

        JsonObject in = new JsonObject();
        JsonObject out = new JsonObject();

        in.addProperty(APIConstants.API_ID, 83);
        in.addProperty(APIConstants.COMMAND,"sp3_control");
        in.addProperty(APIConstants.MAC,mac_add.get(position));
        in.addProperty(APIConstants.STATUS,2);
        in.addProperty(APIConstants.MASK,0);

        String string = in.toString();
        Log.d(TAG, string);

        String outString = mBlNetwork.requestDispatch(string);
        Log.d(TAG, outString);

        out = new JsonParser().parse(outString).getAsJsonObject();

        int code = out.get(APIConstants.CODE).getAsInt();
        Log.d(TAG, Integer.toString(code));

        final String msg = out.get(APIConstants.MSG).getAsString();
        Log.d(TAG, msg);

        if (0 == code) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, "Off Success", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void sp_lightoff() {

        JsonObject in = new JsonObject();
        JsonObject out = new JsonObject();

        in.addProperty(APIConstants.API_ID, 83);
        in.addProperty(APIConstants.COMMAND,"sp3_control");
        in.addProperty(APIConstants.MAC,mac_add.get(position));
        in.addProperty(APIConstants.STATUS,0);
        in.addProperty(APIConstants.MASK,0);

        String string = in.toString();
        Log.d(TAG, string);

        String outString = mBlNetwork.requestDispatch(string);
        Log.d(TAG, outString);

        out = new JsonParser().parse(outString).getAsJsonObject();

        int code = out.get(APIConstants.CODE).getAsInt();
        Log.d(TAG, Integer.toString(code));

        final String msg = out.get(APIConstants.MSG).getAsString();
        Log.d(TAG, msg);

        if (0 == code) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, "Off Success", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
