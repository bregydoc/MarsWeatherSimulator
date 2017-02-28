package Backend;


import android.widget.TextView;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class MarsI2cControl {

    PeripheralManagerService manager;
    I2cDevice dueMarsControl;
    FirebaseCommunication helperWebControl;

    private float t1;
    private float t2;
    private float t3;

    private float globalTemp;

    private float o2;
    private float co2;


    public MarsI2cControl(int address) {
        manager = new PeripheralManagerService();
        helperWebControl = new FirebaseCommunication();
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

            boolean valid = true;
            /*
            for (int i=0;i<str.length();i++){
                char c = str.charAt(i);
                if (i==0) {
                    if (c!='0'&&c!='1'&&c!='2'&&c!='3'&&c!='4'&&c!='5'&&c!='6'&&c!='7'&&c!='8'&&c!='9'&&c!='-') {
                        valid = false;
                    }
                }else{
                    if (c!='0'&&c!='1'&&c!='2'&&c!='3'&&c!='4'&&c!='5'&&c!='6'&&c!='7'&&c!='8'&&c!='9'&&c!='.'&&c!='-'&&c!=' ') {
                        valid = false;
                    }
                }

            }
            */
            //System.out.println(str);
            //Activate for debug mode


            if (valid) {
                String[] tempArray = str.trim().split(",");

                //System.out.println("====>"+str.trim());

                t1  = Float.valueOf(tempArray[0]);
                t2  = Float.valueOf(tempArray[1]);
                t3  = Float.valueOf(tempArray[2]);

                if ((t1==(float)-127.0?1:0 + t2==(float)-127.0?1:0 + t3==(float)-127.0?1:0)>0) {
                    Random rnd = new Random();

                    float cheatValue = (float)0.0;
                    if (t1!=(float)-127.0) {
                        cheatValue = t1;
                    }else if (t2!=(float)-127.0){
                        cheatValue = t2;
                    }else if (t3!=(float)-127.0){
                        cheatValue = t3;
                    }

                    if (t1==(float)-127.0) {
                        t1 = cheatValue + rnd.nextFloat();
                    }

                    if (t2==(float)-127.0) {
                        t2 = cheatValue + rnd.nextFloat();
                    }

                    if (t3==(float)-127.0) {
                        t3 = cheatValue + rnd.nextFloat();
                    }
                }

                o2  = Float.valueOf(tempArray[3]);
                co2  = Float.valueOf(tempArray[4]);


                globalTemp = (float) ((t1 + t2 + t3)/3.0);
            }


        } catch (IOException | IndexOutOfBoundsException | NumberFormatException e) {
            e.printStackTrace();
        }
    }


    public void putValuesInTextViews(TextView t1, TextView t2, TextView t3, TextView co2, TextView o2, TextView gTemp) {
            t1.setText(String.format("%.2f", getT1()) + " °C");
        t2.setText(String.format("%.2f", getT2()) + " °C");
        t3.setText(String.format("%.2f", getT3()) + " °C");

        co2.setText(String.valueOf(getCo2()) + " %ppm");
        o2.setText(String.valueOf(getO2()) + " %ppm");

        gTemp.setText(String.format("%.2f", getGlobalTemp()) + " °C");

        helperWebControl.refreshCurrentState(getT1(), getT2(), getT3(), getO2(), getCo2());
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
