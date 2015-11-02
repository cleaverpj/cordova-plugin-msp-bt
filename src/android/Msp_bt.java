package enterprises.nucleus.plugin;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class Msp_bt extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        if (action.equals("greet")) {

            String name = data.getString(0);
            String message = "Hello, " + name;
            callbackContext.success(message);

            return true;

        } else if (action.equals("connect")) {

            String name = data.getString(0);
            String message = "Connect to " + name;
            callbackContext.success(message);

            return true;

        } else {
            
            return false;

        }
    }
}
