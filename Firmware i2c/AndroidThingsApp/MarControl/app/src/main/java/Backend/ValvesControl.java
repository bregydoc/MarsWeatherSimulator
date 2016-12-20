package Backend;



import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class ValvesControl {

    PeripheralManagerService manager;

    private Gpio valveO2;
    private Gpio valveCO2;
    private Gpio valveVacuum;


    public ValvesControl(String pinValveO2, String pinValveCO2, String pinValveVacuum) {
        manager = new PeripheralManagerService();
        try {
            valveCO2 = manager.openGpio(pinValveCO2);
            valveO2 = manager.openGpio(pinValveO2);
            valveVacuum = manager.openGpio(pinValveVacuum);

            valveCO2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            valveO2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            valveVacuum.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);


            valveCO2.setActiveType(Gpio.ACTIVE_HIGH);
            valveO2.setActiveType(Gpio.ACTIVE_HIGH);
            valveVacuum.setActiveType(Gpio.ACTIVE_HIGH);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void openO2Valve() {
        try {
            valveO2.setValue(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeO2Valve() {
        try {
            valveO2.setValue(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openCO2Valve() {
        try {
            valveCO2.setValue(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeCO2Valve() {
        try {
            valveCO2.setValue(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openVacuumValve() {
        try {
            valveVacuum.setValue(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeVacuumValve() {
        try {
            valveVacuum.setValue(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeAllValves() {
        try {
            valveO2.setValue(false);
            valveCO2.setValue(false);
            valveVacuum.setValue(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnectValves() {
        if ((valveO2 != null)) {
            try {
                valveO2.close();
                valveO2 = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if ((valveCO2 != null)) {
            try {
                valveCO2.close();
                valveCO2 = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if ((valveVacuum != null)) {
            try {
                valveVacuum.close();
                valveVacuum = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean getO2ValveState(){
        try {
            return valveO2.getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getCO2ValveState(){
        try {
            return valveCO2.getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean getVacuumValveState(){
        try {
            return valveVacuum.getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void linkWithButtons(Switch o2, Switch co2, Switch vacuum){
        o2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openO2Valve();
                }else{
                    closeO2Valve();
                }
            }
        });

        co2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openCO2Valve();
                }else{
                    closeCO2Valve();
                }
            }
        });

        vacuum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openVacuumValve();
                }else{
                    openVacuumValve();
                }
            }
        });
    }
}
