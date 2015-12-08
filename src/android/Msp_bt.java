package enterprises.nucleus.plugins.multiwii_bluetooth;

import android.util.Log;
import org.apache.cordova.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import enterprises.nucleus.plugins.multiwii_bluetooth.comms.EZGUI;

public class Msp_bt extends CordovaPlugin {

	public static EZGUI multiWiiDevice;
	
    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

		if (action.equals("getDevices")) {
            Log.i("msp_bt","getDevices");
//			JSONObject json = new JSONObject();
//			json.put("foo", "bar");
			JSONArray json = new JSONArray();
			json.put("foo");
			json.put("bar");
            callbackContext.success(json);

            return true;

        } else if (action.equals("connect")) {
            Log.i("msp_bt","connect");
            String deviceId = data.getString(0);
            String message = "Connect to " + deviceId;
			multiWiiDevice = new EZGUI(this.cordova.getActivity().getApplicationContext(), deviceId);
//			ezgui.commMW.connect(deviceId, (int)0); 
//			ezgui.init(); 
            callbackContext.success(message);

            return true;

        } else if (action.equals("disconnect")) {
            Log.i("msp_bt","disconnect");
            String deviceId = data.getString(0);
            String message = "disconnect " + deviceId;
            callbackContext.success(message);

            return true;

        } else if (action.equals("sendMessage")) {
            Log.i("msp_bt","sendMessage");
            String msgCode = data.getString(0);
            String message = "send message " + msgCode;
			multiWiiDevice.mw.SendRequestMSP(msgCode);
            callbackContext.success(message);

            return true;

        } else 
            
            return false;

    }
}
