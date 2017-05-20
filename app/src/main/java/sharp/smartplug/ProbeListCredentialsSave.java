package sharp.smartplug;

/**
 * Created by Rohit Gurjar on 2/19/2017.
 */

public class ProbeListCredentialsSave {

    String name;
    String mac;

    public ProbeListCredentialsSave(String name, String mac) {
        this.name = name;
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
