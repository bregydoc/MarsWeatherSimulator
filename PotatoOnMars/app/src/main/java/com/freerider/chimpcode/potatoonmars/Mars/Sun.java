package com.freerider.chimpcode.potatoonmars.Mars;



import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

/**
 * Created by root on 2/18/17.
 */

public class Sun {
    private int state;
    private Phoenix satellite;

    public int DAY = 0;
    public int NIGHT = 1;

    public Sun(Phoenix satellite) {
        this.state = 0;
        this.satellite = satellite;
    }

    public Sun(Phoenix satellite, int state) {
        this.state = state;
        this.satellite = satellite;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        if (state == DAY) {
            setDay();
        }else {
            setNight();
        }
    }

    public void setDay() {
        try {
            String strResp = "";
            do {
                this.satellite.systemCommunication.write("D".getBytes(), 1);
                byte[] readBuffer = new byte[2];
                this.satellite.systemCommunication.read(readBuffer, 2);
                strResp = new String(readBuffer, StandardCharsets.UTF_8);
                Thread.sleep(100);
            }while(!strResp.equals("ok"));

            this.state = DAY;


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setNight() {
        try {
            String strResp = "";
            do {
                this.satellite.systemCommunication.write("N".getBytes(), 1);
                byte[] readBuffer = new byte[2];
                this.satellite.systemCommunication.read(readBuffer, 2);
                strResp = new String(readBuffer, StandardCharsets.UTF_8);
                Thread.sleep(100);
            }while(!strResp.equals("ok"));

            this.state = NIGHT;


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void autoSincronizeWithEarthDate(DateCycle dateCycle) {
        Calendar c = dateCycle.getCurrentCalendar();
        int currentHour = c.get(Calendar.HOUR_OF_DAY);
        if (currentHour > 5 && currentHour<18) {
            setDay();
        }else{
            setNight();
        }
    }




}
