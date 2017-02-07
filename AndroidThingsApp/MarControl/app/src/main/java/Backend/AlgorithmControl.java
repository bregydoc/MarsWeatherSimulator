package Backend;


import android.util.Log;

public class AlgorithmControl {

    private float expectedTemperature, expectedO2, expectedCo2;
    private boolean[] currentStateOfValves;
    private float[] currentGasesValues;

    public AlgorithmControl(ValvesControl valvesControl, MarsI2cControl marsI2cControl){
        currentStateOfValves = new boolean[]{valvesControl.getO2ValveState(), valvesControl.getCO2ValveState(), valvesControl.getVacuumValveState()};
        currentGasesValues = new float[]{marsI2cControl.getO2(), marsI2cControl.getCo2()};
    }

    public void setExpectedValues(float temperature, float o2, float co2) {
        expectedTemperature = temperature;
        expectedO2 = o2;
        expectedCo2 = co2;
    }

    public void createAlgorithm() {

    }

    public int runBasicAlgorithm(ValvesControl valvesControl, MarsI2cControl marsI2cControl, boolean debugMode) {
        marsI2cControl.refreshAllValues();
        float do2 = expectedO2 - marsI2cControl.getO2();
        float dco2 = expectedCo2 - marsI2cControl.getCo2();
        if (debugMode) {
            System.out.println("do2: " + String.valueOf(do2) + ", dco2: " + String.valueOf(dco2));
        }
        if (dco2 < 0) {
            if (debugMode) {
                System.out.println("Opening vacuum valve");
            }
            valvesControl.openVacuumValve();
            while (dco2 < 0) {
                if (debugMode) {
                    System.out.println("Waiting while oxygen diff is lower than 0");
                }
                dco2 = expectedCo2 - marsI2cControl.getCo2();
            }
            if (debugMode) {
                System.out.println("Closing vacuum valve");
            }
            valvesControl.closeVacuumValve();

        }else if (do2 < 0) {
            valvesControl.openCO2Valve();
            while (do2 < 0) {
                if (debugMode) {
                    System.out.println("Waiting while carbon dioxide diff is lower than 0");
                }
                do2 = expectedO2 - marsI2cControl.getO2();
            }
            valvesControl.closeCO2Valve();

        }

        return 1;
    }
}
