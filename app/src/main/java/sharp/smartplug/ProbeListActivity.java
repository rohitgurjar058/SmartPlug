package sharp.smartplug;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.*;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
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
import java.util.Locale;

import cn.com.broadlink.blnetwork.BLNetwork;

public class ProbeListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    ListView probe_list;
    BLNetwork mBlNetwork;
    private Collection<ProbeListInfo> deviceCollectionList;
    List<ProbeListInfo> result;
    private static final String TAG = "ProbeListActivity";
    public Context context = ProbeListActivity.this;
    ProbeListAdapter adapter;
    DatabaseHandler db;
    String ssid,password;
    private ProgressDialog dialog;
    ArrayList<String> device_name = new ArrayList<String>();
    ArrayList<String> mac_addr = new ArrayList<String>();
    NetworkUtil networkUtil;
    int ressourceAnInt = 0;

    private static String type,lock,key,subdevice,id,passwrd;
    private static String lan_ip;

    private String name,dev_state;
    private int ressourceId;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_probe_list);
        db = new DatabaseHandler(getApplicationContext());
        intent = new Intent(ProbeListActivity.this,BroadcastService.class);
        mBlNetwork = BLNetwork.getInstanceBLNetwork(ProbeListActivity.this);

        probe_list = (ListView) findViewById(R.id.probe_listview);
        probe_list.setOnItemClickListener(this);

        device_name = getName("ProbeCredentials");
        mac_addr = getMac("ProbeCredentials");

        Log.d("name", String.valueOf(device_name));
        Log.d("mac", String.valueOf(mac_addr));

        for(int m=0;m<db.getListCount();m++)
        {
            adapter = new ProbeListAdapter(ProbeListActivity.this,device_name.get(m),mac_addr.get(m),ressourceId,db.getListCount());
            probe_list.setAdapter(adapter);
        }
        //setLocaleEn(this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(CheckNetworkStatus.isInternetAvailable(context)) {
            NetworkUtil networkUtil = new NetworkUtil(ProbeListActivity.this);
            networkUtil.startScan();
            ssid = networkUtil.getWiFiSSID();
            db = new DatabaseHandler(getApplicationContext());
            password = db.getPassword(ssid);
            initialization(ssid,password);
        }
        else{
            Toast.makeText(context,"Please connect to the network",Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        stopService(intent);
    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                dev_state = updateStatus(intent);
                Log.d("state", String.valueOf(dev_state));
                for (int j = 0; j < db.getListCount(); j++) {

                    if (dev_state.equals("LOCAL"))
                        ressourceId = R.drawable.img_online;
                    else if(dev_state.equals("REMOTE"))
                        ressourceId = R.drawable.img_remote;
                    else {
                        ressourceId = R.drawable.img_offline;
                    }

                    Log.d("res", String.valueOf(ressourceId));
                    adapter = new ProbeListAdapter(ProbeListActivity.this, device_name.get(j), mac_addr.get(j), ressourceId, db.getListCount());
                    probe_list.setAdapter(adapter);
                }
            }
    };

    private String updateStatus(Intent intent) {
        String state = intent.getStringExtra("devicestate");
        Log.d("state", String.valueOf(state));
        return state;
    }

    private ArrayList<String> getMac(String TableName) {

        DatabaseHandler helper = new DatabaseHandler(getApplicationContext());
        String Query = "select mac from "+TableName;
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        Cursor cursor = sqldb.rawQuery(Query,null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        for(int cnt=0;cnt<cursor.getCount();cnt++)
        {
            String mc = cursor.getString(cnt);
            mac_addr.add(mc);
        }
        cursor.close();
        return mac_addr;
    }

    private ArrayList<String> getName(String TableName) {

        DatabaseHandler helper = new DatabaseHandler(getApplicationContext());
        String Query = "SELECT name FROM "+TableName;
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        Cursor cursor = sqldb.rawQuery(Query,null);

        if (cursor != null)
            cursor.moveToFirst();

        Log.d("GET NAME",""+cursor.getCount());

        for(int cnt=0;cnt<cursor.getCount();cnt++)
        {
            String nm = cursor.getString(cnt);
            device_name.add(nm);
        }
        cursor.close();
        return device_name;
    }

    private String get_device_ip()
    {
        JsonObject in = new JsonObject();
        JsonObject out = new JsonObject();

        in.addProperty(APIConstants.API_ID, 15);
        in.addProperty(APIConstants.COMMAND, "device_lan_ip");
      //  in.addProperty(APIConstants.MAC,mac_save);

        String string = in.toString();
        Log.d(TAG, string);

        String outString = mBlNetwork.requestDispatch(string);
        Log.d(TAG, outString);

        out = new JsonParser().parse(outString).getAsJsonObject();

        int code = out.get(APIConstants.CODE).getAsInt();
        final String lan_ip = out.get("lan_ip").getAsString();

        if(code == 0)
            return lan_ip;
        else
            return null;
    }

    private void ObtainWifiInfo() {

        JsonObject in = new JsonObject();

        JsonObject out = new JsonObject();
        JsonElement element;

        in.addProperty(APIConstants.API_ID, 10002);
        in.addProperty(APIConstants.COMMAND, "wifi_info");
      //  in.addProperty("mac",mac_save);
        in.addProperty("ipaddr",lan_ip);

        String string = in.toString();
        Log.d(TAG, string);

        String outString = mBlNetwork.requestDispatch(string);
        Log.d(TAG, outString);

        out = new JsonParser().parse(outString).getAsJsonObject();

        int code = out.get(APIConstants.CODE).getAsInt();
        Log.d(TAG, Integer.toString(code));

        final String msg = out.get(APIConstants.MSG).getAsString();
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
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startService(intent);
                            registerReceiver(broadcastReceiver, new IntentFilter(BroadcastService.BROADCAST_ACTION));
                        }
                    },1000);

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

        private void setLocaleEn(Context context) {
            Locale locale = new Locale("en_US");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            context.getApplicationContext().getResources().updateConfiguration(config, null);
        }

        private void device_add() {

            JsonObject in = new JsonObject();
            JsonObject out = new JsonObject();

            in.addProperty(APIConstants.API_ID, 12);
            in.addProperty(APIConstants.COMMAND, "device_add");
            // in.addProperty(APIConstants.MAC,mac_save);
            in.addProperty(APIConstants.TYPE, type);
            in.addProperty(APIConstants.NAME, name);
            in.addProperty(APIConstants.LOCK, lock);
            in.addProperty(APIConstants.PASSWORD, passwrd);
            in.addProperty(APIConstants.ID, id);
            in.addProperty(APIConstants.SUBDEVICE, subdevice);
            in.addProperty(APIConstants.KEY, key);

            String string = in.toString();
            Log.d(TAG, string);

            String outString = mBlNetwork.requestDispatch(string);
            Log.d(TAG, outString);

            out = new JsonParser().parse(outString).getAsJsonObject();

            int code = out.get(APIConstants.CODE).getAsInt();
            final String msg = out.get(APIConstants.MSG).getAsString();
            Log.d("device_add", msg);
        }

        private void probe_list() {

            JsonObject in = new JsonObject();
            JsonObject out = new JsonObject();
            JsonArray listJsonArray = new JsonArray();

            in.addProperty(APIConstants.API_ID, 11);
            in.addProperty(APIConstants.COMMAND, "probe_list");
            String string = in.toString();
            Log.d(TAG, string);

            String probeOut = mBlNetwork.requestDispatch(string);
            Log.d(TAG, probeOut);

            out = new JsonParser().parse(probeOut).getAsJsonObject();

            int code = out.get(APIConstants.CODE).getAsInt();
            Log.d(TAG, Integer.toString(code));

            listJsonArray = out.get("list").getAsJsonArray();
            Log.d(TAG, listJsonArray.toString());

            Gson gson = new Gson();
            Type listType = new TypeToken<Collection<ProbeListInfo>>() {
            }.getType();
            deviceCollectionList = (Collection<ProbeListInfo>) gson.fromJson(listJsonArray, listType);
            result = new ArrayList<>(deviceCollectionList);

            if (result.size() > 0) {
                for (int j = 0; j < result.size(); j++) {
                    //   mac_save = result.get(j).getMac();
                    name = result.get(j).getName();
                    type = result.get(j).getType();
                    lock = result.get(j).getLock();
                    passwrd = result.get(j).getPassword();
                    id = result.get(j).getId();
                    subdevice = result.get(j).getSubdevice();
                    key = result.get(j).getKey();

                    Log.d("cnt", String.valueOf(db.getListCount()));
                }
            } else {
            }
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            if (CheckNetworkStatus.isInternetAvailable(context)) {
                Toast.makeText(context, "Network h", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(ProbeListActivity.this, FragmentActivity.class);
                in.putStringArrayListExtra("mac", mac_addr);
                in.putStringArrayListExtra("name" , device_name);
                in.putExtra("count",db.getListCount());
                in.putExtra("position",i);
                startActivity(in);
            } else {
                Toast.makeText(context, "Check Your Network Connection", Toast.LENGTH_SHORT).show();
            }
        }

        class CheckDeviceState extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(ProbeListActivity.this);
                dialog.setMessage("Checking Status");
            }

            @Override
            protected String doInBackground(String... params) {

                JsonObject in = new JsonObject();
                JsonObject out = new JsonObject();

                in.addProperty(APIConstants.API_ID, 16);
                in.addProperty(APIConstants.COMMAND, "device_state");
                //  in.addProperty("mac",mac_save);

                String string = in.toString();
                String outString = mBlNetwork.requestDispatch(string);

                out = new JsonParser().parse(outString).getAsJsonObject();
                int code = out.get(APIConstants.CODE).getAsInt();

                final String msg = out.get(APIConstants.MSG).getAsString();
                Log.d(TAG, msg);

                String status = out.get(APIConstants.STATUS).getAsString();
                return status;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

             //   if (s != null)
             //       dev_state = s;
             //   else
              //      dev_state = null;

                Log.d("status", String.valueOf(s));
            }
        }
    }

