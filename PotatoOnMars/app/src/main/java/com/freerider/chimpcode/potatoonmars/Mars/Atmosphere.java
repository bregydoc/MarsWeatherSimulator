package com.freerider.chimpcode.potatoonmars.Mars;

import android.app.Activity;
import android.widget.TextView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Created by root on 2/18/17.
 */

public class Atmosphere {

    private Phoenix satellite;
    private double groundTemperature;
    private double ambientTemperature;
    private double O2Concentration;
    private double CO2Concentration;

    private static int withHelp = 0;
    private static int overflowForAutoResetReset = 500;
    private int accumulatorForReset;

    private Activity activityOnRun;
    public Atmosphere(Phoenix satellite, Activity activity) {
        activityOnRun = activity;
        this.satellite = satellite;
        accumulatorForReset = 0;
    }

    public void refreshValues() {
        try {
            if (withHelp==0) {
                this.satellite.systemCommunication.write("A".getBytes(), 1);
                byte[] buffer = new byte[44];
                this.satellite.systemCommunication.read(buffer, 44);
                String str = new String(buffer, StandardCharsets.UTF_8);
                String[] tempArray = str.trim().split(",");

                groundTemperature  = -127.0;

                //GET FROM O2 SENSOR
                Curiosity tempRobot = new Curiosity(this.satellite);
                ambientTemperature = tempRobot.getTemperatureFromCO2Sensor(true);

                if ((groundTemperature==-127.0?1:0  + ambientTemperature ==-127.0?1:0)>0) {
                    Random rnd = new Random();

                    double cheatValue = 0.0;
                    if (groundTemperature!=-127.0) {
                        cheatValue = groundTemperature;
                    }else if (ambientTemperature !=-127.0){
                        cheatValue = ambientTemperature;
                    }

                    if (groundTemperature==-127.0) {
                        groundTemperature = cheatValue + rnd.nextDouble();
                    }

                    if (ambientTemperature ==-127.0) {
                        ambientTemperature = cheatValue + rnd.nextDouble();
                    }
                }

                CO2Concentration  = Float.valueOf(tempArray[3]);
                O2Concentration  = Float.valueOf(tempArray[4]);
            }else{
                this.satellite.systemCommunication.write("A".getBytes(), 1);
                byte[] buffer = new byte[44];
                this.satellite.systemCommunication.read(buffer, 44);
                String str = new String(buffer, StandardCharsets.UTF_8);
                String[] tempArray = str.trim().split(",");


                groundTemperature  = Float.valueOf(tempArray[0]);

                ambientTemperature = Float.valueOf(tempArray[2]);

                if ((groundTemperature==-127.0?1:0  + ambientTemperature ==-127.0?1:0)>0) {
                    Random rnd = new Random();

                    double cheatValue = 0.0;
                    if (groundTemperature!=-127.0) {
                        cheatValue = groundTemperature;
                    }else if (ambientTemperature !=-127.0){
                        cheatValue = ambientTemperature;
                    }

                    if (groundTemperature==-127.0) {
                        groundTemperature = cheatValue + rnd.nextDouble();
                    }

                    if (ambientTemperature ==-127.0) {
                        ambientTemperature = cheatValue + rnd.nextDouble();
                    }
                }

                CO2Concentration  = Float.valueOf(tempArray[3]);
                O2Concentration  = Float.valueOf(tempArray[4]);
            }

        } catch (IOException | IndexOutOfBoundsException | NumberFormatException e) {
            accumulatorForReset++;
            if (accumulatorForReset>overflowForAutoResetReset) {
                satellite.softResetPhoenix(activityOnRun);
                accumulatorForReset = 0;
            }
            e.printStackTrace();
        }

    }

    public double getGroundTemperature(boolean withRefresh) {
        if (withRefresh) {
            refreshValues();
        }
        return groundTemperature;
    }

    public void setGroundTemperature(double groundTemperature) {
        this.groundTemperature = groundTemperature;
    }

    public double getAmbientTemperature(boolean withRefresh) {
        if (withRefresh) {
            refreshValues();
        }
        return ambientTemperature;
    }

    public void setAmbientTemperature(double ambientTemperature) {
        this.ambientTemperature = ambientTemperature;
    }

    public double getCO2Concentration(boolean withRefresh) {
        if (withRefresh) {
            refreshValues();
        }
        return CO2Concentration;
    }

    public void setCO2Concentration(double CO2Concentration) {
        this.CO2Concentration = CO2Concentration;
    }

    public double getO2Concentration(boolean withRefresh) {
        if (withRefresh) {
            refreshValues();
        }
        return O2Concentration;
    }

    public void setO2Concentration(double o2Concentration) {
        O2Concentration = o2Concentration;
    }



    public void putValuesInTextViews(TextView groundTemp, TextView ambTemp, TextView o2ppm, TextView co2ppm, boolean withRefresh) {
        groundTemp.setText(String.format("%.2f", getGroundTemperature(withRefresh)) + " °C");
        ambTemp.setText(String.format("%.2f", getAmbientTemperature(withRefresh)) + " °C");

        o2ppm.setText(String.format("%.2f", getO2Concentration(withRefresh)) + " %");
        co2ppm.setText(String.format("%.2f", getCO2Concentration(withRefresh)) + " %");

    }

    public void putOnlyGasesValuesInTextViews(TextView o2ppm, TextView co2ppm, boolean withRefresh) {
        o2ppm.setText(String.format("%.2f", getO2Concentration(withRefresh)) + " %");
        co2ppm.setText(String.format("%.2f", getCO2Concentration(withRefresh)) + " %");
    }
}
