package Backend;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;


public class ChartsControl {

    LineData temperatureData;
    LineData O2AndCO2Data;

    LineDataSet temp1DataSet;
    LineDataSet temp2DataSet;
    LineDataSet temp3DataSet;

    LineDataSet globalTempDataSet;

    LineDataSet co2DataSet;
    LineDataSet o2DataSet;


    private int backgroundColor;
    private int t1Color;
    private int t3Color;
    private int t2Color;
    private int globalTempColor;

    private int o2Color;
    private int co2Color;

    private int[] auxForTemperatureBuffer;
    private int[] auxForGasesBuffer;


    public ChartsControl(LineChart temperatureChart, LineChart O2AndCO2Chart) {

        backgroundColor = Color.parseColor("#F5F5F5");
        t1Color = Color.parseColor("#EF5350");
        t2Color = Color.parseColor("#5C6BC0");
        t3Color = Color.parseColor("#CDDC39");
        globalTempColor = Color.parseColor("#FF8F00");

        co2Color = Color.parseColor("#26A69A");
        o2Color = Color.parseColor("#81D4FA");

        auxForTemperatureBuffer = new int[]{0, 0, 0, 0};
        auxForGasesBuffer = new int[]{0, 0};

        temperatureData = new LineData();
        O2AndCO2Data = new LineData();


        temperatureChart.getDescription().setEnabled(false);
        temperatureChart.setDrawGridBackground(false);
        temperatureChart.setTouchEnabled(false);
        temperatureChart.setDragEnabled(true);
        temperatureChart.setBackgroundColor(backgroundColor);

        temperatureChart.getAxisLeft().setEnabled(true);
        temperatureChart.getAxisLeft().setSpaceTop(40);
        temperatureChart.getAxisLeft().setSpaceBottom(40);
        temperatureChart.getAxisRight().setEnabled(false);

        temperatureChart.getXAxis().setEnabled(false);

        temperatureChart.getLegend().setEnabled(true);


        O2AndCO2Chart.getDescription().setEnabled(false);
        O2AndCO2Chart.setDrawGridBackground(false);
        O2AndCO2Chart.setTouchEnabled(false);
        O2AndCO2Chart.setDragEnabled(true);
        O2AndCO2Chart.setBackgroundColor(backgroundColor);

        O2AndCO2Chart.getAxisLeft().setEnabled(true);
        O2AndCO2Chart.getAxisLeft().setSpaceTop(40);
        O2AndCO2Chart.getAxisLeft().setSpaceBottom(40);
        O2AndCO2Chart.getAxisRight().setEnabled(false);

        O2AndCO2Chart.getXAxis().setEnabled(false);

        O2AndCO2Chart.getLegend().setEnabled(true);


        temp1DataSet = generateInitialData(30, "Temperatura 1");
        temp2DataSet = generateInitialData(30, "Temperatura 2");
        temp3DataSet = generateInitialData(30, "Temperatura 3");

        globalTempDataSet = generateInitialData(30, "Global");

        co2DataSet = generateInitialData(30, "Dioxido de carbono");
        o2DataSet = generateInitialData(30, "Oxigeno");


        temp1DataSet.setColor(t1Color);
        temp2DataSet.setColor(t2Color);
        temp3DataSet.setColor(t3Color);

        globalTempDataSet.setColor(globalTempColor);

        co2DataSet.setColor(co2Color);
        o2DataSet.setColor(o2Color);


        temp1DataSet.setDrawFilled(false);
        temp2DataSet.setDrawFilled(false);
        temp3DataSet.setDrawFilled(false);

        globalTempDataSet.setDrawFilled(false);

        co2DataSet.setDrawFilled(false);
        o2DataSet.setDrawFilled(false);


        temp1DataSet.setDrawCircles(false);
        temp2DataSet.setDrawCircles(false);
        temp3DataSet.setDrawCircles(false);

        globalTempDataSet.setDrawCircles(false);

        co2DataSet.setDrawCircles(false);
        o2DataSet.setDrawCircles(false);


        temp1DataSet.setDrawValues(false);
        temp2DataSet.setDrawValues(false);
        temp3DataSet.setDrawValues(false);

        globalTempDataSet.setDrawValues(false);

        co2DataSet.setDrawValues(false);
        o2DataSet.setDrawValues(false);


        temperatureData.addDataSet(temp1DataSet);
        temperatureData.addDataSet(temp2DataSet);
        temperatureData.addDataSet(temp3DataSet);
        temperatureData.addDataSet(globalTempDataSet);

        O2AndCO2Data.addDataSet(co2DataSet);
        O2AndCO2Data.addDataSet(o2DataSet);


        temperatureChart.setData(temperatureData);
        O2AndCO2Chart.setData(O2AndCO2Data);
    }

    private LineDataSet generateInitialData(int sizeOfBuffer, String nameOfDataSet) {
        ArrayList<Entry> vals = new ArrayList<Entry>();
        for (int i=0;i<sizeOfBuffer;i++) {
            vals.add(new Entry(i, (float) 0.0));
        }
        LineDataSet dataSet = new LineDataSet(vals, nameOfDataSet);
        return dataSet;
    }

    public void refreshTemperatureChart(LineChart temperatureChart, MarsI2cControl i2cControl) {
        int[] c = {0,0,0,0};
        float[] tempValues = {i2cControl.getT1(), i2cControl.getT2(), i2cControl.getT3(), i2cControl.getGlobalTemp()};

        for (int i=0;i<4;i++) {

            c[i] = temperatureChart.getLineData().getDataSetByIndex(i).getEntryCount();
            temperatureChart.getLineData().getDataSetByIndex(i).removeFirst();
            temperatureChart.getLineData().getDataSetByIndex(i).addEntry(new Entry(c[i]++ + auxForTemperatureBuffer[i], tempValues[i]));
            auxForTemperatureBuffer[i]++;
        }

        temperatureChart.getLineData().notifyDataChanged();
        temperatureChart.notifyDataSetChanged();
        temperatureChart.invalidate();

    }

    public void refreshGasesChart(LineChart gasesChart, MarsI2cControl i2cControl) {
        int[] c = {0,0};
        float[] tempValues = {i2cControl.getO2(), i2cControl.getCo2()};

        for (int i=0;i<2;i++) {

            c[i] = gasesChart.getLineData().getDataSetByIndex(i).getEntryCount();
            gasesChart.getLineData().getDataSetByIndex(i).removeFirst();
            gasesChart.getLineData().getDataSetByIndex(i).addEntry(new Entry(c[i]++ + auxForGasesBuffer[i], tempValues[i]));
            auxForGasesBuffer[i]++;
        }

        gasesChart.getLineData().notifyDataChanged();
        gasesChart.notifyDataSetChanged();
        gasesChart.invalidate();
    }


}
