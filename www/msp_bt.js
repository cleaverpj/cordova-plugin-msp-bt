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
    sendMessage: function (args, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Msp_bt", "sendMessage", [args]);
    },
	codes: {
		MSP_ATTITUDE: 101
	}
};
