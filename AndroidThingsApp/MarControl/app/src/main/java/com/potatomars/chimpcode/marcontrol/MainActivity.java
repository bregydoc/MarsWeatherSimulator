package com.potatomars.chimpcode.marcontrol;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import Backend.ChartsControl;
import Backend.DialogControl;
import Backend.MarsI2cControl;
import Backend.ValvesControl;

public class MainActivity extends AppCompatActivity {

    MarsI2cControl marsControl;
    ChartsControl chartsControl;

    DialogControl dialogConfigControl;

    ValvesControl valvesControl;

    LineChart temperatureChart;
    LineChart gasesChart;

    TextView txtT1;
    TextView txtT2;
    TextView txtT3;
    TextView txtGlobaltemp;

    TextView txtO2;
    TextView txtCO2;

    Button btnAuto;
    Button btnConfig;

    Switch btnO2;
    Switch btnCO2;
    Switch btnVacuum;

    ProgressBar progressBar;

    Activity myActivity;

    boolean flagAutomatic = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        valvesControl = new ValvesControl("BCM5", "BCM6", "BCM16");


        marsControl = new MarsI2cControl(8);
        myActivity = this;



        txtT1 = (TextView) findViewById(R.id.txtT1);
        txtT2 = (TextView) findViewById(R.id.txtT2);
        txtT3 = (TextView) findViewById(R.id.txtT3);
        txtGlobaltemp = (TextView) findViewById(R.id.txtTFinal);
        txtCO2 = (TextView) findViewById(R.id.txtDioxido);
        txtO2 = (TextView) findViewById(R.id.txtOxigeno);

        btnO2 = (Switch) findViewById(R.id.btnO2);
        btnCO2 = (Switch) findViewById(R.id.btnCO2);
        btnVacuum = (Switch) findViewById(R.id.btnVacuum);

        btnAuto = (Button) findViewById(R.id.btnAuto);
        btnConfig = (Button) findViewById(R.id.btnConfig);

        progressBar = (ProgressBar) findViewById(R.id.prgBar);
        progressBar.setVisibility(View.INVISIBLE);

        valvesControl.linkWithButtons(btnO2, btnCO2, btnVacuum);
        initListeners();
        lauchRefreshValuesTickThread();


        temperatureChart = (LineChart) findViewById(R.id.chart1);
        gasesChart = (LineChart) findViewById(R.id.chart2);

        chartsControl = new ChartsControl(temperatureChart, gasesChart);

        launchLoopedUIRefresh();

    }
    public void initListeners() {
        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfigControl = new DialogControl(myActivity);

            }
        });

        btnAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runAutomaticControl();


            }
        });
    }
    public void lauchRefreshValuesTickThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(10);
                        marsControl.refreshAllValues();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void runAutomaticControl() {
        if (flagAutomatic) {
            flagAutomatic = false;
            btnAuto.setText("AUTOMATIZAR");
            progressBar.setVisibility(View.INVISIBLE);



        }else {
            flagAutomatic = true;
            btnAuto.setText("DETENER");
            btnAuto.setTextAppearance(R.style.btnDetener);
            progressBar.setVisibility(View.VISIBLE);


        }









    }

    public void launchLoopedUIRefresh(){
        (new Thread(() -> {
            while (!Thread.interrupted())
                try {
                    Thread.sleep(1000);
                    runOnUiThread(() -> {

                        marsControl.putValuesInTextViews(txtT1, txtT2, txtT3, txtCO2, txtO2, txtGlobaltemp);
                        chartsControl.refreshTemperatureChart(temperatureChart, marsControl);
                        chartsControl.refreshGasesChart(gasesChart, marsControl);
                    });
                }
                catch (InterruptedException e) {
                    // ooops
                }
        })).start();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        marsControl.closeConnection();
        valvesControl.disconnectValves();
    }
}
