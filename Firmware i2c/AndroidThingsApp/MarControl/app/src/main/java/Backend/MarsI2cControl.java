package Backend;


import android.widget.TextView;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MarsI2cControl {

    PeripheralManagerService manager;
    I2cDevice dueMarsControl;

    private float t1;
    private float t2;
    private float t3;

    private float globalTemp;

    private float o2;
    private float co2;


    public MarsI2cControl(int address) {
        manager = new PeripheralManagerService();

        try {
            dueMarsControl = manager.openI2cDevice("I2C1", address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshAllValues() {
        try {
            dueMarsControl.write("A".getBytes(), 1);
            byte[] buffer = new byte[44];

            dueMarsControl.read(buffer, 44);
            String str = new String(buffer, StandardCharsets.UTF_8);

            String[] tempArray = str.trim().split(",");
            t1  = Float.valueOf(tempArray[0]);
            t2  = Float.valueOf(tempArray[1]);
            t3  = Float.valueOf(tempArray[2]);
            o2  = Float.valueOf(tempArray[3]);
            co2  = Float.valueOf(tempArray[4]);


            globalTemp = (float) ((t1 + t2 + t3)/3.0);

        } catch (IOException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }


    public void putValuesInTextViews(TextView t1, TextView t2, TextView t3, TextView co2, TextView o2, TextView gTemp) {
        t1.setText(String.valueOf(getT1()) + " °C");
        t2.setText(String.valueOf(getT2()) + " °C");
        t3.setText(String.valueOf(getT3()) + " °C");

        co2.setText(String.valueOf(getCo2()) + " %ppm");
        o2.setText(String.valueOf(getO2()) + " %ppm");

        gTemp.setText(String.format("%.2f", getGlobalTemp()) + " °C");


    }

    public void closeConnection() {
        if (dueMarsControl != null) {
            try {
                dueMarsControl.close();
                dueMarsControl = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public float getT1() {
        return t1;
    }

    public float getT2() {
        return t2;
    }

    public float getT3() {
        return t3;
    }

    public float getO2() {
        return o2;
    }

    public float getCo2() {
        return co2;
    }

    public float getGlobalTemp() {
        return globalTemp;
    }
}
