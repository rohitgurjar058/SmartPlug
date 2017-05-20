package sharp.smartplug;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;

import cn.com.broadlink.blnetwork.BLNetwork;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentTimer.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentTimer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentTimer extends Fragment implements View.OnClickListener,AdapterView.OnItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    BLNetwork mBlNetwork;
    Context context;
    ScheduleInfo.Periodic_task[] ptask;
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton btn_schedule,btn_countDown;
    private static final String TAG = "FragmentTimer";

    List<ScheduleInfo.Periodic_task> p_list;
    List<ScheduleInfo.Timer_task> t_list;
    ListView timers_list;
    TimerAdapter adapter;

    ArrayList<String> mac_addr = new ArrayList<String>();
    ArrayList<String> dev_name = new ArrayList<String>();
    private int count,posn;

    public FragmentTimer() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentTimer.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentTimer newInstance(String param1, String param2) {
        FragmentTimer fragment = new FragmentTimer();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_timer, container, false);

        timers_list = (ListView)view.findViewById(R.id.timers_list);
        materialDesignFAM = (FloatingActionMenu)view.findViewById(R.id.menu);
        btn_schedule = (FloatingActionButton)view.findViewById(R.id.schedule_task);
        btn_countDown = (FloatingActionButton)view.findViewById(R.id.CountDown_task);
        mBlNetwork = BLNetwork.getInstanceBLNetwork(getActivity());
        context = container.getContext();

        btn_schedule.setOnClickListener(this);
        btn_countDown.setOnClickListener(this);
        timers_list.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkUtil networkUtil = new NetworkUtil(getActivity());
        networkUtil.startScan();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = getActivity();
        mac_addr = ((FragmentActivity)context).mac_addr;
        dev_name = ((FragmentActivity)context).dev_name;
        count = ((FragmentActivity)context).count;
        posn = ((FragmentActivity) context).position;
    }

    private void sp_random_timer()
    {
        JsonObject in = new JsonObject();
        JsonObject out = new JsonObject();
        JsonObject jobj = new JsonObject();
        JsonParser parser = new JsonParser();

        Gson g_son = new Gson();

        in.addProperty(APIConstants.API_ID, 84);
        in.addProperty(APIConstants.COMMAND,"sp3_task");
        in.addProperty(APIConstants.MAC,mac_addr.get(posn));
        in.addProperty(APIConstants.NAME,dev_name.get(posn));
        in.addProperty(APIConstants.LOCK,0);

        JsonElement element;

        List<ScheduleInfo.Periodic_task> periodic_tasks = new ArrayList<ScheduleInfo.Periodic_task>();
        periodic_tasks.add(new ScheduleInfo.Periodic_task(1, "14:51:00", "14:51:00", 0, 1, 0, 2));
        Type periodic_type = new TypeToken<Collection<ScheduleInfo.Periodic_task>>() {}.getType();
        String p_task = g_son.toJson(periodic_tasks,periodic_type);
        element = parser.parse(p_task);
        in.add("periodic_task",element);

        ScheduleInfo.Random_Timer_task random_tasks = new ScheduleInfo.Random_Timer_task("14:12","17:13",0,1);
        String r_task = g_son.toJson(random_tasks);
        element = (JsonObject)parser.parse(r_task);
        in.add("random_timer_task",element);

        /*  ScheduleInfo.Cycle_Timer_task cycle_tasks =  new ScheduleInfo.Cycle_Timer_task("08:00:00","12:00:00",0,1,60,100);
            Type random_type = new TypeToken<Collection<ScheduleInfo.Periodic_task>>() {}.getType();
            String cycle_task = g_son.toJson(periodic_tasks,periodic_type);
            element = parser.parse(cycle_task);
            in.add("cycle_timer_task",element);*/

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
                    Toast.makeText(context, "Set task success", Toast.LENGTH_LONG).show();
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

    private void sp_schedule_timer()
    {
            JsonObject in = new JsonObject();
            JsonObject out = new JsonObject();
            JsonObject jobj = new JsonObject();
            JsonParser parser = new JsonParser();

            Gson g_son = new Gson();

            in.addProperty(APIConstants.API_ID, 84);
            in.addProperty(APIConstants.COMMAND,"sp3_task");
            in.addProperty(APIConstants.MAC,mac_addr.get(posn));
            in.addProperty(APIConstants.NAME,dev_name.get(posn));
            in.addProperty(APIConstants.LOCK,0);

            JsonElement element;

            List<ScheduleInfo.Periodic_task> periodic_tasks = new ArrayList<ScheduleInfo.Periodic_task>();
            periodic_tasks.add(new ScheduleInfo.Periodic_task(1,"16:31:00","16:32:00", 0, 0, 0, 2));
            Type periodic_type = new TypeToken<Collection<ScheduleInfo.Periodic_task>>() {}.getType();
            String p_task = g_son.toJson(periodic_tasks,periodic_type);
            element = parser.parse(p_task);
            in.add("periodic_task",element);

            List<ScheduleInfo.Timer_task> timer_tasks = new ArrayList<ScheduleInfo.Timer_task>();
            timer_tasks.add(new ScheduleInfo.Timer_task(1, "2017-02-11 16:48:00",1,"2017-02-11 16:49:00"));
            Type timer_type = new TypeToken<Collection<ScheduleInfo.Timer_task>>() {}.getType();
            String t_task = g_son.toJson(timer_tasks,timer_type);
            element = parser.parse(t_task);
            in.add("timer_task",element);

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
                        Toast.makeText(context, "Set task success", Toast.LENGTH_LONG).show();
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

    private void sp_refresh()
    {
        JsonObject in = new JsonObject();
        JsonObject out = new JsonObject();
        JsonArray periodic = new JsonArray();
        JsonArray timer = new JsonArray();

        in.addProperty(APIConstants.API_ID, 82);
        in.addProperty(APIConstants.COMMAND,"sp3_refresh");
        in.addProperty(APIConstants.MAC,mac_addr.get(posn));

        String string = in.toString();
        Log.d(TAG, string);

        String outString = mBlNetwork.requestDispatch(string);
        Log.d(TAG, outString);

        out = new JsonParser().parse(outString).getAsJsonObject();

        int code = out.get(APIConstants.CODE).getAsInt();
        Log.d(TAG, Integer.toString(code));

        final String msg = out.get(APIConstants.MSG).getAsString();
        Log.d(TAG, msg);

        int status = out.get(APIConstants.STATUS).getAsInt();
        Log.d(TAG,Integer.toString(status));

        final String name = out.get(APIConstants.NAME).getAsString();
        //Log.d(TAG,name);

        int lock = out.get(APIConstants.LOCK).getAsInt();

        periodic = out.get("periodic_task").getAsJsonArray();
        Log.d(TAG,periodic.toString());

        timer = out.get("timer_task").getAsJsonArray();
        Log.d(TAG,timer.toString());

        Gson gson = new Gson();
        Type periodic_Type = new TypeToken<Collection<ScheduleInfo.Periodic_task>>() {}.getType();
        Type timer_Type = new TypeToken<Collection<ScheduleInfo.Periodic_task>>() {}.getType();

        List<ScheduleInfo.Periodic_task> p_list  = gson.fromJson(periodic, periodic_Type);
        List<ScheduleInfo.Timer_task> t_list  = gson.fromJson(timer,timer_Type);

        ptask = p_list.toArray(new ScheduleInfo.Periodic_task[p_list.size()]);

        if (p_list.size() > 0)
        {
            for (int i = 0; i < p_list.size(); i++)
            {
                // Log.d(TAG,ptask[i].getOffTime());
            }
            Toast.makeText(context,"Okk Done",Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(context, "No Done", Toast.LENGTH_SHORT).show();
        }

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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.schedule_task:
                Intent i = new Intent(getActivity(),ScheduleTimer.class);
                i.putExtra("mac",mac_addr);
                i.putExtra("name",dev_name);
                i.putExtra("posn",posn);
                startActivity(i);
                getActivity().overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
                break;

            case R.id.CountDown_task:
                Log.d("pos","hello");
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
