package com.freerider.chimpcode.potatoonmars.Mars;

/**
 * Created by Bregy on 2/18/17.
 */



import android.widget.Switch;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class EarthCommunication {

    public int pingCheck;
    FirebaseDatabase database;

    public EarthCommunication() {

        database = FirebaseDatabase.getInstance();
        DatabaseReference pingReference = database.getReference("pingState");

        pingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() ){
                    int value = ((Long)(dataSnapshot.getValue())).intValue();
                    pingCheck = value;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendCurrentMarsState(Weather weather, Atmosphere atmosphere, boolean withRefresh) {
        DatabaseReference currentState = database.getReference("current_state");
        currentState.child("co2_value").setValue(atmosphere.getCO2Concentration(withRefresh));
        currentState.child("o2_value").setValue(atmosphere.getO2Concentration(withRefresh));

        currentState.child("temperature_point_1").setValue(atmosphere.getAmbientTemperature(withRefresh)); // AMBIENT TEMPERATURE?
        currentState.child("temperature_point_3").setValue(atmosphere.getGroundTemperature(withRefresh)); //GROUND TEMPERATURE?

        currentState.child("o2_valve_state").setValue(weather.getO2ValveState());
        currentState.child("co2_valve_state").setValue(weather.getCO2ValveState());
        currentState.child("vacuum_valve_state").setValue(weather.getVacuumValveState());

        currentState.child("vacuum_system_state").setValue(weather.getVacuumSystemState());
        currentState.child("cooling_system_state").setValue(weather.getCoolingsystemState());

        DatabaseReference currentWeatherCloudState = database.getReference("current_state/weatherState");

        DatabaseReference o2Valve = currentWeatherCloudState.child("o2_valve");
        DatabaseReference co2Valve = currentWeatherCloudState.child("co2_valve");
        DatabaseReference vacuumValve = currentWeatherCloudState.child("vacuum_valve");
        DatabaseReference vacuumSystem = currentWeatherCloudState.child("vacuum_system");
        DatabaseReference coolingSystem = currentWeatherCloudState.child("cooling_system");
    }

    public void linkWeatherWithInternet(Weather weather, boolean saveLastValues) {
        DatabaseReference currentWeatherCloudState = database.getReference("current_state/weatherState");

        DatabaseReference o2Valve = currentWeatherCloudState.child("o2_valve");
        DatabaseReference co2Valve = currentWeatherCloudState.child("co2_valve");
        DatabaseReference vacuumValve = currentWeatherCloudState.child("vacuum_valve");
        DatabaseReference vacuumSystem = currentWeatherCloudState.child("vacuum_system");
        DatabaseReference coolingSystem = currentWeatherCloudState.child("cooling_system");

        if (saveLastValues) {
            o2Valve.setValue(weather.getO2ValveState());
            co2Valve.setValue(weather.getCO2ValveState());
            vacuumValve.setValue(weather.getVacuumValveState());
            vacuumSystem.setValue(weather.getVacuumSystemState());
            coolingSystem.setValue(weather.getCoolingsystemState());
        }

        o2Valve.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean value = (boolean) dataSnapshot.getValue();
                if (value) {
                    weather.openO2Valve();
                }else {
                    weather.closeO2Valve();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        co2Valve.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean value = (boolean) dataSnapshot.getValue();
                if (value) {
                    weather.openCO2Valve();
                }else {
                    weather.closeCO2Valve();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        vacuumValve.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean value = (boolean) dataSnapshot.getValue();
                if (value) {
                    weather.openVacuumValve();
                }else {
                    weather.closeVacuumValve();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        vacuumSystem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean value = (boolean) dataSnapshot.getValue();
                if (value) {
                    weather.activateVacummSystem();
                }else {
                    weather.deactivateVacummSystem();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        coolingSystem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean value = (boolean) dataSnapshot.getValue();
                if (value) {
                    weather.activateCoolingSystem();
                }else {
                    weather.deactivateCoolingsystem();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    public void publicHistoricalState(Atmosphere atmosphere, Weather weather, boolean withRefresh) {
        DatabaseReference history = database.getReference("history");
        DateCycle dateCycle = new DateCycle();

        Calendar c = dateCycle.getCurrentCalendar();
        String hour = dateCycle.getHourInString();

        int date = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        String nameOfChild = String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(date);
        nameOfChild = nameOfChild + "/" + hour;

        DatabaseReference intoHistoryData = history.child(nameOfChild);
        intoHistoryData.child("co2_value").setValue(atmosphere.getCO2Concentration(withRefresh));
        intoHistoryData.child("o2_value").setValue(atmosphere.getO2Concentration(withRefresh));
        intoHistoryData.child("temperature_point_1").setValue(atmosphere.getGroundTemperature(withRefresh));
        intoHistoryData.child("temperature_point_3").setValue(atmosphere.getAmbientTemperature(withRefresh));

    }

    public void linkCloudWithSwitchs(Switch o2Switch, Switch co2switch, Switch vacuumSwitch, Switch vacSystemSwitch, Switch coolSwitch) {
        DatabaseReference currentWeatherCloudState = database.getReference("current_state/weatherState");

        DatabaseReference o2Valve = currentWeatherCloudState.child("o2_valve");
        DatabaseReference co2Valve = currentWeatherCloudState.child("co2_valve");
        DatabaseReference vacuumValve = currentWeatherCloudState.child("vacuum_valve");
        DatabaseReference vacuumSystem = currentWeatherCloudState.child("vacuum_system");
        DatabaseReference coolingSystem = currentWeatherCloudState.child("cooling_system");

        o2Valve.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean value = (boolean) dataSnapshot.getValue();
                o2Switch.setChecked(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        co2Valve.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean value = (boolean) dataSnapshot.getValue();
                co2switch.setChecked(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        vacuumValve.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean value = (boolean) dataSnapshot.getValue();
                vacuumSwitch.setChecked(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        vacuumSystem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean value = (boolean) dataSnapshot.getValue();
                vacSystemSwitch.setChecked(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        coolingSystem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean value = (boolean) dataSnapshot.getValue();
                coolSwitch.setChecked(value);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
