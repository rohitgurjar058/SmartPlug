package sharp.smartplug;

/**
 * Created by Rohit Gurjar on 1/22/2017.
 */

public class APIConstants {

    //Broadlink standard input parameters
    public static final String API_ID = "api_id";
    public static final String COMMAND = "command";
    public static final String LICENSE = "license";
    public static final String LICENSE_TYPE = "type_license";
    public static final String GATEWAY_ADDRESS = "dst";
    public static final String TYPE = "type";
    public static final String PASSWORD = "password";
    public static final String ID = "id";
    public static final String SUBDEVICE = "subdevice";
    public static final String KEY = "key";

    //Broadlink standard output parameters
    public static final String CODE = "code";
    public static final String MSG = "msg";
    public static final String STATUS = "status";
    public static final String LOCK = "lock";
    public static final String NAME = "name";
    public static final String MAC = "mac";
    public static final String MASK = "mask";

    //Broadlink devices
    public static final String SP2 = "SP2";

    //Broadlink command strings
    public static final String CMD_NETWORK_INIT = "network_init";
    public static final String CMD_PROBE_LIST = "probe_list";
    public static final String CMD_DEVICE_ADD = "device_add";
    public static final String CMD_DEVICE_UPDATE = "device_update";
    public static final String CMD_DEVICE_DELETE = "device_delete";
    public static final String CMD_DEVICE_STATE = "device_state";
    public static final String CMD_SP2_REFRESH = "sp2_refresh";
    public static final String CMD_SP2_CONTROL = "sp2_control";
    public static final String CMD_SP2_CURRENT_POWER = "sp2_current_power";
    public static final String CMD_EASY_CONFIG = "easyconfig";

    //Broadlink command api_id
    public static final int CMD_NETWORK_INIT_ID = 1;
    public static final int CMD_PROBE_LIST_ID = 11;
    public static final int CMD_DEVICE_ADD_ID = 12;
    public static final int CMD_DEVICE_UPDATE_ID = 13;      //Update device name, after being added
    public static final int CMD_DEVICE_DELETE_ID = 14;
    public static final int CMD_DEVICE_STATE_ID = 16;       //wifi or cloud state
    public static final int CMD_SP2_REFRESH_ID = 71;
    public static final int CMD_SP2_CONTROL_ID = 72;
    public static final int CMD_SP2_CURRENT_POWER_ID = 74;
    public static final int CMD_EASY_CONFIG_ID = 10000;
}
