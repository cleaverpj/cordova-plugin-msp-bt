/*global cordova, module*/

module.exports = {
    greet: function (name, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Msp_bt", "greet", [name]);
    },
    connect: function (deviceId, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Msp_bt", "connect", [deviceId]);
    }
};
