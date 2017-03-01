package com.freerider.chimpcode.potatoonmars;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.freerider.chimpcode.potatoonmars.Artificial.DebugConsole;
import com.freerider.chimpcode.potatoonmars.Mars.Atmosphere;
import com.freerider.chimpcode.potatoonmars.Mars.Curiosity;
import com.freerider.chimpcode.potatoonmars.Mars.DateCycle;
import com.freerider.chimpcode.potatoonmars.Mars.EarthCommunication;
import com.freerider.chimpcode.potatoonmars.Mars.Phoenix;
import com.freerider.chimpcode.potatoonmars.Mars.Sun;
import com.freerider.chimpcode.potatoonmars.Mars.Weather;


public class ControlSystem extends AppCompatActivity {
    DateCycle dateCycle;
    TextView txtDateValue;
    DebugConsole console;
    //-------------------------------------
    //Creating the wonderful simulation!
    Phoenix phoenixSatellite;
    Atmosphere marsAtmosphere;
    Sun sun;
    Weather marsWeather;
    Curiosity robotForControl;
    //-------------------------------------

    //---------------------------
    //Temperatures and gases TextViews
    TextView txtGroundTemperature;
    TextView txtAmbientTemperature;

    TextView txtCO2Value;
    TextView txtO2Value;

    TextView txtTempFromO2;
    //Img for day cycle
    ImageView imgDayState;
    //---------------------------
    //-------------------------------------
    //Switch for valves and system control
    Switch swtO2Valve;
    Switch swtCO2Valve;
    Switch swtVacuumValve;

    Switch swtVacuumSystem;
    Switch swtCoolingSystem;

    //-------------------------------------
    Button btnSoftwareReset;
    Button btnControl;
    //Control for DB:
    EarthCommunication earthCommunication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_system);

        console = new DebugConsole((TextView) findViewById(R.id.txtDebugConsole));
        //Main simulation objects.
        phoenixSatellite = new Phoenix(8, console); //AtSAM System
        marsAtmosphere = new Atmosphere(phoenixSatellite, this);
        marsWeather = new Weather();
        sun = new Sun(phoenixSatellite);
        robotForControl = new Curiosity(phoenixSatellite);
        //-------------------------------------------------------------------
        //Init temperatures and gases TextViews:
        txtGroundTemperature = (TextView) findViewById(R.id.txtGroundTemp);
        txtAmbientTemperature = (TextView) findViewById(R.id.txtAmbientTemp);
        txtCO2Value = (TextView) findViewById(R.id.txtCO2Value);
        txtO2Value = (TextView) findViewById(R.id.txtO2Value);

        txtTempFromO2 = (TextView) findViewById(R.id.txtO2Temp);

        //ImageView for day cycle:
        imgDayState = (ImageView) findViewById(R.id.imgCurrentDate);
        //-------------------------------------------------------------------
        //Switch for control
        swtO2Valve = (Switch) findViewById(R.id.swtO2Valve);
        swtCO2Valve = (Switch) findViewById(R.id.swtCO2Valve);
        swtVacuumValve = (Switch) findViewById(R.id.swtVacuumValve);

        swtVacuumSystem = (Switch) findViewById(R.id.swtVacuumSystem);
        swtCoolingSystem = (Switch) findViewById(R.id.swtCoolingSystem);
        //-------------------------------------------------------------------

        //--------------------------------------------------------------
        //Button for reset:
        btnSoftwareReset = (Button) findViewById(R.id.btnSoftwareReset);
        //--------------------------------------------------------------
        btnControl = (Button) findViewById(R.id.btnControl);

        //Link with DB:
        earthCommunication = new EarthCommunication();

        dateCycle = new DateCycle();
        txtDateValue = (TextView) findViewById(R.id.txtDateValue);

        TextView txtDebugConsole = (TextView) findViewById(R.id.txtDebugConsole);
        console = new DebugConsole(txtDebugConsole);

        //-------------------------------------------------------------------------------
        btnControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runControlAlgorithms();
                btnControl.setClickable(false);
            }
        });
        robotForControl.linkResetWithButton(btnSoftwareReset, this);
        marsWeather.linkWithSwitchs(swtO2Valve, swtCO2Valve, swtVacuumValve, swtCoolingSystem, swtVacuumSystem);
        mainLoopAndTextViewsRefresh();
    }

    public void mainLoopAndTextViewsRefresh(){
        (new Thread(() -> {
            while (true)
                try {
                    Thread.sleep(1000);
                    runOnUiThread(() -> {
                        dateCycle.putHourInTextView(txtDateValue);
                        dateCycle.linkWithImageView(imgDayState);
                        sun.autoSincronizeWithEarthDate(dateCycle);
                        marsAtmosphere.putValuesInTextViews(txtGroundTemperature, txtAmbientTemperature, txtO2Value, txtCO2Value, false);
                        //marsAtmosphere.putOnlyGasesValuesInTextViews(txtO2Value, txtCO2Value, true);
                        earthCommunication.sendCurrentMarsState(marsWeather, marsAtmosphere, false);
                        robotForControl.putTempInTextView(txtTempFromO2, false);
                        //robotForControl.putTempInTextView(txtGroundTemperature, true);
                        console.printLogMessage("Refreshing UI", "Update the last values");
                    });
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
        })).start();
    }

    public void runControlAlgorithms() {
        robotForControl.runCO2AutomaticControl(0.8, marsWeather, marsAtmosphere);
        console.printLogMessage("Automatic control", "Running Co2 process");
        robotForControl.runTemperatureControl(marsWeather, marsAtmosphere);
        console.printLogMessage("Automatic control", "Running temperature process");
        robotForControl.runVacuumAutomaticControl(marsWeather);
        console.printLogMessage("Automatic control", "Running vacuum process");


    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

    }


}
