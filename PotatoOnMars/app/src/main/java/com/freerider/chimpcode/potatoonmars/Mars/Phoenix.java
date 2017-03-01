package com.freerider.chimpcode.potatoonmars.Mars;

import android.app.Activity;

import com.freerider.chimpcode.potatoonmars.Artificial.DebugConsole;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by root on 2/18/17.
 */

public class Phoenix {

    private PeripheralManagerService manager;
    public I2cDevice systemCommunication;

    private Gpio resetPin;
    private DebugConsole console;

    public Phoenix(int address) {
        manager = new PeripheralManagerService();
        try {
            systemCommunication = manager.openI2cDevice("I2C1", address);

            resetPin = manager.openGpio("BCM25");
            resetPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            resetPin.setActiveType(Gpio.ACTIVE_HIGH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Phoenix(int address, DebugConsole console) {
        this.console = console;
        manager = new PeripheralManagerService();
        try {
            systemCommunication = manager.openI2cDevice("I2C1", address);

            resetPin = manager.openGpio("BCM25");
            resetPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            resetPin.setActiveType(Gpio.ACTIVE_HIGH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkCommunication() {
        String strResp = "";
        try {
            this.systemCommunication.write("C".getBytes(), 1);
            byte[] readBuffer = new byte[2];
            this.systemCommunication.read(readBuffer, 2);
            strResp = new String(readBuffer, StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (strResp.equals("ok")) {
            return true;
        }else{
            return false;
        }

    }

    public void softResetPhoenix(Activity activity) {
        if (this.console != null) {
            (new Thread(() -> {
                try {
                    activity.runOnUiThread(() -> {
                        console.printLogMessageWithDelimiter("RESET PHOENIX", "Starting Reset");
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            })).start();
        }

        try {
            resetPin.setValue(false);
            Thread.sleep(100);
            resetPin.setValue(true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (this.console != null) {
            (new Thread(() -> {
                try {
                    activity.runOnUiThread(() -> {
                        console.printLogMessageWithDelimiter("RESET PHOENIX", "Finished Reset");
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            })).start();
        }

    }

    public void softResetPhoenix() {

        try {
            resetPin.setValue(false);
            Thread.sleep(100);
            resetPin.setValue(true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (this.console != null) {
            console.printLogMessageWithDelimiter("RESET PHOENIX", "Finishing Reset");
        }

    }
}
