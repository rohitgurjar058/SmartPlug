package sharp.smartplug;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Rohit Gurjar on 1/27/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "NetworkInfo";

    // table name
    private static final String TABLE_CREDENTIALS = "WifiCredentials";
    private static final String PROBE_CREDENTIALS = "ProbeCredentials";
    private static final String TIMER_CREDENTIALS = "TimerCredentials";

    // Table Columns names
    private static final String SSID = "ssid";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";
    private static final String MAC = "mac";

    String password;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_WIFI = "CREATE TABLE " + TABLE_CREDENTIALS + "("
                + SSID + " TEXT," + PASSWORD + " TEXT" + ")";

        String PROBE_LIST = "CREATE TABLE " + PROBE_CREDENTIALS + "("
                + NAME + " TEXT," + MAC + " TEXT" + ")";

        String TIMER_LIST = "CREATE TABLE " + TIMER_CREDENTIALS + "("
                + NAME + " TEXT," + MAC + " TEXT" + ")";

        sqLiteDatabase.execSQL(CREATE_WIFI);
        sqLiteDatabase.execSQL(PROBE_LIST);
        sqLiteDatabase.execSQL(TIMER_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CREDENTIALS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PROBE_CREDENTIALS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TIMER_CREDENTIALS);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    void addTimerListCredentials(TimerListCredentialsSave probe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Inserting Row
        db.insert(TIMER_CREDENTIALS, null, values);
        db.close(); // Closing database connection
    }

    void addProbeListCredentials(ProbeListCredentialsSave probe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, probe.getName());
        values.put(MAC, probe.getMac());

        // Inserting Row
        db.insert(PROBE_CREDENTIALS, null, values);
        db.close(); // Closing database connection
    }

    void addCredentials(WifiCredentialsSave wifi) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SSID, wifi.getSsid());
        values.put(PASSWORD, wifi.getPassword());

        // Inserting Row
        db.insert(TABLE_CREDENTIALS, null, values);
        db.close(); // Closing database connection
    }

    public String getPassword(String ssid) {

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select password from "+TABLE_CREDENTIALS+" "+"where"+" "+SSID+""+"=?";
        Cursor cursor = db.rawQuery(query,new String[]{ssid});
        if (cursor != null) {
            cursor.moveToFirst();
        }
        else{}

        for(int i=0;i<cursor.getCount();i++)
            password = cursor.getString(i);

        return password;
    }

    public int getDataCount() {
        String countQuery = "SELECT * FROM " + TABLE_CREDENTIALS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor.getCount();
    }

    public int getListCount() {
        String countQuery = "SELECT * FROM " + PROBE_CREDENTIALS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor.getCount();
    }
}
