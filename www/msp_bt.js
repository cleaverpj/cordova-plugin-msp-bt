/*global cordova, module*/

module.exports = {
    getDevices: function (args, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Msp_bt", "getDevices", [args]);
    },
    connect: function (args, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Msp_bt", "connect", [args]);
    },
    disconnect: function (args, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Msp_bt", "disconnect", [args]);
    },
    getData: function (args, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Msp_bt", "getData", [args]);
    },
    setWaypoints: function (args, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Msp_bt", "setWaypoints", [args]);
    },
    setRC: function (args, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Msp_bt", "setRC", [args]);
    },
    setHeading: function (args, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Msp_bt", "setRC", [args]);
    },
    sendMessage: function (args, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Msp_bt", "sendMessage", [args]);
    },
	codes: {
		MSP_IDENT: 100,
		MSP_STATUS: 101,
		MSP_RAW_IMU: 102,
		MSP_SERVO: 103,
		MSP_MOTOR: 104,
		MSP_RC: 105,
		MSP_RAW_GPS: 106,
		MSP_COMP_GPS: 107,
		MSP_ATTITUDE: 108,
		MSP_ALTITUDE: 109,
		MSP_ANALOG: 110,
		MSP_RC_TUNING: 111,
		MSP_PID: 112,
		MSP_BOX: 113,
		MSP_MISC: 114,
		MSP_MOTOR_PINS: 115,
		MSP_BOXNAMES: 116,
		MSP_PIDNAMES: 117,
		MSP_WP: 118,
		MSP_BOXIDS: 119,
		MSP_SERVO_CONF: 120,

		MSP_NAV_STATUS: 121, // out message Returns navigation status
		MSP_NAV_CONFIG: 122, // out message Returns navigation parameters

		MSP_SET_RAW_RC: 200,
		MSP_SET_RAW_GPS: 201,
		MSP_SET_PID: 202,
		MSP_SET_BOX: 203,
		MSP_SET_RC_TUNING: 204,
		MSP_ACC_CALIBRATION: 205,
		MSP_MAG_CALIBRATION: 206,
		MSP_SET_MISC: 207,
		MSP_RESET_CONF: 208,
		MSP_SET_WP: 209,
		MSP_SELECT_SETTING: 210,
		MSP_SET_HEAD: 211,
		MSP_SET_SERVO_CONF: 212,
		MSP_SET_MOTOR: 214,

		MSP_BIND: 240,

		MSP_EEPROM_WRITE: 250,

		MSP_DEBUGMSG: 253,
		MSP_DEBUG: 254,

		MSP_SET_SERIAL_BAUDRATE: 199,
		MSP_ENABLE_FRSKY: 198,

		IDLE: 0, HEADER_START: 1, HEADER_M: 2, HEADER_ARROW: 3, HEADER_SIZE: 4, HEADER_CMD: 5, HEADER_ERR: 6,
	}
};
