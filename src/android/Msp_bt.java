package enterprises.nucleus.plugins.multiwii_bluetooth;

import android.util.Log;
import org.apache.cordova.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import enterprises.nucleus.plugins.multiwii_bluetooth.comms.EZGUI;

public class Msp_bt extends CordovaPlugin {

	public static EZGUI multiWiiDevice;
	private static boolean killme;

	
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
			multiWiiDevice.mw.SendRequestMSP(Integer.parseInt(msgCode));
            callbackContext.success(message);

            return true;

        } else 
            
            return false;

    }
	
		    private static Runnable update = new Runnable() {
        @Override
        public void run() {

            multiWiiDevice.mw.ProcessSerialData(multiWiiDevice.loggingON);

//            multiWiiDevice.frskyProtocol.ProcessSerialData(false);
//            if (multiWiiDevice.commFrsky.Connected)
//                setSupportProgress((int) Functions.map(multiWiiDevice.frskyProtocol.TxRSSI, 0, 110, 0, 10000));

            String t = new String();
            if (multiWiiDevice.mw.BaroPresent == 1)
                t += "[BARO] ";
            if (multiWiiDevice.mw.GPSPresent == 1)
                t += "[GPS] ";
            if (multiWiiDevice.mw.multi_Capability.Nav)
                t += "[NAV" + String.valueOf(multiWiiDevice.mw.multi_Capability.getNavVersion(multiWiiDevice.mw.multiCapability)) + "] ";
            if (multiWiiDevice.mw.SonarPresent == 1)
                t += "[SONAR] ";
            if (multiWiiDevice.mw.MagPresent == 1)
                t += "[MAG] ";
            if (multiWiiDevice.mw.AccPresent == 1)
                t += "[ACC]";

            String t1 = "[" + multiWiiDevice.mw.MultiTypeName[multiWiiDevice.mw.multiType] + "] ";
            t1 += "MultiWii " + String.valueOf(multiWiiDevice.mw.version / 100f);

            if (multiWiiDevice.mw.multi_Capability.ByMis)
                t += " by Miœ";

            t1 += "\n" + t + "\n";
            t1 += "selectedProfile" + ":" + String.valueOf(multiWiiDevice.mw.confSetting) + "\n";

            if (multiWiiDevice.mw.ArmCount > 0) {

                int hours = multiWiiDevice.mw.LifeTime / 3600;
                int minutes = (multiWiiDevice.mw.LifeTime % 3600) / 60;
                int seconds = multiWiiDevice.mw.LifeTime % 60;

                String timeString = hours + ":" + minutes + ":" + seconds;

                t1 += "armedCount" + ":" + String.valueOf(multiWiiDevice.mw.ArmCount) + "  " + "liveTime" + ":" + timeString;
            }

//            if (multiWiiDevice.commMW.Connected)
//                MainActivity.multiWiiDeviceendTvConsole(t1);
            //  else
            // MainActivity.multiWiiDeviceendTvConsole(_context.getString(R.string.Disconnected));

//            if (!multiWiiDevice.mw.multi_Capability.Nav) {
//                ((Button) adapter.views.get(0).findViewById(R.id.ButtonNavigation)).setVisibility(View.GONE);
//            } else {
//                ((Button) adapter.views.get(0).findViewById(R.id.ButtonNavigation)).setVisibility(View.VISIBLE);
//            }

            multiWiiDevice.Frequentjobs();
            multiWiiDevice.mw.SendRequest(multiWiiDevice.MainRequestMethod);
			
//            if (!killme)
  //              App.mHandler.postDelayed(update, multiWiiDevice.RefreshRate);

            if (multiWiiDevice.D)
                Log.d(multiWiiDevice.TAG, "loop " + this.getClass().getName());
        }

    };
	
	
}
