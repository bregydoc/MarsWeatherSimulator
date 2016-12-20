package Backend;


import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import java.io.IOException;
import java.util.List;

public class SerialControl {

    String TAG = "SerialControl-Debug";
    private UartDevice mDevice;

    public SerialControl() {
        PeripheralManagerService manager = new PeripheralManagerService();
        List<String> deviceList = manager.getUartDeviceList();
        if (deviceList.isEmpty()) {
            Log.i(TAG, "No UART port available on this device.");
        } else {
            Log.i(TAG, "List of available devices: " + deviceList);
        }
    }

    public void initSerial() {
        try {
            PeripheralManagerService manager = new PeripheralManagerService();
            mDevice = manager.openUartDevice("UART0");
            mDevice.registerUartDeviceCallback(mUartCallback);
            configureUartFrame(mDevice);
        } catch (IOException e) {
            Log.w(TAG, "Unable to access UART device", e);
        }
    }

    public void closeConnection() {
        if (mDevice != null) {
            try {
                mDevice.close();
                mDevice = null;
            } catch (IOException e) {
                Log.w(TAG, "Unable to close UART device", e);
            }
        }
        assert mDevice != null;
        mDevice.unregisterUartDeviceCallback(mUartCallback);
    }

    private UartDeviceCallback mUartCallback = new UartDeviceCallback() {
        @Override
        public boolean onUartDeviceDataAvailable(UartDevice uart) {
            try {
                byte[] buffer = new byte[1];

                int count = uart.read(buffer, 1);
                System.out.println("--- " + String.valueOf(count) +" ------>  " + String.valueOf(buffer[0]));
            } catch (IOException e) {
                Log.w(TAG, "Unable to access UART device", e);
            }

            return true;
        }

        @Override
        public void onUartDeviceError(UartDevice uart, int error) {
            Log.w(TAG, uart + ": Error event " + error);
        }
    };

    public void configureUartFrame(UartDevice uart) throws IOException {
        // Configure the UART port
        uart.setBaudrate(115200);
        uart.setDataSize(8);
        uart.setParity(UartDevice.PARITY_NONE);
        uart.setStopBits(1);
    }
}
