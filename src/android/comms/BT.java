package enterprises.nucleus.plugins.multiwii_bluetooth.comms;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BT extends Communication {

	private static final boolean D = true;
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothSocket btSocket = null;
	private OutputStream outStream = null;
	private InputStream inStream = null;
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	public BT(Context context) {
		super(context);
		Enable();
	}

	@Override
	public void Enable() {
//        MainActivity.appendTvConsole("Starting Bluetooth");
		if (D)
			Log.d("BT", "+++ Enable BT +++");

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
 //           MainActivity.appendTvConsole(App._context.getString(R.string.Bluetoothisnotavailable));
			// finish();
			return;
		}

		if (!mBluetoothAdapter.isEnabled()) {
 //           MainActivity.appendTvConsole(App._context.getString(R.string.Bluetoothisnotavailable));
			mBluetoothAdapter.enable();
			return;
		}

		if (D)
			Log.d("BT", "+++ DONE IN ON CREATE, GOT LOCAL BT ADAPTER +++");

	}

	@Override
	public void Connect(String address, int speed) {
		if (mBluetoothAdapter.isEnabled()) {
			Log.d("BT", "Connecting adapter");
			try {
				GetRemoteDevice(address);
				Log.d("BT", "Connecting socket:" + btSocket);
				btSocket.connect();
				Connected = true;

				Log.d("BT", "BT connection established, data transfer link open.");
			} catch (IOException e) {
				try {
					Log.e("BT", "IO Exception " + e);
					btSocket.close();
					Connected = false;
				} catch (IOException e2) {
					Log.e("BT", "Unable to close socket during connection failure", e2);
				}
			}

			// Create a data stream so we can talk to server.
			if (D)
				Log.d("BT", "+ getOutputStream  getInputStream +");

			try {
				outStream = btSocket.getOutputStream();
				inStream = btSocket.getInputStream();
				Log.d("BT", "created streams successfully");

			} catch (IOException e) {
				Log.e("BT", "ON RESUME: Output stream creation failed.", e);
			}
		} else {
			Log.e("BT", "Adapter not enabled");
		}
		Log.d("BT", "Finished Connecting");
	}

	@Override
	public boolean dataAvailable() {
		boolean a = false;

		try {
			if (Connected)
				a = inStream.available() > 0;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return a;
	}

	@Override
	public byte Read() {
		Log.d("bt", "received!!");
		BytesRecieved += 1;
		byte a = 0;
		try {
			a = (byte) inStream.read();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(context, "Read error", Toast.LENGTH_LONG).show();
		}
		return (byte) (a);
	}

	@Override
	public void Write(byte[] arr) {
		Log.d("bt", "sending:" + String.valueOf(arr));
		super.Write(arr);
		try {
			if (Connected) {
				outStream.write(arr);
				Log.d("bt", "sent:" + String.valueOf(arr));
			}
		} catch (IOException e) {
			Log.e("BT", "SEND : Exception during write.", e);
			CloseSocket();

			Toast.makeText(context, "Write error", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void Close() {
		CloseSocket();
	}

	@Override
	public void Disable() {
		try {
			mBluetoothAdapter.disable();
		} catch (Exception e) {
			Toast.makeText(context, "Can't dissable BT", Toast.LENGTH_LONG).show();
		}
	}

	@SuppressLint("NewApi")
	private void GetRemoteDevice(String address) {
		if (D) {
			Log.d("BT", "attempting to connect to mac:" + address);
		}

		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

		if (D) {
			Log.d("BT", "got device:" + device);
		}

		try {

			if (D) {
				Log.d("BT", "making serial connection");
			}

			btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
			Log.d("BT", "made serial connection:" + btSocket);

		} catch (IOException e) {
			Log.e("BT", "GetRemoteDevice: Socket creation failed.", e);
		}

		if (mBluetoothAdapter.isDiscovering())
			mBluetoothAdapter.cancelDiscovery();
	}

	public void CloseSocket() {
		if (outStream != null) {
			try {
				outStream.flush();
			} catch (IOException e) {
				Log.e("BT", "CloseSocket: Couldn't flush output stream.", e);
				Toast.makeText(context, "Unable to close socket", Toast.LENGTH_LONG).show();
			}
		}

		try {
			if (btSocket != null)
				btSocket.close();
			Connected = false;

			if (D) {
				Log.d("BT", "socket closed");
			}

		} catch (Exception e2) {
			Log.e("BT", "CloseSocket: Unable to close socket.", e2);
		}
	}
}
