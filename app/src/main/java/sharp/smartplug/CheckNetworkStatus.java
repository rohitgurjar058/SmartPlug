package sharp.smartplug;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by Rohit Gurjar on 1/22/2017.
 */

public class CheckNetworkStatus {

    static ConnectivityManager cm;
    static NetworkInfo informationabtnet;
    static NetworkInfo[] netInfo;

    public static boolean isInternetAvailable(Context context) {

        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        boolean connectionavailable = false;

        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getAllNetworkInfo();
        informationabtnet = cm.getActiveNetworkInfo();

        for (NetworkInfo ni : netInfo) {

            try {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected()) haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected()) haveConnectedMobile = true;
                if (informationabtnet.isAvailable()
                        && informationabtnet.isConnected())
                    connectionavailable = true;

                Log.i("ConnectionAvailable", "" + connectionavailable);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
