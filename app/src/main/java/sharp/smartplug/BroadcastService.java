package sharp.smartplug;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blnetwork.BLNetwork;

public class BroadcastService extends Service {

	private static final String TAG = "BroadcastService";
	public static final String BROADCAST_ACTION = "sharp.smartplug.displayevent";
	private final Handler handler = new Handler();
	Intent intent;
	static BLNetwork mBlNetwork;
	private static String device_state;
	Context context = BroadcastService.this;
	DatabaseHandler db;
	int status;
	String ssid,password;
	List<String> mac_addr = new ArrayList<String>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		db = new DatabaseHandler(getApplicationContext());
		mBlNetwork = BLNetwork.getInstanceBLNetwork(getApplicationContext());
		intent = new Intent(BROADCAST_ACTION);
	}

    @Override
    public void onStart(Intent intent, int startId) {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 2000); // 1 second
    }

    private Runnable sendUpdatesToUI = new Runnable() {
    	public void run() {
    		DisplayLoggingInfo();    		
    	    handler.postDelayed(this, 3000); // 3 seconds
    	}
    };    
    
    private void DisplayLoggingInfo() {
    	Log.d(TAG, "Checking...");

		for(int m=0;m<db.getListCount();m++)
		{
				intent.putExtra("devicestate", sp3_device_state(m));
				intent.putExtra("statusSwitch", sp3_refresh(m));
				sendBroadcast(intent);
		}
    }

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {		
        handler.removeCallbacks(sendUpdatesToUI);		
		super.onDestroy();
	}

	public String getMac(String TableName,int m) {

		DatabaseHandler helper = new DatabaseHandler(this);
		String Query = "select mac from "+TableName;
		SQLiteDatabase sqldb = helper.getReadableDatabase();
		Cursor cursor = sqldb.rawQuery(Query,null);

		if (cursor != null) {
			cursor.moveToFirst();
		}

		String mac = cursor.getString(m);
		cursor.close();
		return mac;
	}

	public int sp3_refresh(int index)
	{
		JsonObject in = new JsonObject();
		JsonObject out = new JsonObject();

		in.addProperty(APIConstants.API_ID, 82);
		in.addProperty(APIConstants.COMMAND,"sp3_refresh");
		in.addProperty("mac",getMac("ProbeCredentials",index));

		String string = in.toString();
		String outString = mBlNetwork.requestDispatch(string);
		out = new JsonParser().parse(outString).getAsJsonObject();
		int code = out.get(APIConstants.CODE).getAsInt();
		status = out.get(APIConstants.STATUS).getAsInt();
		return status;
	}

	protected int initialize(final String ssid, final String password) {

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

		return code;
	}

	public String sp3_device_state(int index)
	{
		JsonObject in = new JsonObject();
		JsonObject out = new JsonObject();

		in.addProperty(APIConstants.API_ID,16);
		in.addProperty(APIConstants.COMMAND,"device_state");
		in.addProperty("mac",getMac("ProbeCredentials",index));

		String string = in.toString();
		String outString = mBlNetwork.requestDispatch(string);
		out = new JsonParser().parse(outString).getAsJsonObject();
		Log.d("out", String.valueOf(out));

		int code = out.get(APIConstants.CODE).getAsInt();
		Log.d(TAG, String.valueOf(code));

		final String status = out.get(APIConstants.STATUS).getAsString();
		Log.d(TAG, String.valueOf(status));

		if(status != null)
			return status;
		else
			return null;
	}
}
