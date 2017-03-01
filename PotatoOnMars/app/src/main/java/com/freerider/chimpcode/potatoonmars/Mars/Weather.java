package com.freerider.chimpcode.potatoonmars.Mars;

import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

/**
 * Created by root on 2/18/17.
 */

public class Weather {

    PeripheralManagerService manager;

    private Atmosphere atmosphere;

    private Gpio O2Valve;
    private Gpio CO2Valve;
    private Gpio vacuumValve;

    private Gpio coolingSystem;
    private Gpio vacuumSystem;

    public Weather() {
        manager = new PeripheralManagerService();
        try {
            CO2Valve = manager.openGpio("BCM6");
            O2Valve = manager.openGpio("BCM5");
            vacuumValve = manager.openGpio("BCM16");

            CO2Valve.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            O2Valve.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            vacuumValve.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);


            CO2Valve.setActiveType(Gpio.ACTIVE_HIGH);
            O2Valve.setActiveType(Gpio.ACTIVE_HIGH);
            vacuumValve.setActiveType(Gpio.ACTIVE_HIGH);

            coolingSystem = manager.openGpio("BCM26");
            vacuumSystem = manager.openGpio("BCM20");


            coolingSystem.setActiveType(Gpio.ACTIVE_HIGH);
            vacuumSystem.setActiveType(Gpio.ACTIVE_HIGH);


            coolingSystem.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            vacuumSystem.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    ///////////////////////////////////// S Y S T E M S ///////////////////////////////////////

    public void activateCoolingSystem() {
        try {
            coolingSystem.setValue(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deactivateCoolingsystem() {
        try {
            coolingSystem.setValue(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void activateVacummSystem() {
        try {
            vacuumSystem.setValue(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deactivateVacummSystem() {
        try {
            vacuumSystem.setValue(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getCoolingsystemState() {
        try {
            return coolingSystem.getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getVacuumSystemState() {
        try {
            return vacuumSystem.getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //////////////////////////////////// V A L V E S //////////////////////////////////////////
    public void openO2Valve() {
        try {
            O2Valve.setValue(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeO2Valve() {
        try {
            O2Valve.setValue(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openCO2Valve() {
        try {
            CO2Valve.setValue(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeCO2Valve() {
        try {
            CO2Valve.setValue(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openVacuumValve() {
        try {
            vacuumValve.setValue(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeVacuumValve() {
        try {
            vacuumValve .setValue(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getO2ValveState(){
        try {
            return O2Valve.getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getCO2ValveState(){
        try {
            return CO2Valve.getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean getVacuumValveState(){
        try {
            return vacuumValve.getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void closeAllValves() {
        try {
            O2Valve.setValue(false);
            CO2Valve.setValue(false);
            vacuumValve.setValue(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deactivateAllSystems() {
        try {
            coolingSystem.setValue(false);
            vacuumSystem.setValue(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void launchCompleteVacummSystem() {

        try {
            openVacuumValve();
            Thread.sleep(1000);
            activateVacummSystem();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void stopCompleteVacummSystem() {
        try {
            deactivateVacummSystem();
            Thread.sleep(1000);
            closeVacuumValve();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public void disconnectValves() {
        if ((O2Valve != null)) {
            try {
                O2Valve.close();
                O2Valve = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if ((CO2Valve!= null)) {
            try {
                CO2Valve.close();
                CO2Valve = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if ((vacuumValve != null)) {
            try {
                vacuumValve.close();
                vacuumValve = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if ((coolingSystem != null)) {
            try {
                coolingSystem.close();
                coolingSystem = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if ((vacuumSystem != null)) {
            try {
                vacuumSystem.close();
                vacuumSystem = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void linkWithSwitchs(Switch o2Valve, Switch co2Valve, Switch vacValve, Switch coolSystem, Switch vacSystem) {
        o2Valve.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openO2Valve();
                }else{
                    closeO2Valve();
                }
            }
        });

        co2Valve.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openCO2Valve();
                }else{
                    closeCO2Valve();
                }
            }
        });

        vacValve.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openVacuumValve();
                }else{
                    closeVacuumValve();
                }
            }
        });

        vacSystem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    activateVacummSystem();
                }else{
                    deactivateVacummSystem();
                }
            }
        });

        coolSystem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    activateCoolingSystem();
                }else{
                    deactivateCoolingsystem();
                }
            }
        });

    }


}
