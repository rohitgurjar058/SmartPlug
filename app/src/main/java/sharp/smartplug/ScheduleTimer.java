package sharp.smartplug;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.transition.ChangeBounds;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.codetroopers.betterpickers.recurrencepicker.EventRecurrence;
import com.codetroopers.betterpickers.recurrencepicker.EventRecurrenceFormatter;
import com.codetroopers.betterpickers.recurrencepicker.RecurrencePickerDialogFragment;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import cn.com.broadlink.blnetwork.BLNetwork;

public class ScheduleTimer extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    RelativeLayout relative_on,relative_off;
    LinearLayout linear_repeated;
    TextView txt_on_time,txt_off_time,txt_repeated_value;
    SwitchCompat switch_on,switch_off;
    private static String TAG = "ScheduleTimer";
    Context context = ScheduleTimer.this;

    static final int DIALOG_ON_ID = 0;
    static final int DIALOG_OFF_ID = 1;
    static final int DIALOG_DATE_ON = 2;
    static final int DIALOG_DATE_OFF = 3;
    private static int hour_on,hour_off,year_on,month_on,day_on,year_off,month_off,day_off;
    int minutes_on,minutes_off;

    private ProgressDialog dialog;
    private int on_enable,off_enable;
    private String on_time,off_time;
    ArrayList<String> mac_addr = new ArrayList<String>();
    ArrayList<String> dev_name = new ArrayList<String>();
    int posn;
    private String final_str_on,final_str_off;
    BLNetwork mBlNetwork;

    Button cancel,save;
    Button btn_set,btn_all,btn_all_select,btn_sun,btn_sun_select,btn_mon,btn_mon_select,btn_tue,btn_tue_select,
            btn_wed,btn_wed_select,btn_thrs,btn_thrs_select,btn_fri,btn_fri_select,btn_sat,btn_sat_select;

    BottomSheetDialog btn_sheet_dialog;
    BottomSheetBehavior btn_sheet_behaviour;
    private static int state_all,state_sun,state_mon,state_tue,state_wed,state_thrs,state_fri,state_sat;
    private static String save_repeated;
    String st,ssid,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_timer);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mBlNetwork = BLNetwork.getInstanceBLNetwork(ScheduleTimer.this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mac_addr = extras.getStringArrayList("mac");
            dev_name = extras.getStringArrayList("name");
            posn = extras.getInt("posn");}

        state_all=state_sun=state_mon=state_tue=state_wed=state_thrs=state_fri=state_sat=0;
        relative_on = (RelativeLayout)findViewById(R.id.relative_on);
        relative_off = (RelativeLayout)findViewById(R.id.relative_off);
        linear_repeated = (LinearLayout)findViewById(R.id.linear_repeated);
        txt_on_time = (TextView)findViewById(R.id.txt_on_time);
        txt_off_time = (TextView)findViewById(R.id.txt_off_time);
        txt_repeated_value = (TextView)findViewById(R.id.txt_repeated_value);
        cancel = (Button)findViewById(R.id.cancel);
        save = (Button)findViewById(R.id.save);
        switch_on = (SwitchCompat) findViewById(R.id.toggle_on);
        switch_off = (SwitchCompat) findViewById(R.id.toggle_off);
        openDialog();

        switch_on.setOnCheckedChangeListener(this);
        switch_off.setOnCheckedChangeListener(this);
        relative_on.setOnClickListener(this);
        relative_off.setOnClickListener(this);
        linear_repeated.setOnClickListener(this);
        cancel.setOnClickListener(this);
        save.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

     /*   NetworkUtil networkUtil = new NetworkUtil(ScheduleTimer.this);
        networkUtil.startScan();
        ssid = networkUtil.getWiFiSSID();
      //  initialization(ssid, password);*/
    }

    private void initialization(String ssid, String password) {

        JsonObject in = new JsonObject();
        JsonObject out = new JsonObject();

        in.addProperty(APIConstants.API_ID, 1);
        in.addProperty(APIConstants.COMMAND, "network_init");
        in.addProperty(APIConstants.LICENSE, "pRjJIGY2P8+GCaEIA29pKWCueAfjR/A+LQOlwRp75meHOfTqDxAXpUj4cBzEbO1afzFC7mlqZZvbsU2KPPgOao1zjAJx8r096JOdTOeC6b1AHfITZdg=");
        in.addProperty(APIConstants.LICENSE_TYPE,"YBt2qc/qloPTr3ALDN2Rpk4XdWDVeri2PHrfSDEPol5VEF41Ngc1KM5lk6/C28kF2sZohcEtCUSacdlxX2POmZDifUx9/7GAuht0f3+Hz4k=");
        in.addProperty("ssid", ssid);
        in.addProperty("password", password);
        in.addProperty("broadlinkv2", 1);
        in.addProperty("main_udp_ser","www.baidu.com");
        in.addProperty("backup_udp_se","www.sina.com.cn");
        in.addProperty("main_tcp_ser","www.google.com");
        in.addProperty("main_udp_port",80);
        in.addProperty("backup_udp_port",8080);
        in.addProperty("main_tcp_port",80);

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
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, "Network Initialized", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context,"Network Not Initialized" , Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void sp_schedule_timer(int on_enable, String final_str_on, int off_enable, String final_str_off,String mac,String name) {

        JsonObject in = new JsonObject();
        JsonObject out = new JsonObject();
        JsonObject jobj = new JsonObject();
        JsonParser parser = new JsonParser();

        Gson g_son = new Gson();

        in.addProperty(APIConstants.API_ID, 84);
        in.addProperty(APIConstants.COMMAND,"sp3_task");
        in.addProperty(APIConstants.MAC,mac);
        in.addProperty(APIConstants.NAME,name);
        in.addProperty(APIConstants.LOCK,0);

        JsonElement element;

        List<ScheduleInfo.Periodic_task> periodic_tasks = new ArrayList<ScheduleInfo.Periodic_task>();
        periodic_tasks.add(new ScheduleInfo.Periodic_task(1,"16:31:00","16:32:00", 0, 0, 0, 2));
        Type periodic_type = new TypeToken<Collection<ScheduleInfo.Periodic_task>>() {}.getType();
        String p_task = g_son.toJson(periodic_tasks,periodic_type);
        element = parser.parse(p_task);
        in.add("periodic_task",element);

        List<ScheduleInfo.Timer_task> timer_tasks = new ArrayList<ScheduleInfo.Timer_task>();
        timer_tasks.add(new ScheduleInfo.Timer_task(on_enable,final_str_on,off_enable,final_str_off));
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
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(ScheduleTimer.this, "Set task success", Toast.LENGTH_LONG).show();
                    Intent in = new Intent(ScheduleTimer.this, FragmentActivity.class);
                    in.putExtra("OpenFragmentTimer", 1);
                    startActivity(in);
                }
            });
        } else {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(ScheduleTimer.this, msg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        if(id == DIALOG_ON_ID)
            return new TimePickerDialog(ScheduleTimer.this,picker_on_listener,hour_on,minutes_on,false);
        else if(id == DIALOG_OFF_ID)
            return new TimePickerDialog(ScheduleTimer.this,picker_off_listener,hour_off,minutes_off,false);
        else if(id == DIALOG_DATE_ON)
            return new DatePickerDialog(ScheduleTimer.this,picker_date_on_listener,year_on,month_on,day_on);
        else if(id == DIALOG_DATE_OFF)
            return new DatePickerDialog(ScheduleTimer.this,picker_date_off_listener,year_off,month_off,day_off);
        return null;
    }

    protected DatePickerDialog.OnDateSetListener picker_date_on_listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_on = year;
            month_on = month+1;
            day_on = dayOfMonth;
            showDialog(DIALOG_ON_ID);
        }
    };

    protected DatePickerDialog.OnDateSetListener picker_date_off_listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_off = year;
            month_off = month+1;
            day_off = dayOfMonth;
            showDialog(DIALOG_OFF_ID);
        }
    };

    protected TimePickerDialog.OnTimeSetListener picker_on_listener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_on = hourOfDay;
            minutes_on = minute;
            on_time = ChangeToChinessTime(hour_on,minutes_on,00);

            String from_date = year_on+"-"+month_on+"-"+day_on;
            final_str_on = from_date+" "+on_time;
            txt_on_time.setText(final_str_on);
        }
    };

    protected TimePickerDialog.OnTimeSetListener picker_off_listener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_off = hourOfDay;
            minutes_off = minute;
            off_time = ChangeToChinessTime(hour_off,minutes_off,00);

            String to_date = year_off+"-"+month_off+"-"+day_off;
            final_str_off = to_date+" "+off_time;
            txt_off_time.setText(final_str_off);
        }
    };

    private String ChangeToChinessTime(int from_hr, int from_min, int from_sec) {

        from_hr+=2;
        from_min+=30;

        if(from_hr>=24)
            from_hr-=24;

        if(from_min>=60) {
            from_hr++;

            if(from_hr>=24)
                from_hr-=24;
            from_min -= 60;
        }

        String from_time = from_hr+":"+from_min+":"+from_sec;
        return from_time;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.relative_on :
                showDialog(DIALOG_DATE_ON);
                break;

            case R.id.relative_off :
                showDialog(DIALOG_DATE_OFF);
                break;

            case R.id.linear_repeated:
                btn_sheet_dialog.show();
                savedSelect();
                break;

            case R.id.cancel:
                Intent i = new Intent(this, FragmentActivity.class);
                i.putExtra("OpenFragmentTimer",1);
                startActivity(i);
                break;

            case R.id.save:
                if(CheckNetworkStatus.isInternetAvailable(context)) {

                    initialization(ssid,password);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String mac = mac_addr.get(posn);
                            String name = dev_name.get(posn);
                            sp_schedule_timer(on_enable,final_str_on,off_enable,final_str_off,mac,name);
                        }
                    },3000);
                }
                else
                {
                    Toast.makeText(ScheduleTimer.this, "Please Connect to Network Connection", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_set:
                save_repeated="";
                st = ""+state_all+""+state_sun+""+state_mon+""+state_tue+""+state_wed+""+state_thrs+""+state_fri+""+state_sat;
                Log.d("state",st);

                if(state_all == 1)
                    save_repeated="Sun "+"Mon "+"Tue "+"Wed "+"Thrs "+"Fri "+"Sat";
                else if(state_all == 0){

                    if (state_sun == 1)
                        save_repeated += "Sun ";
                    if (state_mon == 1)
                        save_repeated += "Mon ";
                    if (state_tue == 1)
                        save_repeated += "Tue ";
                    if (state_wed == 1)
                        save_repeated += "Wed ";
                    if (state_thrs == 1)
                        save_repeated += "Thrs ";
                    if (state_fri == 1)
                        save_repeated += "Fri ";
                    if (state_sat == 1)
                        save_repeated += "Sat ";

                    if(state_sun==0 && state_mon==0 && state_tue==0 && state_wed==0 && state_thrs==0 && state_fri==0 && state_sat==0)
                        save_repeated += "Only One Time";
                }
                txt_repeated_value.setText(save_repeated);
                btn_sheet_dialog.dismiss();
                break;

            case R.id.btn_all:
                state_all=state_sun=state_mon=state_tue=state_wed=state_thrs=state_fri=state_sat=1;
                btn_all.setVisibility(View.INVISIBLE);
                btn_all_select.setVisibility(View.VISIBLE);
                btn_sun.setVisibility(View.INVISIBLE);
                btn_sun_select.setVisibility(View.VISIBLE);
                btn_mon.setVisibility(View.INVISIBLE);
                btn_mon_select.setVisibility(View.VISIBLE);
                btn_tue.setVisibility(View.INVISIBLE);
                btn_tue_select.setVisibility(View.VISIBLE);
                btn_wed.setVisibility(View.INVISIBLE);
                btn_wed_select.setVisibility(View.VISIBLE);
                btn_thrs.setVisibility(View.INVISIBLE);
                btn_thrs_select.setVisibility(View.VISIBLE);
                btn_fri.setVisibility(View.INVISIBLE);
                btn_fri_select.setVisibility(View.VISIBLE);
                btn_sat.setVisibility(View.INVISIBLE);
                btn_sat_select.setVisibility(View.VISIBLE);
                break;

            case R.id.btn_all_select:
                state_all=state_sun=state_mon=state_tue=state_wed=state_thrs=state_fri=state_sat=0;
                btn_all_select.setVisibility(View.INVISIBLE);
                btn_all.setVisibility(View.VISIBLE);
                btn_sun.setVisibility(View.VISIBLE);
                btn_sun_select.setVisibility(View.INVISIBLE);
                btn_mon.setVisibility(View.VISIBLE);
                btn_mon_select.setVisibility(View.INVISIBLE);
                btn_tue.setVisibility(View.VISIBLE);
                btn_tue_select.setVisibility(View.INVISIBLE);
                btn_wed.setVisibility(View.VISIBLE);
                btn_wed_select.setVisibility(View.INVISIBLE);
                btn_thrs.setVisibility(View.VISIBLE);
                btn_thrs_select.setVisibility(View.INVISIBLE);
                btn_fri.setVisibility(View.VISIBLE);
                btn_fri_select.setVisibility(View.INVISIBLE);
                btn_sat.setVisibility(View.VISIBLE);
                btn_sat_select.setVisibility(View.INVISIBLE);
                break;

            case R.id.btn_sun:
                state_sun=1;
                btn_sun.setVisibility(View.INVISIBLE);
                btn_sun_select.setVisibility(View.VISIBLE);
                check4ALL();
                break;

            case R.id.btn_sun_select:
                state_sun=0;
                btn_sun_select.setVisibility(View.INVISIBLE);
                btn_sun.setVisibility(View.VISIBLE);
                check4ALL();
                break;

            case R.id.btn_mon:
                state_mon=1;
                btn_mon.setVisibility(View.INVISIBLE);
                btn_mon_select.setVisibility(View.VISIBLE);
                check4ALL();
                break;

            case R.id.btn_mon_select:
                state_mon=0;
                btn_mon_select.setVisibility(View.INVISIBLE);
                btn_mon.setVisibility(View.VISIBLE);
                check4ALL();
                break;

            case R.id.btn_tue:
                state_tue=1;
                btn_tue.setVisibility(View.INVISIBLE);
                btn_tue_select.setVisibility(View.VISIBLE);
                check4ALL();
                break;

            case R.id.btn_tue_select:
                state_tue=0;
                btn_tue_select.setVisibility(View.INVISIBLE);
                btn_tue.setVisibility(View.VISIBLE);
                check4ALL();
                break;

            case R.id.btn_wed:
                state_wed=1;
                btn_wed.setVisibility(View.INVISIBLE);
                btn_wed_select.setVisibility(View.VISIBLE);
                check4ALL();
                break;

            case R.id.btn_wed_select:
                state_wed=0;
                btn_wed_select.setVisibility(View.INVISIBLE);
                btn_wed.setVisibility(View.VISIBLE);
                check4ALL();
                break;

            case R.id.btn_thrs:
                state_thrs=1;
                btn_thrs.setVisibility(View.INVISIBLE);
                btn_thrs_select.setVisibility(View.VISIBLE);
                check4ALL();
                break;

            case R.id.btn_thrs_select:
                state_thrs=0;
                btn_thrs_select.setVisibility(View.INVISIBLE);
                btn_thrs.setVisibility(View.VISIBLE);
                check4ALL();
                break;

            case R.id.btn_fri:
                state_fri=1;
                btn_fri.setVisibility(View.INVISIBLE);
                btn_fri_select.setVisibility(View.VISIBLE);
                check4ALL();
                break;

            case R.id.btn_fri_select:
                state_fri=0;
                btn_fri_select.setVisibility(View.INVISIBLE);
                btn_fri.setVisibility(View.VISIBLE);
                check4ALL();
                break;

            case R.id.btn_sat:
                state_sat=1;
                btn_sat.setVisibility(View.INVISIBLE);
                btn_sat_select.setVisibility(View.VISIBLE);
                check4ALL();
                break;

            case R.id.btn_sat_select:
                state_sat=0;
                btn_sat_select.setVisibility(View.INVISIBLE);
                btn_sat.setVisibility(View.VISIBLE);
                check4ALL();
                break;
        }
    }

    private void savedSelect()
    {
        String str = String.valueOf(state_mon);

        if(state_all == 1)
        {
            btn_all.setVisibility(View.INVISIBLE);
            btn_all_select.setVisibility(View.VISIBLE);
            btn_sun.setVisibility(View.INVISIBLE);
            btn_sun_select.setVisibility(View.VISIBLE);
            btn_mon.setVisibility(View.INVISIBLE);
            btn_mon_select.setVisibility(View.VISIBLE);
            btn_tue.setVisibility(View.INVISIBLE);
            btn_tue_select.setVisibility(View.VISIBLE);
            btn_thrs.setVisibility(View.INVISIBLE);
            btn_thrs_select.setVisibility(View.VISIBLE);
            btn_fri.setVisibility(View.INVISIBLE);
            btn_fri_select.setVisibility(View.VISIBLE);
            btn_sat.setVisibility(View.INVISIBLE);
            btn_sat_select.setVisibility(View.VISIBLE);
        }
        if(state_sun == 1)
        {
            btn_sun.setVisibility(View.INVISIBLE);
            btn_sun_select.setVisibility(View.VISIBLE);
        }
        if(state_mon == 1)
        {
            btn_mon.setVisibility(View.INVISIBLE);
            btn_mon_select.setVisibility(View.VISIBLE);
        }
        if(state_tue == 1)
        {
            btn_tue.setVisibility(View.INVISIBLE);
            btn_tue_select.setVisibility(View.VISIBLE);
        }
        if(state_wed == 1)
        {
            btn_wed.setVisibility(View.INVISIBLE);
            btn_wed_select.setVisibility(View.VISIBLE);
        }
        if(state_thrs == 1)
        {
            btn_thrs.setVisibility(View.INVISIBLE);
            btn_thrs_select.setVisibility(View.VISIBLE);
        }
        if(state_fri == 1)
        {
            btn_fri.setVisibility(View.INVISIBLE);
            btn_fri_select.setVisibility(View.VISIBLE);
        }
        if(state_sat == 1)
        {
            btn_sat.setVisibility(View.INVISIBLE);
            btn_sat_select.setVisibility(View.VISIBLE);
        }
    }

    private void check4ALL()
    {
        if(state_sun==1 && state_mon==1 && state_tue==1 && state_wed==1 && state_thrs==1 && state_fri==1 && state_sat==1)
        {
            btn_all.setVisibility(View.INVISIBLE);
            btn_all_select.setVisibility(View.VISIBLE);
            state_all = 1;
        }
        else
        {
            btn_all.setVisibility(View.VISIBLE);
            btn_all_select.setVisibility(View.INVISIBLE);
            state_all=0;
        }
    }

    private void openDialog() {

        btn_sheet_dialog = new BottomSheetDialog(ScheduleTimer.this);
        View bottom_view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet,null);
        btn_sheet_dialog.setContentView(bottom_view);

        btn_sheet_behaviour = BottomSheetBehavior.from((View)bottom_view.getParent());
        btn_sheet_behaviour.setPeekHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,400, getResources().getDisplayMetrics()));

        btn_set = (Button)bottom_view.findViewById(R.id.btn_set);
        btn_all = (Button)bottom_view.findViewById(R.id.btn_all);
        btn_all_select = (Button)bottom_view.findViewById(R.id.btn_all_select);
        btn_sun = (Button)bottom_view.findViewById(R.id.btn_sun);
        btn_sun_select = (Button)bottom_view.findViewById(R.id.btn_sun_select);
        btn_mon = (Button)bottom_view.findViewById(R.id.btn_mon);
        btn_mon_select = (Button)bottom_view.findViewById(R.id.btn_mon_select);
        btn_tue = (Button)bottom_view.findViewById(R.id.btn_tue);
        btn_tue_select = (Button)bottom_view.findViewById(R.id.btn_tue_select);
        btn_wed = (Button)bottom_view.findViewById(R.id.btn_wed);
        btn_wed_select = (Button)bottom_view.findViewById(R.id.btn_wed_select);
        btn_thrs = (Button)bottom_view.findViewById(R.id.btn_thrs);
        btn_thrs_select = (Button)bottom_view.findViewById(R.id.btn_thrs_select);
        btn_fri = (Button)bottom_view.findViewById(R.id.btn_fri);
        btn_fri_select = (Button)bottom_view.findViewById(R.id.btn_fri_select);
        btn_sat = (Button)bottom_view.findViewById(R.id.btn_sat);
        btn_sat_select = (Button)bottom_view.findViewById(R.id.btn_sat_select);

        btn_set.setOnClickListener(this);
        btn_all.setOnClickListener(this);
        btn_all_select.setOnClickListener(this);
        btn_sun.setOnClickListener(this);
        btn_sun_select.setOnClickListener(this);
        btn_mon.setOnClickListener(this);
        btn_mon_select.setOnClickListener(this);
        btn_tue.setOnClickListener(this);
        btn_tue_select.setOnClickListener(this);
        btn_wed.setOnClickListener(this);
        btn_wed_select.setOnClickListener(this);
        btn_thrs.setOnClickListener(this);
        btn_thrs_select.setOnClickListener(this);
        btn_fri.setOnClickListener(this);
        btn_fri_select.setOnClickListener(this);
        btn_sat.setOnClickListener(this);
        btn_sat_select.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.toggle_on:
                if(isChecked)
                    on_enable=1;
                else
                    on_enable=0;
                break;
            case R.id.toggle_off:
                if(isChecked)
                    off_enable=1;
                else
                    off_enable=0;
                break;
        }
    }
}
