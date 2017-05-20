package sharp.smartplug;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.com.broadlink.blnetwork.BLNetwork;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    BLNetwork mBlNetwork;
    private static final String TAG = "MainActivity";
    private Context context = MainActivity.this;
    String ssid,password;
    Button btn;

    TextView config_message;
    ImageView configure,cancel;
    EditText editText_ssid,editText_password;
    private AVLoadingIndicatorView configure_progress;
    String indicator;
    Animation zoom_in;
    SharedPreferences prefs;

    private Collection<ProbeListInfo> deviceCollectionList;
    List<ProbeListInfo> result;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DatabaseHandler(getApplicationContext());
        prefs = getSharedPreferences("preference", Context.MODE_PRIVATE);
        mBlNetwork = BLNetwork.getInstanceBLNetwork(MainActivity.this);
        initView();
        initListeners();
    }

    private void initListeners() {

        configure.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    private void initView() {
        zoom_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);

        editText_ssid = (EditText)findViewById(R.id.editText_ssid);
        editText_password = (EditText)findViewById(R.id.editText_password);

        configure = (ImageView)findViewById(R.id.configure);
        cancel = (ImageView)findViewById(R.id.cancel);
        configure_progress = (AVLoadingIndicatorView) findViewById(R.id.loading_view);
        config_message = (TextView)findViewById(R.id.config_message);
    }

    @Override
    protected void onResume() {
        super.onResume();

        NetworkUtil networkUtil = new NetworkUtil(MainActivity.this);
        networkUtil.startScan();
        String ssid = networkUtil.getWiFiSSID();
        editText_ssid.setText(ssid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id)
        {
            case R.id.configure :
                if(NetworkUtil.iswifiConnected()) {
                    configure_ui();
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            ssid = editText_ssid.getText().toString();
                            password = editText_password.getText().toString();
                            easyConfig(ssid, password);
                        }
                    }).start();
                    config_message.setText("Configuring...  Please wait for a minute");
                }
                else{
                    Toast.makeText(this,"Please Enable Your Wifi",Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.cancel :
                if(NetworkUtil.iswifiConnected()) {
                    cancel_ui();
                    cancelConfig();
                }
                break;
        }
    }

    private void cancel_ui() {
        configure.setAnimation(zoom_in);
        configure.startAnimation(zoom_in);
        cancel.setVisibility(View.INVISIBLE);
        configure_progress.hide();
        config_message.setVisibility(View.INVISIBLE);
        configure.setVisibility(View.VISIBLE);
    }

    private void configure_ui() {
        configure.setAnimation(zoom_in);
        configure.startAnimation(zoom_in);
        configure.setVisibility(View.INVISIBLE);
        configure_progress.smoothToShow();
        config_message.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.VISIBLE);
    }

    private void cancelConfig() {

        JsonObject in = new JsonObject();

        in.addProperty(APIConstants.API_ID,10001);
        in.addProperty(APIConstants.COMMAND,"cancel_easyconfig");

        String string = in.toString();
        mBlNetwork.requestDispatch(string);
    }

    private void easyConfig(String ssid, String password) {

        initialize(ssid,password);
        finalconfig(ssid,password);
    }

    protected void initialize(final String ssid, final String password) {

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
                    configure_ui();
                }
            });
        } else {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context,"Network Not Initialized" , Toast.LENGTH_LONG).show();
                    cancel_ui();
                }
            });
        }
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

        db = new DatabaseHandler(getApplicationContext());

        for(int j=0;j<result.size();j++)
        {
            if(!CheckIsListAlreadyInDBorNot("ProbeCredentials","mac",result.get(j).getMac()))
                db.addProbeListCredentials(new ProbeListCredentialsSave(result.get(j).getName(), result.get(j).getMac()));

            if(CheckIsListAlreadyInDBorNot("ProbeCredentials","mac",result.get(0).getMac())) {
                Log.d("data", "h");
                Log.d("data",getName("ProbeCredentials").get(0));
            }
            else
            Log.d("data"," nahi  h");

        }
        Log.d("size", String.valueOf(result.size()));
    }

    public List<String> getName(String TableName) {

        List<String> device_name = new ArrayList<String>();
        DatabaseHandler helper = new DatabaseHandler(this);
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

    public boolean CheckIsListAlreadyInDBorNot(String TableName, String mac, String macValue) {

        DatabaseHandler helper = new DatabaseHandler(this);
        String Query = "select * from "+TableName+" where " +mac+ "=?";
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        Cursor cursor = sqldb.rawQuery(Query, new String[]{macValue});

        Log.d("Check",""+cursor.getCount());

        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    private void finalconfig(final String ssid, final String password) {

        JsonObject in = new JsonObject();
        JsonObject out = new JsonObject();

        in.addProperty(APIConstants.API_ID, 10000);
        in.addProperty(APIConstants.COMMAND, "easyconfig");
        in.addProperty("ssid", ssid);
        in.addProperty("password", password);
        in.addProperty("broadlinkv2", 1);
        in.addProperty(APIConstants.GATEWAY_ADDRESS,"192.168.3.1");

        String string = in.toString();
        Log.d(TAG,string);

        String outString = mBlNetwork.requestDispatch(string);
        Log.d(TAG,outString);

        out = new JsonParser().parse(outString).getAsJsonObject();

        int code = out.get(APIConstants.CODE).getAsInt();
        Log.d(TAG,Integer.toString(code));

        final String msg = out.get(APIConstants.MSG).getAsString();
        Log.d(TAG,msg);

        if (0 == code) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("instance",true);
                    editor.commit();
                    Toast.makeText(context, "EasyConfig Success", Toast.LENGTH_LONG).show();

                    if(!CheckIsDataAlreadyInDBorNot("WifiCredentials","ssid",ssid,"password",password))
                    {
                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        db.addCredentials(new WifiCredentialsSave(ssid,password));
                        int count = db.getDataCount();
                        Log.d(TAG, String.valueOf(count));
                    }
                    else {Log.d(TAG,"data nahi h");}

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            probe_list();
                        }
                    }, 3000);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(MainActivity.this, ProbeListActivity.class);
                            startActivity(i);
                        }
                    }, 3000);

                    cancel_ui();
                }
            });
        }
        else
        {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, "Reset your SP device and then Reconfigure", Toast.LENGTH_LONG).show();
                    cancel_ui();
                }
            });
        }
    }

    public boolean CheckIsDataAlreadyInDBorNot(String TableName, String ssid, String ssidValue,String password,String passwordValue) {

        DatabaseHandler helper = new DatabaseHandler(this);
        String Query = "select ssid from "+TableName+" where " +ssid+ "=?" + " and " +password+"=?";
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        Cursor cursor = sqldb.rawQuery(Query, new String[]{ssidValue,passwordValue});
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}