package sharp.smartplug;

/**
 * Created by Rohit Gurjar on 1/28/2017.
 */

public class WifiCredentialsSave {

    String ssid;
    String password;

    // Empty constructor
    public WifiCredentialsSave(){

    }

    // constructor
    public WifiCredentialsSave(String ssid, String password){
        this.ssid = ssid;
        this.password = password;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
