package Backend;


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
}
