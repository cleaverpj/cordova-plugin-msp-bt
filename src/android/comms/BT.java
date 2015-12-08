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
//		MainActivity.appendTvConsole("Connecting");
		Log.d("BT", "Connecting");
		if (mBluetoothAdapter.isEnabled()) {
			try {
				GetRemoteDevice(address);
				btSocket.connect();
				Connected = true;

				Log.d("BT", "BT connection established, data transfer link open.");
 //               MainActivity.appendTvConsole("BT connection established, data transfer link open.");
			} catch (IOException e) {
				try {
					btSocket.close();
					Connected = false;

 //                   MainActivity.appendTvConsole("Unabletoconnect");
				} catch (IOException e2) {
					Log.e("BT", "ON RESUME: Unable to close socket during connection failure", e2);
  //                  MainActivity.appendTvConsole("Connection failure");
				}
			}

			// Create a data stream so we can talk to server.
			if (D)
				Log.d("BT", "+ getOutputStream  getInputStream +");

			try {
				outStream = btSocket.getOutputStream();
				inStream = btSocket.getInputStream();

			} catch (IOException e) {
				Log.e("BT", "ON RESUME: Output stream creation failed.", e);
  //              MainActivity.appendTvConsole("Stream creation failed");
			}
		}

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
		super.Write(arr);
		try {
			if (Connected)
				outStream.write(arr);
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
			Log.d("BT", "+ ON RESUME +");
			Log.d("BT", "+ ABOUT TO ATTEMPT CLIENT CONNECT +");
		}

		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		try {

			btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);

		} catch (IOException e) {
			Log.e("BT", "ON RESUME: Socket creation failed.", e);
//			Toast.makeText(context, context.getString(R.string.Unabletoconnect), Toast.LENGTH_LONG).show();
		}

		if (mBluetoothAdapter.isDiscovering())
			mBluetoothAdapter.cancelDiscovery();
	}

	public void CloseSocket() {
		if (outStream != null) {
			try {
				outStream.flush();
			} catch (IOException e) {
				Log.e("BT", "ON PAUSE: Couldn't flush output stream.", e);
				Toast.makeText(context, "Unable to close socket", Toast.LENGTH_LONG).show();
			}
		}

		try {
			if (btSocket != null)
				btSocket.close();
			Connected = false;

//			Toast.makeText(context, context.getString(R.string.Disconnected), Toast.LENGTH_LONG).show();

		} catch (Exception e2) {
			Log.e("BT", "ON PAUSE: Unable to close socket.", e2);
//			Toast.makeText(context, "Unable to close socket", Toast.LENGTH_LONG).show();
		}

	}

}
