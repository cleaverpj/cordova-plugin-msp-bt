package enterprises.nucleus.plugins.multiwii_bluetooth.comms;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class Bluetooth2Serial {
    private static final String TAG = "bt2ser";
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    public byte[] readBuffer = new byte[100];
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    private String pairingName;
    // private String pairingName = "linvor";
    private String lastMsg = "";

    // private String pairingName= "LG-P506";

    public Bluetooth2Serial() {
        this.pairingName = "HB02";
    }

    public Bluetooth2Serial(String pairingName) {
        this.pairingName = pairingName;
    }

    public void open() throws IOException {
        Log.i(TAG, "starting");

        try {

            findBT();
            openBT();
        } catch (NullPointerException e) {
            // no bt
        }

    }

    public void close() throws IOException {
        try {
            closeBT();
        } catch (IOException ex) {
            Log.e(TAG, "ioexception:" + ex);
        }

    }

    void findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
 //           MainActivity.appendTvConsole("No bluetooth adapter available");
            Log.i(TAG, "No bluetooth adapter available");
 //           MainActivity.appendTvConsole("No bluetooth adapter available");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
 //           MainActivity.getActivity().startActivityForResult(enableBluetooth,
 //                   0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(pairingName)) {
                    mmDevice = device;
  //                  MainActivity.appendTvConsole("found BT device");
                    break;
                }
            }
        }
    }

    void openBT() {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Standard
        // SerialPortService
        // ID
        try {
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            // beginListenForData();

 //           MainActivity.appendTvConsole("Bluetooth Opened");
            Log.i(TAG, "Bluetooth Opened");
        } catch (NullPointerException e) {
            Log.e(TAG, "NullPointerException:" + e);
  //          MainActivity.appendTvConsole("Bluetooth Failed - are you paired correctly to "
  //                  + pairingName);
        } catch (IOException e) {
            Log.e(TAG, "IOException:" + e);
 //           MainActivity.appendTvConsole("Bluetooth Failed - are you paired correctly to "
 //                   + pairingName);
        }
    }

    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; // This is the ASCII code for a newline
        // character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {

                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0,
                                            encodedBytes, 0,
                                            encodedBytes.length);
                                    final String data = new String(
                                            encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            try {
          //                                      if (App.logDataToScreen)
          //                                          MainActivity.appendTvConsole(data);
     //                                           App.wsServer.sendToAll(data);

                                            } catch (Exception e) {
            //                                    MainActivity.appendTvConsole("couldn't send bluetooth data to websocket:" + e);
                                                Log.w("bluetooth","couldn't send bluetooth data to websocket:" + e);
                                            }
              //                              Helper.toastShort(data);
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void sendData(String msg) {
        msg += "\n";
        if (!lastMsg.equalsIgnoreCase(msg)) {

            try {
                Log.d(TAG, " Sending Data:" + msg);
                mmOutputStream.write(msg.getBytes());
            } catch (IOException e) {
                Log.e(TAG, "io exception:" + e);
            } catch (NullPointerException e) {
                Log.e(TAG, "null pointer exception:" + e);
                Log.e(TAG, "mmOutputStream, not connected??");
            }
            lastMsg = msg;
        }
    }

    void closeBT() throws IOException {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
 //       Helper.toastShort("Bluetooth Closed");
    }

    public int read(byte[] buf, int numberBytes) throws IOException {
        // my one, converted to bt
        int bytesAvailable = 0;
        try {
            bytesAvailable = mmInputStream.available();
            if (bytesAvailable > 0) {

                byte[] packetBytes = new byte[bytesAvailable];
                mmInputStream.read(packetBytes);
                for (int i = 0; i < bytesAvailable; i++) {
                    byte b = packetBytes[i];

                    readBuffer[readBufferPosition++] = b;
                }
            }
        } catch (IOException ex) {
            stopWorker = true;
        }

        return bytesAvailable;
    }

    public void write(byte[] writeData, int numberBytes) throws IOException {

        try {
            Log.d(TAG, " writing " + numberBytes + " from :" + writeData);
            mmOutputStream.write(writeData, 0, numberBytes);
        } catch (IOException e) {
            Log.e(TAG, "io exception:" + e);
        } catch (NullPointerException e) {
            Log.e(TAG, "null pointer exception:" + e);
            Log.e(TAG, "mmOutputStream, not connected??");
        }

    }

}
