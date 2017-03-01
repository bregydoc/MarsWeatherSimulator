package com.freerider.chimpcode.potatoonmars.Mars;

import android.app.Activity;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.freerider.chimpcode.potatoonmars.Artificial.DebugConsole;

import java.io.Console;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Created by root on 2/18/17.
 */

public class Curiosity {
    Phoenix satellite;

    double tempFromO2Sensor;

    public Curiosity(Phoenix satellite) {
        this.satellite = satellite;
        this.tempFromO2Sensor = 22.0;
    }

    public void linkResetWithButton(Button resetButton, Activity activity) {
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                satellite.softResetPhoenix(activity);
            }
        });
    }

    public double getTemperatureFromCO2Sensor(boolean withRefresh) {
        double lastTempValue = tempFromO2Sensor;
        if (withRefresh) {
            try {
                satellite.systemCommunication.write("F".getBytes(), 1);
                byte[] buffer = new byte[17];
                this.satellite.systemCommunication.read(buffer, 17);
                String str = new String(buffer, StandardCharsets.UTF_8);
                String[] tempArray = str.trim().split(",");
                //System.out.println("DEBUG LINE! ----> " + str);
                double temperatureFromCO2Sensor = Float.valueOf(tempArray[0]);
                tempFromO2Sensor = temperatureFromCO2Sensor;
            } catch (IOException | IndexOutOfBoundsException | NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (tempFromO2Sensor < 1 || tempFromO2Sensor > 30) {

        }

        double diff = (lastTempValue - tempFromO2Sensor);
        double uncertainty = diff < 0?diff*-1:diff;
        if ( uncertainty > 10.0) {
            return lastTempValue;
        }
        return tempFromO2Sensor;

    }

    public void putTempInTextView(TextView textView, boolean withRefresh) {
        textView.setText(String.format("%.2f", getTemperatureFromCO2Sensor(withRefresh)) + " °C");
    }

    public void runVacuumAutomaticControl (Weather weatherControl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1200000);
                        //Thread.sleep(180000);
                        weatherControl.openVacuumValve();
                        weatherControl.activateVacummSystem();
                        Thread.sleep(10000);
                        //Thread.sleep(5000);
                        weatherControl.closeVacuumValve();
                        weatherControl.deactivateVacummSystem();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();


    }

    public void runCO2AutomaticControl (double expectedCo2, Weather weatherControl, Atmosphere atmosphere) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (atmosphere.getCO2Concentration(true) < expectedCo2) {
                            weatherControl.openCO2Valve();
                            Thread.sleep(2000);
                            weatherControl.closeCO2Valve();
                        }
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();
    }

    public void runTemperatureControl (Weather weatherControl, Atmosphere atmosphere) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        if (atmosphere.getAmbientTemperature(true) > 23.0) {
                            weatherControl.activateCoolingSystem();

                            while (23.0 - 2 < atmosphere.getAmbientTemperature(true)) {
                                atmosphere.refreshValues();
                                Thread.sleep(2000);
                            }

                            weatherControl.deactivateCoolingsystem();
                        }
                        Thread.sleep(1000);
                        System.out.println("===== Running temperature control thread =====");
                        //System.out.println(Calendar.getInstance(TimeZone.getTimeZone("GMT-5:00")).get(Calendar.HOUR_OF_DAY));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }



            }
        }).start();
    }

}
