package enterprises.nucleus.plugins.multiwii_bluetooth.comms;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class BT_New extends Communication {

	private static int ConnectingMethod = 2; // 2 to invoke

	// Debugging
	private static final String TAG = "BluetoothReadService";
	private static final boolean D = true;

	private static final UUID SerialPortServiceClass_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private final BluetoothAdapter mAdapter;

	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;

	private InputStream mmInStream;
	private OutputStream mmOutStream;

	SimpleQueue<Integer> fifo = new SimpleQueue<Integer>();

	public synchronized void start() {
		if (D)
			Log.d(TAG, "start");

		// Cancel any thread attempting to make a connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		setState(STATE_NONE);
		Connected = false;
	}

	public synchronized void connect(BluetoothDevice device) {
		if (D)
			Log.d(TAG, "connecting to: " + device);

		// Cancel any thread attempting to make a connection
		if (mState == STATE_CONNECTING) {
			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Start the thread to connect with the given device
		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
		setState(STATE_CONNECTING);

	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 * 
	 * @param socket
	 *            The BluetoothSocket on which the connection was made
	 * @param device
	 *            The BluetoothDevice that has been connected
	 */
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
		if (D)
			Log.d(TAG, "connected");

		// Cancel the thread that completed the connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();

		sendDeviceName(device.getName());

		setState(STATE_CONNECTED);
		Connected = true;

	}

	/**
	 * Stop all threads
	 */
	public synchronized void stop() {
		if (D)
			Log.d(TAG, "stop");

		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		setState(STATE_NONE);
		Connected = false;
		// Toast.makeText(context, context.getString(R.string.Disconnected),
		// Toast.LENGTH_LONG).show();
//		sendMessageToUI_Toast(context.getString(R.string.Disconnected));
	}

	/**
	 * Indicate that the connection attempt failed and notify the UI Activity.
	 */
	private void connectionFailed() {
		setState(STATE_NONE);
		Connected = false;
		Log.d(TAG, "connectionFailed");

//		sendMessageToUI_Toast(context.getString(R.string.Unabletoconnect));
	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	private void connectionLost() {
		setState(STATE_NONE);
		Connected = false;

		Log.d(TAG, "connectionLost");

//		sendMessageToUI_Toast(context.getString(R.string.ConnectionLost));
	}

	/**
	 * This thread runs while attempting to make an outgoing connection with a
	 * device. It runs straight through; the connection either succeeds or
	 * fails.
	 */
	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			Log.d(TAG, "ConnectThread Start - " + device.getAddress());

			mmDevice = device;
			BluetoothSocket tmp = null;

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice

			if (ConnectingMethod == 2) {
				try {
					Method m = device.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
					tmp = (BluetoothSocket) m.invoke(device, 1);
				} catch (Exception e) {
					try {
						tmp = device.createRfcommSocketToServiceRecord(SerialPortServiceClass_UUID);
					} catch (IOException e1) {
						Log.e(TAG, "createRfcommSocketToServiceRecord failed", e1);
					}

				}
			} else {
				try {
					tmp = device.createRfcommSocketToServiceRecord(SerialPortServiceClass_UUID);
				} catch (IOException e1) {
					Log.e(TAG, "createRfcommSocketToServiceRecord failed", e1);
				}
			}
			mmSocket = tmp;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectThread");
			setName("ConnectThread");

			// Always cancel discovery because it will slow down a connection

			if (mAdapter.isDiscovering()) {
				Log.i(TAG, "cancelDiscovery");
				mAdapter.cancelDiscovery();
			}

			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				Log.i(TAG, "trying to connect");
				mmSocket.connect();
				Connected = true;

				Log.i(TAG, "BT connection established, data transfer link open.");

			} catch (IOException e) {
				connectionFailed();
				// Close the socket
				try {
					mmSocket.close();
				} catch (IOException e2) {
					Log.e(TAG, "unable to close() socket during connection failure", e2);
				}

				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (this) {
				mConnectThread = null;
			}

			// Start the connected thread
			connected(mmSocket, mmDevice);
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}

	/**
	 * This thread runs during a connection with a remote device. It handles all
	 * incoming and outgoing transmissions.
	 */
	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;

		public ConnectedThread(BluetoothSocket socket) {
			Log.i(TAG, "create ConnectedThread");
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try {
				Log.i(TAG, "Geting Streams..");
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
				Log.i(TAG, "Streams OK");
			} catch (IOException e) {
				Log.e(TAG, "Geting Streams failed", e);
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;

		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");
			byte[] buffer = new byte[1024];
			int bytes;

			// Keep listening to the InputStream while connected
			while (true) {
				try {
					// Read from the InputStream
					bytes = mmInStream.read(buffer);

					for (int i = 0; i < bytes; i++)
						fifo.put(Integer.valueOf(buffer[i]));

					// Send the obtained bytes to the UI Activity
					mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();

					// String a = buffer.toString();

				} catch (IOException e) {
					Log.e(TAG, "disconnected", e);
					connectionLost();
					break;
				}
			}
		}

		/**
		 * Write to the connected OutStream.
		 * 
		 * @param buffer
		 *            The bytes to write
		 */
		public void write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);

				// Share the sent message back to the UI Activity
				// mHandler.obtainMessage(BlueTerm.MESSAGE_WRITE, buffer.length,
				// -1, buffer).sendToTarget();
			} catch (IOException e) {
				Log.e(TAG, "Exception during write", e);
			}
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}

	// //////////////////////////////////////////////////////////////////////////
	public BT_New(Context context) {
		super(context);
		if (D)
			Log.d(TAG, "BT_New");

		mAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mAdapter == null) {
			// Toast.makeText(context,
			// context.getString(R.string.Bluetoothisnotavailable),
			// Toast.LENGTH_LONG).show();
			return;
		}

		Enable();

	}

	@Override
	public void Enable() {
		if (D)
			Log.d(TAG, "Enable BT");

		mState = STATE_NONE;

		if (!mAdapter.isEnabled()) {
			// Toast.makeText(context, "Turning On Bluetooth...",
			// Toast.LENGTH_SHORT).show();
			mAdapter.enable();
			return;
		}

		start();
	}

	@Override
	public void Connect(String address, int speed) {
		if (D)
			Log.d(TAG, "Connect()");
		BluetoothDevice device = mAdapter.getRemoteDevice(address);
		// Toast.makeText(context, context.getString(R.string.Connecting) + " "
		// + device.getName(), Toast.LENGTH_LONG).show();

		setState(STATE_CONNECTING);
		connect(device);
	}

	@Override
	public synchronized boolean dataAvailable() {
		return !fifo.isEmpty();
	}

	@Override
	public synchronized byte Read() {
		BytesRecieved+=1;
		return (byte) (fifo.get() & 0xff);
	}

	@Override
	public synchronized void Write(byte[] arr) {
		super.Write(arr);
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (mState != STATE_CONNECTED)
				return;
			r = mConnectedThread;
		}
		// Perform the write unsynchronized
		r.write(arr);
	}

	@Override
	public synchronized void Close() {
		// Toast.makeText(context, "Disconnecting...",
		// Toast.LENGTH_SHORT).show();
		if (D)
			Log.d(TAG, "Close");
		if (mmOutStream != null) {
			try {
				mmOutStream.flush();
			} catch (IOException e) {
				Log.e(TAG, "ON PAUSE: Couldn't flush output stream.", e);
			}
		}
		stop();

	}

	@Override
	public synchronized void Disable() {
		try {
			if (mAdapter.isEnabled())
				if (D)
					Log.d(TAG, "Disable BT");
			mAdapter.disable();
		} catch (Exception e) {
			// Toast.makeText(context, "Can't dissable BT",
			// Toast.LENGTH_LONG).show();
		}

	}

}
