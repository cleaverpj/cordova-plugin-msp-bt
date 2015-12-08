/*  MultiWii EZ-GUI
    Copyright (C) <2012>  Bartosz Szczygiel (eziosoft)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package enterprises.nucleus.plugins.multiwii_bluetooth.comms;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//import com.ezio.multiwii.waypoints.Waypoint;
//import communication.Communication;

public class MultiWii230NAV extends MultirotorData {

	public MultiWii230NAV(Communication bt) {
		EZGUIProtocol = "2.3 NAV";

	//	timer1 = 10; // used to send request every 10 requests
	//	timer2 = 0; // used to send requests once after connection

		this.communication = bt;

		PIDITEMS = 10;
		CHECKBOXITEMS = 0;
		byteP = new int[PIDITEMS];
		byteI = new int[PIDITEMS];
		byteD = new int[PIDITEMS];
		BoxNames = new String[0];
		init();
	}

	private void init() {
		CHECKBOXITEMS = BoxNames.length;
		activation = new int[CHECKBOXITEMS];
		ActiveModes = new boolean[CHECKBOXITEMS];
		Checkbox = new Boolean[CHECKBOXITEMS][12];
		ResetAllChexboxes();
	}

	private void ResetAllChexboxes() {
		for (int i = 0; i < BoxNames.length; i++) {
			for (int j = 0; j < 12; j++) {
				Checkbox[i][j] = false;
			}
		}
	}

    int timer3 = -1;

    int[] requests = new int[] { 0, MSP_ATTITUDE  };
    final int[] requestsOnce = new int[] { MSP_IDENT, MSP_BOXNAMES, MSP_PID, MSP_BOX, MSP_MISC, MSP_NAV_CONFIG };
    int[] requestsPeriodical = new int[] { MSP_ATTITUDE, MSP_RC};
//    int[] requestsPeriodical = new int[] { MSP_ALTITUDE, MSP_RAW_GPS, MSP_BOX, MSP_RAW_IMU, MSP_STATUS, MSP_COMP_GPS, MSP_ANALOG, MSP_SERVO, MSP_MOTOR, MSP_RC, MSP_DEBUG };

    public void SendRequest2() {

        if (communication.Connected) {

            // MSP_WP - in App.java

            if (CHECKBOXITEMS == 0)
                timer3 = -1;

            // Log.d("aaa", "timer3=" + String.valueOf(timer3));
            switch (timer3) {
                case -1:
                    sendRequestMSP(requestMSP(requestsOnce));
                    break;

                default:
                    requests[0] = (requestsPeriodical[timer3]);
                    sendRequestMSP(requestMSP(requests));

                    break;
            }

            timer3++;
            if (timer3 >= requestsPeriodical.length)
                timer3 = 0;

        }

    }

    // send msp without payload
	public List<Byte> requestMSP(int msp) {
		return requestMSP(msp, null);
	}

	// send multiple msp without payload
	private List<Byte> requestMSP(int[] msps) {
		List<Byte> s = new LinkedList<Byte>();
		for (int m : msps) {
			s.addAll(requestMSP(m, null));
		}
		return s;
	}

	// send msp with payload
	public List<Byte> requestMSP(int msp, Character[] payload) {
		if (msp < 0) {
			return null;
		}
		List<Byte> bf = new LinkedList<Byte>();
		for (byte c : MSP_HEADER.getBytes()) {
			bf.add(c);
		}

		byte checksum = 0;
		byte pl_size = (byte) ((payload != null ? (int) (payload.length) : 0) & 0xFF);
		bf.add(pl_size);
		checksum ^= (pl_size & 0xFF);

		bf.add((byte) (msp & 0xFF));
		checksum ^= (msp & 0xFF);

		if (payload != null) {
			for (char c : payload) {
				bf.add((byte) (c & 0xFF));
				checksum ^= (c & 0xFF);
			}
		}
		bf.add(checksum);
		return (bf);
	}

	public void sendRequestMSP(List<Byte> msp) {
		byte[] arr = new byte[msp.size()];
		int i = 0;
		for (byte b : msp) {
			arr[i++] = b;
		}
		Log.d("mw", String.valueOf(arr));
		Log.d("data", String.valueOf(arr[0]));
		Log.d("data", String.valueOf(arr[1]));
		Log.d("data", String.valueOf(arr[2]));
		Log.d("data", String.valueOf(arr[3]));
		Log.d("data", String.valueOf(arr[4]));
		Log.d("data", String.valueOf(arr[5]));
		Log.d("data", String.valueOf(arr[6]));
		Log.d("data", String.valueOf(arr[7]));
		Log.d("data", String.valueOf(arr[8]));
		Log.d("data", String.valueOf(arr[9]));
		Log.d("data", String.valueOf(arr[10]));
		Log.d("data", String.valueOf(arr[11]));
		communication.Write(arr); // send the complete byte sequence in one go
	}

	public void evaluateCommand(byte cmd, int dataSize) {

		int i;
		int icmd = (int) (cmd & 0xFF);
        String msg;
		switch (icmd) {
		case MSP_IDENT:
			version = read8();
			multiType = read8();
			MSPversion = read8(); // MSP version
			multiCapability = read32();// capability
			if ((multiCapability & 1) > 0)
				multi_Capability.RXBind = true;
			if ((multiCapability & 4) > 0)
				multi_Capability.Motors = true;
			if ((multiCapability & 8) > 0)
				multi_Capability.Flaps = true;

			if ((multiCapability & 16) > 0)
				multi_Capability.Nav = true;

			if ((multiCapability & 0x80000000) > 0)
				multi_Capability.ByMis = true;

 //           App.wsServer.sendToAll("{\"id\":" + MSP_IDENT
 //                   + ",\"version\":" + version
 //                   + ",\"multiType\":" + multiType
 //                   + ",\"MSPversion\":" + MSPversion
 //                   + ",\"multiCapability\":" + multiCapability
 //                   + "}");

 //           App.deviceStateRef.child("identity").child("version").setValue(version);
 //           App.deviceStateRef.child("identity").child("multiType").setValue(multiType);
 //           App.deviceStateRef.child("identity").child("MSPversion").setValue(MSPversion);
 //           App.deviceStateRef.child("identity").child("multiCapability").setValue(multiCapability);

            break;

		case MSP_STATUS:
			cycleTime = read16();
			i2cError = read16();
			SensorPresent = read16();
			mode = read32();
			confSetting = read8();

			if ((SensorPresent & 1) > 0)
				AccPresent = 1;
			else
				AccPresent = 0;

			if ((SensorPresent & 2) > 0)
				BaroPresent = 1;
			else
				BaroPresent = 0;

			if ((SensorPresent & 4) > 0)
				MagPresent = 1;
			else
				MagPresent = 0;

			if ((SensorPresent & 8) > 0)
				GPSPresent = 1;
			else
				GPSPresent = 0;

			if ((SensorPresent & 16) > 0)
				SonarPresent = 1;
			else
				SonarPresent = 0;

			for (i = 0; i < CHECKBOXITEMS; i++) {
				if ((mode & (1 << i)) > 0)
					ActiveModes[i] = true;
				else
					ActiveModes[i] = false;

			}
//            App.wsServer.sendToAll("{\"id\":" + MSP_STATUS
 //                   + ",\"cycleTime\":" + cycleTime
 //                   + ",\"i2cError\":" + i2cError
 //                   + ",\"mode\":" + mode
 //                   + ",\"confSetting\":" + confSetting
 //                   + ",\"AccPresent\":" + AccPresent
 //                   + ",\"BaroPresent\":" + BaroPresent
 //                   + ",\"MagPresent\":" + MagPresent
 //                   + ",\"GPSPresent\":" + GPSPresent
 //                   + ",\"SonarPresent\":" + SonarPresent
 //                   + "}");

  /*          App.deviceStateRef.child("status").child("MSP_STATUS").setValue(MSP_STATUS);
            App.deviceStateRef.child("status").child("cycleTime").setValue(cycleTime);
            App.deviceStateRef.child("status").child("i2cError").setValue(i2cError);
            App.deviceStateRef.child("status").child("confSetting").setValue(confSetting);
            App.deviceStateRef.child("status").child("AccPresent").setValue(AccPresent);
            App.deviceStateRef.child("status").child("BaroPresent").setValue(BaroPresent);
            App.deviceStateRef.child("status").child("MagPresent").setValue(MagPresent);
            App.deviceStateRef.child("status").child("GPSPresent").setValue(GPSPresent);
            App.deviceStateRef.child("status").child("SonarPresent").setValue(SonarPresent);
*/
			break;
		case MSP_RAW_IMU:

			ax = read16();
			ay = read16();
			az = read16();

			gx = read16() / 8;
			gy = read16() / 8;
			gz = read16() / 8;

			magx = read16() / 3;
			magy = read16() / 3;
			magz = read16() / 3;
   //         App.wsServer.sendToAll("{\"id\":" + MSP_RAW_IMU + ",\"ax\":" + ax + ",\"ay\":" + ay + ",\"az\":" + az + ",\"gx\":" + gx + ",\"gy\":" + gy + ",\"gz\":" + gz + ",\"magx\":" + magx + ",\"magy\":" + magy + ",\"magz\":" + magz + "}");


            this.status.accelerometer_x = ax;
            this.status.accelerometer_y = ay;
            this.status.accelerometer_z = az;

            App.deviceStateRef.child("imu").child("gyro_x").setValue(gx);
            App.deviceStateRef.child("imu").child("gyro_y").setValue(gy);
            App.deviceStateRef.child("imu").child("gyro_z").setValue(gz);

            App.deviceStateRef.child("imu").child("magnetometer_x").setValue(magx);
            App.deviceStateRef.child("imu").child("magnetometer_y").setValue(magy);
            App.deviceStateRef.child("imu").child("magnetometer_z").setValue(magz);


            break;

		case MSP_SERVO:
            msg = "{\"id\":" + MSP_SERVO;
			for (i = 0; i < 8; i++) {
                servo[i] = read16();
                msg += ",\"servo" + i + "\":" + servo[i];
//                App.deviceStateRef.child("servos").child(Integer.toString(i)).setValue(servo[i]);
            }
     //       App.wsServer.sendToAll(msg + "}");
            break;
		case MSP_MOTOR:
            msg = "{\"id\":" + MSP_MOTOR;
			for (i = 0; i < 8; i++) {
                mot[i] = read16();
                msg += ",\"motor" + i + "\":" + mot[i];
//                App.deviceStateRef.child("motors").child(Integer.toString(i)).setValue(mot[i]);
            }
			if (multiType == SINGLECOPTER)
				servo[7] = mot[0];
			if (multiType == DUALCOPTER) {
				servo[7] = mot[0];
				servo[6] = mot[1];
			}
//            App.wsServer.sendToAll(msg + "}");

			break;
		case MSP_RC:
			rcRoll = read16();
			rcPitch = read16();
			rcYaw = read16();
			rcThrottle = read16();
			rcAUX1 = read16();
			rcAUX2 = read16();
			rcAUX3 = read16();
			rcAUX4 = read16();
//            App.wsServer.sendToAll("{\"id\":" + MSP_RC
//                    + ",\"rcRoll\":" + rcRoll
//                    + ",\"rcPitch\":" + rcPitch
//                    + ",\"rcYaw\":" + rcYaw
//                    + ",\"rcThrottle\":" + rcThrottle
//                    + ",\"rcAUX1\":" + rcAUX1
//                    + ",\"rcAUX2\":" + rcAUX2
//                    + ",\"rcAUX3\":" + rcAUX3
//                    + ",\"rcAUX4\":" + rcAUX4
 //                   + "}");

/*
            App.deviceStateRef.child("rc").child("roll").setValue(rcRoll);
            App.deviceStateRef.child("rc").child("pitch").setValue(rcPitch);
            App.deviceStateRef.child("rc").child("throttle").setValue(rcThrottle);
            App.deviceStateRef.child("rc").child("yaw").setValue(rcYaw);
*/

            break;
		case MSP_RAW_GPS:
			GPS_fix = read8();
			GPS_numSat = read8();
			GPS_latitude = read32();
			GPS_longitude = read32();
			GPS_altitude = read16();
			GPS_speed = read16();
			GPS_ground_course = read16();
//            App.wsServer.sendToAll("{\"id\":" + MSP_RAW_GPS
//                    + ",\"GPS_fix\":" + GPS_fix
//                    + ",\"GPS_numSat\":" + GPS_numSat
//                    + ",\"GPS_latitude\":" + GPS_latitude
//                    + ",\"GPS_longitude\":" + GPS_longitude
 //                   + ",\"GPS_altitude\":" + GPS_altitude
 //                   + ",\"GPS_speed\":" + GPS_speed
 //                   + ",\"GPS_ground_course\":" + GPS_ground_course
 //                   + "}");

/*
            App.deviceStateRef.child("gps").child("number_satellites").setValue(GPS_numSat);
			App.deviceStateRef.child("gps").child("fix").setValue(GPS_fix);
			App.deviceStateRef.child("gps").child("longitude").setValue(GPS_longitude);
            App.deviceStateRef.child("gps").child("latitude").setValue(GPS_latitude);
            App.deviceStateRef.child("gps").child("altitude").setValue(GPS_altitude);
            App.deviceStateRef.child("gps").child("speed").setValue(GPS_speed);
            App.deviceStateRef.child("gps").child("heading").setValue(GPS_ground_course);
*/

			break;
		case MSP_COMP_GPS:
			GPS_distanceToHome = read16();
			GPS_directionToHome = read16();
			GPS_update = read8();
//            App.wsServer.sendToAll("{\"id\":" + MSP_COMP_GPS
//                    + ",\"GPS_distanceToHome\":" + GPS_distanceToHome
//                    + ",\"GPS_directionToHome\":" + GPS_directionToHome
//                    + ",\"GPS_update\":" + GPS_update
//                    + "}");

/*
            App.deviceStateRef.child("gps").child("distance_to_home").setValue(GPS_distanceToHome);
            App.deviceStateRef.child("gps").child("direction_to_home").setValue(GPS_directionToHome);
            App.deviceStateRef.child("gps").child("update").setValue(GPS_update);
*/

			break;
		case MSP_ATTITUDE:
			angx = read16() / 10;
			angy = read16() / 10;
			head = read16();
//            App.wsServer.sendToAll("{\"id\":" + MSP_ATTITUDE
//                    + ",\"angx\":" + angx
//                    + ",\"angy\":" + angy
//                    + ",\"head\":" + head
//                    + "}");

/*
            App.deviceStateRef.child("attitude").child("x").setValue(angx);
            App.deviceStateRef.child("attitude").child("y").setValue(angy);
            App.deviceStateRef.child("attitude").child("heading").setValue(head);
*/

            break;
		case MSP_ALTITUDE:
			alt = ((float) read32() / 100);
			vario = read16();
//            App.wsServer.sendToAll("{\"id\":" + MSP_ALTITUDE
//                    + ",\"alt\":" + alt
//                    + ",\"vario\":" + vario
//                    + "}");

/*
            App.deviceStateRef.child("barometer").child("altitude").setValue(alt);
            App.deviceStateRef.child("barometer").child("vario").setValue(vario);
*/

			break;
		case MSP_ANALOG:
			bytevbat = read8();
			pMeterSum = read16();
			rssi = read16();
			amperage = read16();
//            App.wsServer.sendToAll("{\"id\":" + MSP_ANALOG
//                    + ",\"bytevbat\":" + bytevbat
//                    + ",\"pMeterSum\":" + pMeterSum
//                    + ",\"rssi\":" + rssi
//                    + ",\"amperage\":" + amperage
//                    + "}");

/*
            App.deviceStateRef.child("analog").child("battery_voltage").setValue(bytevbat);
            App.deviceStateRef.child("analog").child("pMeterSum").setValue(pMeterSum);
            App.deviceStateRef.child("analog").child("rssi").setValue(rssi);
            App.deviceStateRef.child("analog").child("amperage").setValue(amperage);
*/

			break;
		case MSP_RC_TUNING:
			byteRC_RATE = read8();
			byteRC_EXPO = read8();
			byteRollPitchRate = read8();
			byteYawRate = read8();
			byteDynThrPID = read8();
			byteThrottle_MID = read8();
			byteThrottle_EXPO = read8();
//            App.wsServer.sendToAll("{\"id\":" + MSP_RC_TUNING
//                    + ",\"RC_RATE\":" + byteRC_RATE
//                    + ",\"RC_EXPO\":" + byteRC_EXPO
//                    + ",\"RollPitchRate\":" + byteRollPitchRate
//                    + ",\"YawRate\":" + byteYawRate
//                    + ",\"DynThrPID\":" + byteDynThrPID
//                    + ",\"Throttle_MID\":" + byteThrottle_MID
//                    + ",\"Throttle_EXPO\":" + byteThrottle_EXPO
//                    + "}");

/*
            App.deviceStateRef.child("rc_tuning").child("rssi").setValue(byteRC_RATE);
            App.deviceStateRef.child("rc_tuning").child("exponential").setValue(byteRC_EXPO);
            App.deviceStateRef.child("rc_tuning").child("roll_pitch_rate").setValue(byteRollPitchRate);
            App.deviceStateRef.child("rc_tuning").child("yaw_rate").setValue(byteYawRate);
            App.deviceStateRef.child("rc_tuning").child("dynamic_throttle_pid").setValue(byteDynThrPID);
            App.deviceStateRef.child("rc_tuning").child("throttle_mid").setValue(byteThrottle_MID);
            App.deviceStateRef.child("rc_tuning").child("throttle_exponential").setValue(byteThrottle_EXPO);
*/

			break;
		case MSP_ACC_CALIBRATION:
			break;
		case MSP_MAG_CALIBRATION:
			break;
		case MSP_PID:
            msg = "{\"id\":" + MSP_PID;
			for (i = 0; i < PIDITEMS; i++) {
				byteP[i] = read8();
				byteI[i] = read8();
				byteD[i] = read8();
                msg += ",\"P" + i + "\":" + byteP[i];
                msg += ",\"I" + i + "\":" + byteI[i];
                msg += ",\"D" + i + "\":" + byteD[i];
/*
                App.deviceStateRef.child("pid").child("p" + i).setValue(byteP[i]);
                App.deviceStateRef.child("pid").child("i" + i).setValue(byteI[i]);
                App.deviceStateRef.child("pid").child("d" + i).setValue(byteD[i]);
*/
			}
//            App.wsServer.sendToAll(msg + "}");
			break;
		case MSP_BOX:
            msg = "{\"id\":" + MSP_BOX;
			for (i = 0; i < CHECKBOXITEMS; i++) {
				activation[i] = read16();
				for (int aa = 0; aa < 12; aa++) {
					if ((activation[i] & (1 << aa)) > 0)
						Checkbox[i][aa] = true;
					else
						Checkbox[i][aa] = false;
                    msg += ",\"CB" + i + "." + aa + "\":" + (Checkbox[i][aa] ? "true" : "false");
//                    App.deviceStateRef.child("checkboxes").child(Integer.toString(i)).child(Integer.toString(aa)).setValue(Checkbox[i][aa]);
                }
			}
//            App.wsServer.sendToAll(msg + "}");

			break;
		case MSP_BOXNAMES:
//            msg = "{\"id\":" + MSP_BOXNAMES;
			BoxNames = new String(inBuf, 0, dataSize).split(";");
			Log.d("aaa", new String(inBuf, 0, dataSize));
			for (String s : BoxNames) {
//              msg += ",\"Boxname" + i + "\":" + s;
				Log.d("aaa", s);
			}
			init();
			break;
		case MSP_PIDNAMES:
			PIDNames = new String(inBuf, 0, dataSize).split(";");
//            App.wsServer.sendToAll("{\"id\":" + MSP_PIDNAMES
//                    + ",\"angx\":" + angx
//                    + ",\"angy\":" + angy
//                    + ",\"head\":" + head
//                    + "}");
			break;

		case MSP_SERVO_CONF:
			// min:2 / max:2 / middle:2 / rate:1
            msg = "{\"id\":" + MSP_BOX;
			for (i = 0; i < 8; i++) {
				ServoConf[i].Min = read16();
				ServoConf[i].Max = read16();
				ServoConf[i].MidPoint = read16();
				ServoConf[i].Rate = read8();
                msg += ",\"ServoMin" + i + "\":" + ServoConf[i].Min;
                msg += ",\"ServoMax" + i + "\":" + ServoConf[i].Max;
                msg += ",\"ServoMid" + i + "\":" + ServoConf[i].MidPoint;
                msg += ",\"ServoRate" + i + "\":" + ServoConf[i].Rate;
/*
                App.deviceStateRef.child("servo_configuration").child(Integer.toString(i)).child("minimum").setValue(ServoConf[i].Min);
                App.deviceStateRef.child("servo_configuration").child(Integer.toString(i)).child("maximum").setValue(ServoConf[i].Max);
                App.deviceStateRef.child("servo_configuration").child(Integer.toString(i)).child("midpoint").setValue(ServoConf[i].MidPoint);
                App.deviceStateRef.child("servo_configuration").child(Integer.toString(i)).child("rate").setValue(ServoConf[i].Rate);
*/
			}
//            App.wsServer.sendToAll(msg + "}");
			break;
		case MSP_MISC:
			intPowerTrigger = read16(); // a

			minthrottle = read16();// b
			maxthrottle = read16();// c
			mincommand = read16();// d
			failsafe_throttle = read16();// e
			ArmCount = read16();// f
			LifeTime = read32();// g
			mag_decliniation = read16() / 10f;// h

			vbatscale = read8();// i
			vbatlevel_warn1 = (float) (read8() / 10.0f);// j
			vbatlevel_warn2 = (float) (read8() / 10.0f);// k
			vbatlevel_crit = (float) (read8() / 10.0f);// l
			if (ArmCount < 1)
				Log_Permanent_Hidden = true;
//            App.wsServer.sendToAll("{\"id\":" + MSP_MISC
//                    + ",\"intPowerTrigger\":" + intPowerTrigger
//                    + ",\"minthrottle\":" + minthrottle
//                    + ",\"maxthrottle\":" + maxthrottle
//                    + ",\"mincommand\":" + mincommand
//                    + ",\"failsafe_throttle\":" + failsafe_throttle
//                    + ",\"ArmCount\":" + ArmCount
//                    + ",\"LifeTime\":" + LifeTime
//                    + ",\"mag_decliniation\":" + mag_decliniation
//                    + ",\"vbatscale\":" + vbatscale
//                    + ",\"vbatlevel_warn1\":" + vbatlevel_warn1
//                    + ",\"vbatlevel_warn2\":" + vbatlevel_warn2
//                    + ",\"vbatlevel_crit\":" + vbatlevel_crit
//                    + "}");

/*
            App.deviceStateRef.child("miscellaneous").child("power_trigger").setValue(intPowerTrigger);
            App.deviceStateRef.child("miscellaneous").child("minimum_throttle").setValue(minthrottle);
            App.deviceStateRef.child("miscellaneous").child("maximum_throttle").setValue(maxthrottle);
            App.deviceStateRef.child("miscellaneous").child("minimum_command").setValue(mincommand);
            App.deviceStateRef.child("miscellaneous").child("failsafe_throttle").setValue(failsafe_throttle);
            App.deviceStateRef.child("miscellaneous").child("arm_count").setValue(ArmCount);
            App.deviceStateRef.child("miscellaneous").child("vbatscale").setValue(vbatscale);
            App.deviceStateRef.child("miscellaneous").child("battery_warning_level_1").setValue(vbatlevel_warn1);
            App.deviceStateRef.child("miscellaneous").child("battery_warning_level_2").setValue(vbatlevel_warn2);
            App.deviceStateRef.child("miscellaneous").child("battery_warning_critical").setValue(vbatlevel_crit);
*/

			break;

		case MSP_MOTOR_PINS:
            msg = "{\"id\":" + MSP_MOTOR_PINS;
			for (i = 0; i < 8; i++) {
				byteMP[i] = read8();
                msg += ",\"byteMP" + i + "\":" + byteMP[i];
//                App.deviceStateRef.child("motor_pins").child(Integer.toString(i)).setValue(byteMP[i]);
			}
  //          App.wsServer.sendToAll(msg + "}");
			break;
		case MSP_DEBUG:
			debug1 = read16();
			debug2 = read16();
			debug3 = read16();
			debug4 = read16();
//            App.wsServer.sendToAll("{\"id\":" + MSP_DEBUG
//                    + ",\"debug1\":" + debug1
//                    + ",\"debug2\":" + debug2
//                    + ",\"debug3\":" + debug3
//                    + ",\"debug4\":" + debug4
//                    + "}");
/*
            App.deviceStateRef.child("debug").child("1").setValue(debug1);
            App.deviceStateRef.child("debug").child("2").setValue(debug2);
            App.deviceStateRef.child("debug").child("3").setValue(debug3);
            App.deviceStateRef.child("debug").child("4").setValue(debug4);
*/

			break;
		case MSP_DEBUGMSG:
			while (dataSize-- > 0) {
				char c = (char) read8();
				if (c != 0) {
					DebugMSG += c;
				}
			}
//            App.wsServer.sendToAll("{\"id\":" + MSP_DEBUGMSG
//                    + ",\"DebugMSG \":" + debug1
//                    + "}");
			break;
		case MSP_WP:
//			WaypointNav WP = new WaypointNav();
//			WP.Number = read8();
//			WP.Action = read8();
//			WP.Lat = read32();
//			WP.Lon = read32();
//			WP.Altitude = read32() / 100;
//			WP.Parameter1 = read16();
//			WP.Parameter2 = read16();
//			WP.Parameter3 = read16();
//			WP.Flag = read8();

//			WaypointsList.add(WP);

//			Log.d("nav", "MSP_WP (get) " + String.valueOf(WP.Number) + "  " + String.valueOf(WP.Lat) + "x" + String.valueOf(WP.Lon) + " A" + String.valueOf(WP.Action) + " F" + String.valueOf(WP.Flag));
//            App.wsServer.sendToAll("{\"id\":" + MSP_WP
//                    + ",\"Number\":" + WP.Number
//                    + ",\"Action\":" + WP.Action
//                    + ",\"Lat\":" + WP.Lat
 //                   + ",\"Lon\":" + WP.Lon
//                    + ",\"Altitude\":" + WP.Altitude
//                    + ",\"Parameter1\":" + WP.Parameter1
//                    + ",\"Parameter2\":" + WP.Parameter2
//                    + ",\"Parameter3\":" + WP.Parameter3
//                    + "}");

//            App.deviceStateRef.child("waypoints").child(Integer.toString(WP.Number)).child("action").setValue(WP.Action);
//            App.deviceStateRef.child("waypoints").child(Integer.toString(WP.Number)).child("latitude").setValue(WP.Lat);
//            App.deviceStateRef.child("waypoints").child(Integer.toString(WP.Number)).child("longitude").setValue(WP.Lon);
//            App.deviceStateRef.child("waypoints").child(Integer.toString(WP.Number)).child("altitude").setValue(WP.Altitude);
//            App.deviceStateRef.child("waypoints").child(Integer.toString(WP.Number)).child("parameter_1").setValue(WP.Parameter1);
//            App.deviceStateRef.child("waypoints").child(Integer.toString(WP.Number)).child("parameter_2").setValue(WP.Parameter2);
//            App.deviceStateRef.child("waypoints").child(Integer.toString(WP.Number)).child("parameter_3").setValue(WP.Parameter3);
//            App.deviceStateRef.child("waypoints").child(Integer.toString(WP.Number)).child("parameter_4").setValue(WP.Flag);

			break;

		case MSP_NAV_CONFIG:
			NAVmaxWpNumber = read8();
//            App.wsServer.sendToAll("{\"id\":" + MSP_NAV_CONFIG
//                    + ",\"NAVmaxWpNumber\":" + NAVmaxWpNumber
//                    + "}");

//            App.deviceStateRef.child("waypoints").child("total").setValue(NAVmaxWpNumber);

			break;

		case MSP_NAV_STATUS:
			NAVGPSMode = read8();
			NAVstate = read8();// serialize8(NAV_state);
			NAVcurrentAction = read8();// serialize8(mission_step.action);
			NAVcurrentWPNumber = read8();// serialize8(mission_step.number);
			NAVerror = read8();// serialize8(NAV_error);
			//
			NAVoriginalAltitude = read32();// serialize32(original_altitude);
			NAVtargetAltitude = read32();// serialize32(target_altitude);
			NAValtToHold = read32();// serialize32(alt_to_hold);
			NAValtChangeFlag = read8();// serialize8(alt_change_flag);

//            App.wsServer.sendToAll("{\"id\":" + MSP_NAV_STATUS
//                + ",\"NAVGPSMode\":" + NAVGPSMode
//                + ",\"NAVstate\":" + NAVstate
 //               + ",\"NAVcurrentAction\":" + NAVcurrentAction
//                + ",\"NAVcurrentWPNumber\":" + NAVcurrentWPNumber
//                + ",\"NAVerror\":" + NAVerror
//                + ",\"NAVoriginalAltitude\":" + NAVoriginalAltitude
//                + ",\"NAVtargetAltitude\":" + NAVtargetAltitude
//                + ",\"NAValtToHold\":" + NAValtToHold
//                + ",\"NAValtChangeFlag\":" + NAValtChangeFlag
//                + "}");

  /*          App.deviceStateRef.child("navigation_status").child("mode").setValue(NAVGPSMode);
            App.deviceStateRef.child("navigation_status").child("state").setValue(NAVstate);
            App.deviceStateRef.child("navigation_status").child("current_action").setValue(NAVcurrentAction);
            App.deviceStateRef.child("navigation_status").child("current_waypoint").setValue(NAVcurrentWPNumber);
            App.deviceStateRef.child("navigation_status").child("error").setValue(NAVerror);
            App.deviceStateRef.child("navigation_status").child("original_altitude").setValue(NAVoriginalAltitude);
            App.deviceStateRef.child("navigation_status").child("target_altitude").setValue(NAVtargetAltitude);
            App.deviceStateRef.child("navigation_status").child("altitude_to_hold").setValue(NAValtToHold);
            App.deviceStateRef.child("navigation_status").child("altitude_change_flag").setValue(NAValtChangeFlag);
*/
			break;


		default:
			Log.e("aaa", "Error command - unknown replay " + String.valueOf(icmd));

		}
	}

	int c_state = IDLE;
	byte c;
	boolean err_rcvd = false;
	int offset = 0, dataSize = 0;
	byte checksum = 0;
	byte cmd;
	byte[] inBuf = new byte[256];
	int i = 0;
	int p = 0;

	int read32() {
		return (inBuf[p++] & 0xff) + ((inBuf[p++] & 0xff) << 8) + ((inBuf[p++] & 0xff) << 16) + ((inBuf[p++] & 0xff) << 24);
	}

	int read16() {
		return (inBuf[p++] & 0xff) + ((inBuf[p++]) << 8);
	}

	int read8() {
		return inBuf[p++] & 0xff;
	}

	private void ReadFrame() {
		DataFlow--;

		while (communication.dataAvailable()) {

			try {
				c = (communication.Read());

			} catch (Exception e) {
				c_state = IDLE;
				Log.e("MultiwiiProtocol", "Read  = null");
				break;
			}

			if (c_state == IDLE) {
				c_state = (c == '$') ? HEADER_START : IDLE;
			} else if (c_state == HEADER_START) {
				c_state = (c == 'M') ? HEADER_M : IDLE;
			} else if (c_state == HEADER_M) {
				if (c == '>') {
					c_state = HEADER_ARROW;
				} else if (c == '!') {
					c_state = HEADER_ERR;
				} else {
					c_state = IDLE;
				}
			} else if (c_state == HEADER_ARROW || c_state == HEADER_ERR) {
				/* is this an error message? */
				err_rcvd = (c_state == HEADER_ERR); /*
													 * now we are expecting the
													 * payload size
													 */
				dataSize = (c & 0xFF);
				/* reset index variables */
				p = 0;
				offset = 0;
				checksum = 0;
				checksum ^= (c & 0xFF);
				/* the command is to follow */
				c_state = HEADER_SIZE;
			} else if (c_state == HEADER_SIZE) {
				cmd = (byte) (c & 0xFF);
				checksum ^= (c & 0xFF);
				c_state = HEADER_CMD;
			} else if (c_state == HEADER_CMD && offset < dataSize) {
				checksum ^= (c & 0xFF);
				inBuf[offset++] = (byte) (c & 0xFF);
			} else if (c_state == HEADER_CMD && offset >= dataSize) {
				/* compare calculated and transferred checksum */
				if ((checksum & 0xFF) == (c & 0xFF)) {
					if (err_rcvd) {
						Log.e("Multiwii protocol", "Copter did not understand request type " + c);
					} else {
						/* we got a valid response packet, evaluate it */
						evaluateCommand(cmd, (int) dataSize);
						DataFlow = DATA_FLOW_TIME_OUT;
					}
				} else {
					Log.e("Multiwii protocol", "invalid checksum for command " + ((int) (cmd & 0xFF)) + ": " + (checksum & 0xFF) + " expected, got " + (int) (c & 0xFF));
					Log.e("Multiwii protocol", "<" + (cmd & 0xFF) + " " + (dataSize & 0xFF) + "> {");
					// for (i = 0; i < dataSize; i++) {
					// if (i != 0) {
					// Log.e("Multiwii protocol"," ");
					// }
					// Log.e("Multiwii protocol",(inBuf[i] & 0xFF));
					// }
					Log.e("Multiwii protocol", "} [" + c + "]");
					Log.e("Multiwii protocol", new String(inBuf, 0, dataSize));
				}
				c_state = IDLE;
			}

		}
	}

	@Override
	public void SendRequestMSP_PID_MSP_RC_TUNING() {
		int[] requests = { MSP_PID, MSP_RC_TUNING };
		sendRequestMSP(requestMSP(requests));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ezio.multiwii.mw.MultirotorData#SendRequestMSP_SET_MISC(int,
	 * int, int, int, int, float, byte, float, float, float)
	 */
	@Override
	public void SendRequestMSP_SET_MISC(int PowerTrigger, int minthrottle, int maxthrottle, int mincommand, int failsafe_throttle, float mag_decliniation, int vbatscale, float vbatlevel_warn1, float vbatlevel_warn2, float vbatlevel_crit) {

		// intPowerTrigger1 UNIT 16
		// conf.minthrottle UNIT 16
		// MAXTHROTTLE UNIT 16
		// MINCOMMAND UNIT 16
		// conf.failsafe_throttle UNIT 16
		// plog.arm UNIT 16
		// plog.lifetime UNIT 32
		// conf.mag_declination UNIT 16
		// conf.vbatscale UNIT 8
		// conf.vbatlevel_warn1 UNIT 8
		// conf.vbatlevel_warn2 UNIT 8
		// conf.vbatlevel_crit UNIT 8

		payload = new ArrayList<Character>();

		intPowerTrigger = (Math.round(PowerTrigger));
		payload.add((char) (intPowerTrigger % 256));
		payload.add((char) (intPowerTrigger / 256));

		payload.add((char) (minthrottle % 256));
		payload.add((char) (minthrottle / 256));

		payload.add((char) (maxthrottle % 256));
		payload.add((char) (maxthrottle / 256));

		payload.add((char) (mincommand % 256));
		payload.add((char) (mincommand / 256));

		payload.add((char) (failsafe_throttle % 256));
		payload.add((char) (failsafe_throttle / 256));

		payload.add((char) (0));// plog.arm (16bit) not used
		payload.add((char) (0));// plog.arm (16bit) not used
		payload.add((char) (0)); // plog.lifetime + (plog.armed_time / 1000000)
									// (32bit) not used
		payload.add((char) (0)); // plog.lifetime + (plog.armed_time / 1000000)
									// (32bit) not used
		payload.add((char) (0)); // plog.lifetime + (plog.armed_time / 1000000)
									// (32bit) not used
		payload.add((char) (0)); // plog.lifetime + (plog.armed_time / 1000000)
									// (32bit) not used

		int nn = Math.round(mag_decliniation * 10); // mag_decliniation
		payload.add((char) (nn - ((nn >> 8) << 8))); // mag_decliniation
		payload.add((char) (nn >> 8)); // mag_decliniation

		nn = Math.round(vbatscale);
		payload.add((char) (nn)); // VBatscale

		int q = (int) (vbatlevel_warn1 * 10);
		payload.add((char) (q));

		q = (int) (vbatlevel_warn2 * 10);
		payload.add((char) (q));

		q = (int) (vbatlevel_crit * 10);
		payload.add((char) (q));

		sendRequestMSP(requestMSP(MSP_SET_MISC, payload.toArray(new Character[payload.size()])));

		// MSP_EEPROM_WRITE
		sendRequestMSP(requestMSP(MSP_EEPROM_WRITE));

		// ///////////////////////////////////////////////////////////

	}

	@Override
	public void ProcessSerialData(boolean appLogging) {
		if (communication.Connected) {
			ReadFrame();
			if (appLogging)
				Logging();
		}
	}

	@Override
	public void SendRequestMSP_ACC_CALIBRATION() {
		sendRequestMSP(requestMSP(MSP_ACC_CALIBRATION));
	}

	@Override
	public void SendRequestMSP_MAG_CALIBRATION() {
		sendRequestMSP(requestMSP(MSP_MAG_CALIBRATION));
	}

	ArrayList<Character> payload = new ArrayList<Character>();

	@Override
	public void SendRequestMSP_SET_PID(float confRC_RATE, float confRC_EXPO, float rollPitchRate, float yawRate, float dynamic_THR_PID, float throttle_MID, float throttle_EXPO, float[] confP, float[] confI, float[] confD) {

		// MSP_SET_RC_TUNING
		payload = new ArrayList<Character>();
		payload.add((char) (Math.round(confRC_RATE * 100)));
		payload.add((char) (Math.round(confRC_EXPO * 100)));
		payload.add((char) (Math.round(rollPitchRate * 100)));
		payload.add((char) (Math.round(yawRate * 100)));
		payload.add((char) (Math.round(dynamic_THR_PID * 100)));
		payload.add((char) (Math.round(throttle_MID * 100)));
		payload.add((char) (Math.round(throttle_EXPO * 100)));
		sendRequestMSP(requestMSP(MSP_SET_RC_TUNING, payload.toArray(new Character[payload.size()])));

		// MSP_SET_PID
		payload = new ArrayList<Character>();
		for (int i = 0; i < PIDITEMS; i++) {
			byteP[i] = (int) (Math.round(confP[i] * 10));
			byteI[i] = (int) (Math.round(confI[i] * 1000));
			byteD[i] = (int) (Math.round(confD[i]));
		}

		// POS-4 POSR-5 NAVR-6 use different dividers
		byteP[4] = (int) (Math.round(confP[4] * 100.0));
		byteI[4] = (int) (Math.round(confI[4] * 100.0));

		byteP[5] = (int) (Math.round(confP[5] * 10.0));
		byteI[5] = (int) (Math.round(confI[5] * 100.0));
		byteD[5] = (int) ((Math.round(confD[5] * 10000.0)) / 10);

		byteP[6] = (int) (Math.round(confP[6] * 10.0));
		byteI[6] = (int) (Math.round(confI[6] * 100.0));
		byteD[6] = (int) ((Math.round(confD[6] * 10000.0)) / 10);

		for (i = 0; i < PIDITEMS; i++) {
			payload.add((char) (byteP[i]));
			payload.add((char) (byteI[i]));
			payload.add((char) (byteD[i]));
		}
		sendRequestMSP(requestMSP(MSP_SET_PID, payload.toArray(new Character[payload.size()])));

	}

	@Override
	public void SendRequestMSP_RESET_CONF() {
		sendRequestMSP(requestMSP(MSP_RESET_CONF));

	}

	@Override
	public void SendRequestMSP_NAV_CONFIG() {
		sendRequestMSP(requestMSP(MSP_NAV_CONFIG));

	}

	@Override
	public void SendRequestMSP_MISC() {
		sendRequestMSP(requestMSP(MSP_MISC));

	}

	@Override
	public void SendRequestMSP_SET_RAW_GPS(byte GPS_FIX, byte numSat, int coordLAT, int coordLON, int altitude, int speed) {
		ArrayList<Character> payload = new ArrayList<Character>();
		payload.add((char) GPS_FIX);
		payload.add((char) numSat);
		payload.add((char) (coordLAT & 0xFF));
		payload.add((char) ((coordLAT >> 8) & 0xFF));
		payload.add((char) ((coordLAT >> 16) & 0xFF));
		payload.add((char) ((coordLAT >> 24) & 0xFF));

		payload.add((char) (coordLON & 0xFF));
		payload.add((char) ((coordLON >> 8) & 0xFF));
		payload.add((char) ((coordLON >> 16) & 0xFF));
		payload.add((char) ((coordLON >> 24) & 0xFF));

		payload.add((char) (altitude & 0xFF));
		payload.add((char) ((altitude >> 8) & 0xFF));

		payload.add((char) (speed & 0xFF));
		payload.add((char) ((speed >> 8) & 0xFF));

		sendRequestMSP(requestMSP(MSP_SET_RAW_GPS, payload.toArray(new Character[payload.size()])));
	}

	/**
	 * 0rcRoll 1rcPitch 2rcYaw 3rcThrottle 4rcAUX1 5rcAUX2 6rcAUX3 7rcAUX4
	 */
	@Override
	public void SendRequestMSP_SET_RAW_RC(int[] channels8) {
		ArrayList<Character> payload = new ArrayList<Character>();
		for (int i = 0; i < 8; i++) {
			payload.add((char) (channels8[i] & 0xFF));
			payload.add((char) ((channels8[i] >> 8) & 0xFF));
		}

		sendRequestMSP(requestMSP(MSP_SET_RAW_RC, payload.toArray(new Character[payload.size()])));

		sendRequestMSP(requestMSP(new int[] { MSP_RC }));
		Log.d("aaa", "RC:" + String.valueOf(rcRoll) + " " + String.valueOf(rcPitch) + " " + String.valueOf(rcYaw) + " " + String.valueOf(rcThrottle) + " " + String.valueOf(rcAUX1) + " " + String.valueOf(rcAUX2) + " " + String.valueOf(rcAUX3) + " " + String.valueOf(rcAUX4));
	}

	@Override
	public void SendRequestMSP_BOX() {
		sendRequestMSP(requestMSP(MSP_BOX));
	}

	@Override
	public void SendRequestMSP_SET_BOX() {
		// MSP_SET_BOX
		payload = new ArrayList<Character>();
		for (i = 0; i < CHECKBOXITEMS; i++) {
			activation[i] = 0;
			for (int aa = 0; aa < 12; aa++) {
				activation[i] += (int) (((int) (Checkbox[i][aa] ? 1 : 0)) * (1 << aa));
			}
			payload.add((char) (activation[i] % 256));
			payload.add((char) (activation[i] / 256));
		}
		sendRequestMSP(requestMSP(MSP_SET_BOX, payload.toArray(new Character[payload.size()])));

	}

	@Override
	public void SendRequestMSP_EEPROM_WRITE() {
		// MSP_EEPROM_WRITE
		sendRequestMSP(requestMSP(MSP_EEPROM_WRITE));
	}


	@Override
	public void ZeroConnection() {
		timer3 = -1;

	}

	// /////////////////////////////END NEW requests\\\\\\\\\\\\\\\\\\\\\\\\\\

	@Override
	public void SendRequestMSP_WP(int Number) {
		ArrayList<Character> payload = new ArrayList<Character>();
		payload.add((char) Number);
		sendRequestMSP(requestMSP(MSP_WP, payload.toArray(new Character[payload.size()])));
		Log.d("aaa", "MSP_WP (SendRequestGetWayPoint) " + String.valueOf(Number));
	}

	// ////////Extra functions/////////////////
	@Override
	public void SendRequestMSP_SET_SERIAL_BAUDRATE(int baudRate) {
		ArrayList<Character> payload = new ArrayList<Character>();

		payload.add((char) (baudRate & 0xFF));
		payload.add((char) ((baudRate >> 8) & 0xFF));
		payload.add((char) ((baudRate >> 16) & 0xFF));
		payload.add((char) ((baudRate >> 24) & 0xFF));

		sendRequestMSP(requestMSP(MSP_SET_SERIAL_BAUDRATE, payload.toArray(new Character[payload.size()])));

		Log.d("aaa", "MSP_SET_SERIAL_BAUDRATE " + String.valueOf(baudRate));
	}

	@Override
	public void SendRequestMSP_ENABLE_FRSKY() {
		sendRequestMSP(requestMSP(MSP_ENABLE_FRSKY));
		Log.d("aaa", "MSP_ENABLE_FRSKY");

	}

	// ///////////End of Extra Functions////////////

	@Override
	public void SendRequestMSP_SELECT_SETTING(int setting) {

		payload = new ArrayList<Character>();
		payload.add((char) setting);
		sendRequestMSP(requestMSP(MSP_SELECT_SETTING, payload.toArray(new Character[payload.size()])));
		Log.d("aaa", "MSP_SELECT_SETTING");
	}

	@Override
	public void SendRequestMSP_BIND() {
		sendRequestMSP(requestMSP(MSP_BIND));
		Log.d("aaa", "MSP_BIND");
	}

	@Override
	public void SendRequestMSP_SET_HEAD(int heading) {
		payload = new ArrayList<Character>();
		payload.add((char) (heading & 0xFF));
		payload.add((char) ((heading >> 8) & 0xFF));
		sendRequestMSP(requestMSP(MSP_SET_HEAD, payload.toArray(new Character[payload.size()])));
		Log.d("aaa", "MSP_SET_HEAD " + String.valueOf(heading));

	}

	@Override
	public void SendRequestMSP_SET_MOTOR(byte motorTogglesByte) {
		int motEnable[] = new int[8];
		payload = new ArrayList<Character>();
		motorTogglesByte = (byte) (motEnable[0] + motEnable[1] * 2 + motEnable[2] * 4 + motEnable[3] * 8 + motEnable[4] * 16 + motEnable[5] * 32 + motEnable[6] * 64 + motEnable[7] * 128);
		payload.add((char) (motorTogglesByte));
		// toggleMotor=false;
		sendRequestMSP(requestMSP(MSP_SET_MOTOR, payload.toArray(new Character[payload.size()])));
		Log.d("aaa", "MSP_SET_MOTOR " + String.valueOf(motorTogglesByte));
	}

	@Override
	public void SendRequest(int MainRequestMethod) {
		// if (MainRequestMethod == 1)
		// SendRequest1();
		if (MainRequestMethod == 2)
			SendRequest2();

	}

	@Override
	public void SendRequestMSP_SERVO_CONF() {
		sendRequestMSP(requestMSP(MSP_SERVO_CONF));
	}

	@Override
	public void SendRequestMSP_SET_SERVO_CONF() {
		ArrayList<Character> payload = new ArrayList<Character>();

		for (int i = 0; i < ServoConf.length; i++) {
			payload.add((char) (ServoConf[i].Min & 0xFF));
			payload.add((char) ((ServoConf[i].Min >> 8) & 0xFF));

			payload.add((char) (ServoConf[i].Max & 0xFF));
			payload.add((char) ((ServoConf[i].Max >> 8) & 0xFF));

			payload.add((char) (ServoConf[i].MidPoint & 0xFF));
			payload.add((char) ((ServoConf[i].MidPoint >> 8) & 0xFF));

			payload.add((char) ServoConf[i].Rate);
		}
		sendRequestMSP(requestMSP(MSP_SET_SERVO_CONF, payload.toArray(new Character[payload.size()])));
	}

	@Override
	public void SendRequestMSP_SET_WP(Waypoint waypoint) {
		// Not used with NAV

	}

    @Override
    public void SendRequestMSP_SET_WP_NAV(Waypoint w) {

    }

//    @Override
//	public void SendRequestMSP_SET_WP_NAV(WaypointNav w) {
//		w.Altitude *= 100;

//		ArrayList<Character> payload = new ArrayList<Character>();
//		payload.add((char) w.Number);
//		payload.add((char) w.Action);
//		payload.add((char) (w.Lat & 0xFF));
//		payload.add((char) ((w.Lat >> 8) & 0xFF));
//		payload.add((char) ((w.Lat >> 16) & 0xFF));
//		payload.add((char) ((w.Lat >> 24) & 0xFF));

//		payload.add((char) (w.Lon & 0xFF));
//		payload.add((char) ((w.Lon >> 8) & 0xFF));
//		payload.add((char) ((w.Lon >> 16) & 0xFF));
//		payload.add((char) ((w.Lon >> 24) & 0xFF));

//		payload.add((char) (w.Altitude & 0xFF));
//		payload.add((char) ((w.Altitude >> 8) & 0xFF));
//		payload.add((char) ((w.Altitude >> 16) & 0xFF));
//		payload.add((char) ((w.Altitude >> 24) & 0xFF));

//		payload.add((char) (w.Parameter1 & 0xFF));
//		payload.add((char) ((w.Parameter1 >> 8) & 0xFF));

//		payload.add((char) (w.Parameter2 & 0xFF));
//		payload.add((char) ((w.Parameter2 >> 8) & 0xFF));

//		payload.add((char) (w.Parameter3 & 0xFF));
//		payload.add((char) ((w.Parameter3 >> 8) & 0xFF));

//		payload.add((char) w.Flag);

//		sendRequestMSP(requestMSP(MSP_SET_WP, payload.toArray(new Character[payload.size()])));

//		Log.d("nav", "MSP_SET_WP " + String.valueOf(w.Number) + "  " + String.valueOf(w.Lat) + "x" + String.valueOf(w.Lon) + " " + String.valueOf(w.Altitude) + " " + String.valueOf(w.Flag));
//	}

	@Override
	public void SendRequestMSP(int MSPCommand) {
		sendRequestMSP(requestMSP(MSPCommand));
	}

}
