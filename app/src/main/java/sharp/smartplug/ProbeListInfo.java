package sharp.smartplug;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rohit Gurjar on 12/26/2016.
 */

public class ProbeListInfo {

    @SerializedName("mac")
    private String mac;

    @SerializedName("type")
    private String type;

    @SerializedName("name")
    private String name;

    @SerializedName("lock")
    private String lock;

    @SerializedName("password")
    private String password;

    @SerializedName("id")
    private String id;

    @SerializedName("subdevice")
    private String subdevice;

    @SerializedName("key")
    private String key;

    public ProbeListInfo(String mac, String type, String name, String lock, String password, String id, String subdevice,String key) {
        this.mac = mac;
        this.type = type;
        this.name = name;
        this.lock = lock;
        this.password = password;
        this.id = id;
        this.subdevice = subdevice;
        this.key = key;
    }

    public String getMac() {  return mac; }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getLock() {
        return lock;
    }

    public String getPassword() {
        return password;
    }

    public String getId() {
        return id;
    }

    public String getSubdevice() {
        return subdevice;
    }

    public String getKey() {
        return key;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLock(String lock) {
        this.lock = lock;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSubdevice(String subdevice) {
        this.subdevice = subdevice;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
