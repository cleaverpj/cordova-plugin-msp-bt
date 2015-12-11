package enterprises.nucleus.plugins.multiwii_bluetooth;

import android.util.Log;
import org.apache.cordova.*;
import org.json.JSONObject;
import android.os.Handler;
import org.json.JSONArray;
import org.json.JSONException;

import enterprises.nucleus.plugins.multiwii_bluetooth.comms.EZGUI;

public class Msp_bt extends CordovaPlugin {

	private static final int refreshRate = 100;
	private static Handler mHandler;
	private static boolean killme;

	public static EZGUI multiWiiDevice;
	
    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

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
            String deviceId = args.getString(0);
            String message = "Connect to " + deviceId;
			multiWiiDevice = new EZGUI(this.cordova.getActivity().getApplicationContext(), deviceId);
			
			mHandler = new Handler();
			mHandler.postDelayed(update, refreshRate);
            callbackContext.success(message);

            return true;

        } else if (action.equals("disconnect")) {
            Log.i("msp_bt","disconnect");
            String deviceId = args.getString(0);
            String message = "disconnect " + deviceId;
            callbackContext.success(message);

            return true;
 
        } else if (action.equals("sendMessage")) {
            Log.i("msp_bt","sendMessage");
            String msgCode = args.getString(0);
            String message = "send message " + msgCode;
			multiWiiDevice.mw.SendRequestMSP(Integer.parseInt(msgCode));
            callbackContext.success(message);
 
            return true;

        } else if (action.equals("setWaypoint")) {
            Log.i("msp_bt","setWaypoints");
            String msgCode = args.getString(0);
            String message = "send message " + msgCode;
			Log.i("msp_bt","TODO set waypoint");
            callbackContext.success(message);
 
            return true;

        } else if (action.equals("setRC")) {
            Log.i("msp_bt","setRC");
            String msgCode = args.getString(0);
            String message = "send message " + msgCode;
			Log.i("msp_bt","TODO set RC");
            callbackContext.success(message);
 
            return true;

        } else if (action.equals("setHeading")) {
            Log.i("msp_bt","setHeading");
            Integer heading = Integer.parseInt(args.getString(0));
            String message = "send message " + heading;
			multiWiiDevice.mw.SendRequestMSP_SET_HEAD(heading);
            callbackContext.success(message);
 
            return true;

        } else if (action.equals("setMode")) {
            Log.i("msp_bt","setMode");
            String msgCode = args.getString(0);
            String message = "send message " + msgCode;
			Log.i("msp_bt","TODO setMode");
            callbackContext.success(message);
 
            return true;

        } else if (action.equals("setSensitivity")) {
            Log.i("msp_bt","setSensitivity");
            String msgCode = args.getString(0);
            String message = "send message " + msgCode;
			Log.i("msp_bt","TODO setSensitivity");
            callbackContext.success(message);

            return true;

        } else if (action.equals("getData")) {
            Log.i("msp_bt","sendMessage");
            int msgCode = Integer.parseInt(args.getString(0));
			JSONObject json = new JSONObject();
			if(msgCode == multiWiiDevice.mw.MSP_IDENT) {
				json.put("version", multiWiiDevice.mw.version);
				json.put("multiType", multiWiiDevice.mw.multiType);
				json.put("MSPversion", multiWiiDevice.mw.MSPversion);					
			} else if(msgCode == multiWiiDevice.mw.MSP_STATUS) {
				json.put("cycleTime", multiWiiDevice.mw.cycleTime);
				json.put("i2cError", multiWiiDevice.mw.i2cError);
				json.put("mode", multiWiiDevice.mw.mode);
				json.put("confSetting", multiWiiDevice.mw.confSetting);
				json.put("AccPresent", multiWiiDevice.mw.AccPresent);
				json.put("BaroPresent", multiWiiDevice.mw.BaroPresent);
				json.put("MagPresent", multiWiiDevice.mw.MagPresent);
				json.put("GPSPresent", multiWiiDevice.mw.GPSPresent);
				json.put("SonarPresent", multiWiiDevice.mw.SonarPresent);
				json.put("MSPversion", multiWiiDevice.mw.MSPversion);
				json.put("CHECKBOXITEMS", multiWiiDevice.mw.CHECKBOXITEMS);
//					ActiveModes[i]
			} else if(msgCode == multiWiiDevice.mw.MSP_RAW_IMU) {
				json.put("ax", multiWiiDevice.mw.ax);
				json.put("ay", multiWiiDevice.mw.ay);
				json.put("az", multiWiiDevice.mw.az);
				json.put("gx", multiWiiDevice.mw.gx);
				json.put("gy", multiWiiDevice.mw.gy);
				json.put("gz", multiWiiDevice.mw.gz);
				json.put("magx", multiWiiDevice.mw.magx);
				json.put("magy", multiWiiDevice.mw.magy);
				json.put("magz", multiWiiDevice.mw.magz);
			} else if(msgCode == multiWiiDevice.mw.MSP_SERVO) {
				json.put("servo1", multiWiiDevice.mw.servo[0]);
				json.put("servo2", multiWiiDevice.mw.servo[1]);
				json.put("servo3", multiWiiDevice.mw.servo[2]);
				json.put("servo4", multiWiiDevice.mw.servo[3]);
				json.put("servo5", multiWiiDevice.mw.servo[4]);
				json.put("servo6", multiWiiDevice.mw.servo[5]);
				json.put("servo7", multiWiiDevice.mw.servo[6]);
				json.put("servo8", multiWiiDevice.mw.servo[7]);
			} else if(msgCode == multiWiiDevice.mw.MSP_MOTOR) {
				json.put("motor1", multiWiiDevice.mw.mot[0]);
				json.put("motor2", multiWiiDevice.mw.mot[1]);
				json.put("motor3", multiWiiDevice.mw.mot[2]);
				json.put("motor4", multiWiiDevice.mw.mot[3]);
				json.put("motor5", multiWiiDevice.mw.mot[4]);
				json.put("motor6", multiWiiDevice.mw.mot[5]);
				json.put("motor7", multiWiiDevice.mw.mot[6]);
				json.put("motor8", multiWiiDevice.mw.mot[7]);
			} else if(msgCode == multiWiiDevice.mw.MSP_RC) {
				json.put("rcRoll", multiWiiDevice.mw.rcRoll);
				json.put("rcPitch", multiWiiDevice.mw.rcPitch);
				json.put("rcYaw", multiWiiDevice.mw.rcYaw);
				json.put("rcThrottle", multiWiiDevice.mw.rcThrottle);
				json.put("rcAUX1", multiWiiDevice.mw.rcAUX1);
				json.put("rcAUX2", multiWiiDevice.mw.rcAUX2);
				json.put("rcAUX3", multiWiiDevice.mw.rcAUX3);
				json.put("rcAUX4", multiWiiDevice.mw.rcAUX4);
			} else if(msgCode == multiWiiDevice.mw.MSP_RAW_GPS) {
				json.put("GPS_fix", multiWiiDevice.mw.GPS_fix);
				json.put("GPS_numSat", multiWiiDevice.mw.GPS_numSat);
				json.put("GPS_latitude", multiWiiDevice.mw.GPS_latitude);
				json.put("GPS_longitude", multiWiiDevice.mw.GPS_longitude);
				json.put("GPS_altitude", multiWiiDevice.mw.GPS_altitude);
				json.put("GPS_speed", multiWiiDevice.mw.GPS_speed);
				json.put("GPS_ground_course", multiWiiDevice.mw.GPS_ground_course);
			} else if(msgCode == multiWiiDevice.mw.MSP_COMP_GPS) {
				json.put("GPS_distanceToHome", multiWiiDevice.mw.GPS_distanceToHome);
				json.put("GPS_directionToHome", multiWiiDevice.mw.GPS_directionToHome);
				json.put("GPS_update", multiWiiDevice.mw.GPS_update);
			} else if(msgCode == multiWiiDevice.mw.MSP_ATTITUDE) {
				json.put("ang_x", multiWiiDevice.mw.ang_x);
				json.put("attitude_y", multiWiiDevice.mw.ang_y);
				json.put("heading", multiWiiDevice.mw.head);
				// another way, only needed for async stuff??
				//				PluginResult result = new PluginResult(PluginResult.Status.OK, json);
				//				result.setKeepCallback(true);
				//				callbackContext.sendPluginResult(result);	
			} else if(msgCode == multiWiiDevice.mw.MSP_ALTITUDE) {
				json.put("alt", multiWiiDevice.mw.alt);
				json.put("vario", multiWiiDevice.mw.vario);
			} else if(msgCode == multiWiiDevice.mw.MSP_ANALOG) {
				json.put("bytevbat", multiWiiDevice.mw.bytevbat);
				json.put("pMeterSum", multiWiiDevice.mw.pMeterSum);
				json.put("rssi", multiWiiDevice.mw.rssi);
				json.put("amperage", multiWiiDevice.mw.amperage);
			} else if(msgCode == multiWiiDevice.mw.MSP_RC_TUNING) {
				json.put("byteRC_RATE", multiWiiDevice.mw.byteRC_RATE);
				json.put("byteRC_EXPO", multiWiiDevice.mw.byteRC_EXPO);
				json.put("byteRollPitchRate", multiWiiDevice.mw.byteRollPitchRate);
				json.put("byteYawRate", multiWiiDevice.mw.byteYawRate);
				json.put("byteDynThrPID", multiWiiDevice.mw.byteDynThrPID);
				json.put("byteThrottle_MID", multiWiiDevice.mw.byteThrottle_MID);
				json.put("byteThrottle_EXPO", multiWiiDevice.mw.byteThrottle_EXPO);
			} else if(msgCode == multiWiiDevice.mw.MSP_ACC_CALIBRATION) {
			} else if(msgCode == multiWiiDevice.mw.MSP_MAG_CALIBRATION) {
			} else if(msgCode == multiWiiDevice.mw.MSP_PID) {
				for (int i = 0; i < multiWiiDevice.mw.PIDITEMS; i++) {
					json.put("P" + i, multiWiiDevice.mw.byteP[i]);
					json.put("I" + i, multiWiiDevice.mw.byteI[i]);
					json.put("D" + i, multiWiiDevice.mw.byteD[i]);
				}
			} else if(msgCode == multiWiiDevice.mw.MSP_BOX) {
				for (int i = 0; i < multiWiiDevice.mw.CHECKBOXITEMS; i++) {
					json.put("activation" + i,  multiWiiDevice.mw.activation[i]);
					for (int aa = 0; aa < 12; aa++) {
						json.put("checkbox" + i + "_" + aa,  multiWiiDevice.mw.Checkbox[i][aa]);
					}
				}
			} else if(msgCode == multiWiiDevice.mw.MSP_BOXNAMES) {
			} else if(msgCode == multiWiiDevice.mw.MSP_PIDNAMES) {
			} else if(msgCode == multiWiiDevice.mw.MSP_SERVO_CONF) {
			} else if(msgCode == multiWiiDevice.mw.MSP_MISC) {
				json.put("intPowerTrigger", multiWiiDevice.mw.intPowerTrigger);
				json.put("minthrottle", multiWiiDevice.mw.minthrottle);
				json.put("maxthrottle", multiWiiDevice.mw.maxthrottle);
				json.put("mincommand", multiWiiDevice.mw.mincommand);
				json.put("failsafe_throttle", multiWiiDevice.mw.failsafe_throttle);
				json.put("ArmCount", multiWiiDevice.mw.ArmCount);
				json.put("LifeTime", multiWiiDevice.mw.LifeTime);
				json.put("mag_decliniation", multiWiiDevice.mw.mag_decliniation);
				json.put("vbatscale", multiWiiDevice.mw.vbatscale);
				json.put("vbatlevel_warn1", multiWiiDevice.mw.vbatlevel_warn1);
				json.put("vbatlevel_warn2", multiWiiDevice.mw.vbatlevel_warn2);
				json.put("vbatlevel_crit", multiWiiDevice.mw.vbatlevel_crit);
			} else if(msgCode == multiWiiDevice.mw.MSP_MOTOR_PINS) {
				for (int i = 0; i < 8; i++) {
					json.put("byteMP" + i, multiWiiDevice.mw.byteMP[i]);
				}
			} else if(msgCode == multiWiiDevice.mw.MSP_DEBUG) {
				json.put("debug1", multiWiiDevice.mw.debug1);
				json.put("debug2", multiWiiDevice.mw.debug2);
				json.put("debug3", multiWiiDevice.mw.debug3);
				json.put("debug4", multiWiiDevice.mw.debug4);
			} else if(msgCode == multiWiiDevice.mw.MSP_DEBUGMSG) {					
			} else if(msgCode == multiWiiDevice.mw.MSP_WP) {
			} else if(msgCode == multiWiiDevice.mw.MSP_NAV_CONFIG) {
				json.put("NAVmaxWpNumber", multiWiiDevice.mw.NAVmaxWpNumber);				
			} else if(msgCode == multiWiiDevice.mw.MSP_NAV_STATUS) {
				json.put("NAVGPSMode", multiWiiDevice.mw.NAVGPSMode);
				json.put("NAVstate", multiWiiDevice.mw.NAVstate);
				json.put("NAVcurrentAction", multiWiiDevice.mw.NAVcurrentAction);
				json.put("NAVcurrentWPNumber", multiWiiDevice.mw.NAVcurrentWPNumber);
				json.put("NAVerror", multiWiiDevice.mw.NAVerror);
				json.put("NAVoriginalAltitude", multiWiiDevice.mw.NAVoriginalAltitude);
				json.put("NAVtargetAltitude", multiWiiDevice.mw.NAVtargetAltitude);
				json.put("NAValtToHold", multiWiiDevice.mw.NAValtToHold);
				json.put("NAValtChangeFlag", multiWiiDevice.mw.NAValtChangeFlag);
			}	
			callbackContext.success(json); 
            return true;

        } else 
            
            return false;
    }
	
	private static Runnable update = new Runnable() {
        @Override
        public void run() {

            multiWiiDevice.mw.ProcessSerialData(multiWiiDevice.loggingON);

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

            multiWiiDevice.Frequentjobs();
            multiWiiDevice.mw.SendRequest(multiWiiDevice.MainRequestMethod);
			
            if (!killme)
                mHandler.postDelayed(update, refreshRate);

            if (multiWiiDevice.D)
                Log.d(multiWiiDevice.TAG, "loop " + this.getClass().getName());
        }
    };
}
