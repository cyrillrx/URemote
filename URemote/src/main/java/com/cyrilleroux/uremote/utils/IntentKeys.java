package com.cyrilleroux.uremote.utils;

/**
 * Class that hosts the intent keys of the application.
 *
 * @author Cyril Leroux
 *         Created before first commit (08/04/12).
 */
public class IntentKeys {
    /** The request type key. */
    public static final String REQUEST_TYPE = "REQUEST_TYPE";

    /** The request code key. */
    public static final String REQUEST_CODE = "REQUEST_CODE";

    /** Key related to a path value */
    public static final String DIRECTORY_PATH = "directory_path";

    /** Device Address key. */
    public static final String EXTRA_DEVICE_ADDRESS = "device_address";
    public static final String EXTRA_SERVER_DATA = "server_data";
    public static final String EXTRA_SERVER_ACTION = "server_action";
    public static final String EXTRA_SERVER_ID = "server_id";
    public static final String EXTRA_SERVER_CONF_FILE = "server_conf_file";
    public static final String EXTRA_APPLICATION_LIST = "application_list";

    public static final String ACTION_SELECT = "android.intent.action.SELECT";
    public static final String ACTION_ADD = "android.intent.action.ADD";
    public static final String ACTION_SAVE = "android.intent.action.SAVE";
    public static final String ACTION_LOAD = "android.intent.action.LOAD";

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Broadcasts constants
    //	public static final String BROADCAST_CHECK_SERVER_STATE = "server_state";
    //	public static final String BROADCAST_SERVER_IS_ON = "server_is_on";
    //	public static final String BROADCAST_SERVER_IS_OFF = "server_is_off";
    //
    //	public static final String DIR_PATH		= "directory_path";
    //	public static final String DIR_CONTENT	= "directory_content";
}
