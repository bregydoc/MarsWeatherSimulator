#include "Arduino.h"
#include <DallasTemperature.h>
#include <OneWire.h>
#include <Wire.h>

#include <Adafruit_NeoPixel.h>
// Es necesario modificar el archivo Wire.h, ya que este admite un buffer máximo
// de 32 bytes


//Control for NeoPixels:------------------------------------------------------------
#define PIN 6
#define NUMPIXELS 30
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUMPIXELS, PIN, NEO_GRB + NEO_KHZ800);
//----------------------------------------------------------------------------------


//#define DEBUG_MODE

void receiveEvent(int howMany);
void requestEvent();
void refreshO2AndC02();
void refreshTemperatureSensors();
char *floatToString1(char *outstr, double val, byte precision, byte widthp);
char *floatToString2(char *outstr, float value, int places, int minwidth, bool rightjustify);

void setAllLightsWithColor(int color);
void setMeshTwoColors(int color1, int color2);
void setBrightness(int bright);


float O2ppm = 0.0;
float CO2ppm = 0.0;
float temperatureValues[3] = {0.0, 0.0, 0.0};
float humidity = 0.0;

int stateRequest = 0;

int temperatureSensorsPins[3] = {A0, A1, A2};
int temperatureDigitalPins[3] = {5, 6, 7};
int utilLeds[4] = {46, 48, 50, 52};


// =========================== One wire interface =======================
OneWire temp1PinConnection(temperatureDigitalPins[0]);
DallasTemperature temp1Interface(&temp1PinConnection);

OneWire temp2PinConnection(temperatureDigitalPins[1]);
DallasTemperature temp2Interface(&temp2PinConnection);

OneWire temp3PinConnection(temperatureDigitalPins[2]);
DallasTemperature temp3Interface(&temp3PinConnection);

// =========================== =================  =======================

void initAllSensors() {
    for (int i = 0; i < 3; i++) {
        pinMode(temperatureSensorsPins[i], OUTPUT);
    }
    pinMode(LED_BUILTIN, OUTPUT);

    Serial1.begin(9600);
    Serial1.write("M 1\r\n");

    Serial2.begin(9600);
}

void setup() {
    Wire.begin(8);
    initAllSensors();
    // randomSeed(analogRead(6
    #ifdef DEBUG_MODE
    Serial.begin(9600);
    Serial.println("Debug mode actived, system ready.");
    #endif

    Wire.onReceive(receiveEvent);
    Wire.onRequest(requestEvent);

    pixels.begin();
    //ONLY TEST
    //pixels.setBrightness(10);
    setMeshTwoColors(pixels.Color(220,10,170), pixels.Color(12, 120, 90));
}

void loop() {
    refreshO2AndC02();
    refreshTemperatureSensors();


}

void receiveEvent(int howMany) {

    char c;
    while (0 < Wire.available()) {
        c = Wire.read();
    }
    if (c == 'A') {
        stateRequest = 2;
    }else if(c == 'L') {
        stateRequest = 10;
    }
}

void requestEvent() {
    char stringBuffer[8];
    if (stateRequest == 0) {

    } else if (stateRequest == 1) {

    } else if (stateRequest == 2) {
        // Write 8 + 1 + 8 + 1 + 8 + 1 + 8 + 1 + 8 = 44 bytes
        int precision = 2;
        int sizeOfNumberInString = 8;

        digitalWrite(LED_BUILTIN, HIGH);

        for (int i = 0; i < 3; i++) {
            Wire.write(floatToString2(stringBuffer, temperatureValues[i], precision,
            sizeOfNumberInString, false));
            Wire.write(',');
        }
        Wire.write(floatToString2(stringBuffer, CO2ppm, precision, sizeOfNumberInString, false));
        Wire.write(',');
        Wire.write(floatToString2(stringBuffer, O2ppm, precision, sizeOfNumberInString, false));
        digitalWrite(LED_BUILTIN, LOW);
    }
}

void refreshO2AndC02() {
    // Refresh current value of CO2 and O2
    Serial1.write("%\r\n");
    String rawO2 = Serial1.readString();
    Serial1.flush();
    String o2String = rawO2.substring(2, 8);

    #ifdef DEBUG_MODE
    Serial.println("Raw o2: " + rawO2);
    #endif
    // Invertido
    CO2ppm = o2String.toFloat();

    Serial2.write("z\r\n");
    String rawCO2 = Serial2.readString();
    Serial2.flush();

    String co2String = rawCO2.substring(2, 8);

    #ifdef DEBUG_MODE
    Serial.println("Raw co2: " + rawCO2);
    #endif
    // Invertido
    O2ppm = (co2String.toFloat()) / 100.0;

    // float co2Random = random(50,100);
    // float o2Random = random(5,100-co2Random);
    //
    // O2ppm = o2Random;
    // CO2ppm = co2Random;
}

void refreshTemperatureSensors() {
    /*
    for (int i=0;i<3;i++) {
    temperatureValues[i] =
    (((analogRead(temperatureSensorsPins[i])/4096.0)*330) - 0.5);
}
*/
    temp1Interface.requestTemperatures();
    temp2Interface.requestTemperatures();
    temp3Interface.requestTemperatures();

    temperatureValues[0] = temp1Interface.getTempCByIndex(0);
    temperatureValues[1] = temp2Interface.getTempCByIndex(0);
    temperatureValues[2] = temp3Interface.getTempCByIndex(0);
}

char *floatToString1(char *outstr, double val, byte precision, byte widthp) {
    char temp[16];
    byte i;

    double roundingFactor = 0.5;
    unsigned long mult = 1;
    for (i = 0; i < precision; i++) {
        roundingFactor /= 10.0;
        mult *= 10;
    }

    temp[0] = '\0';
    outstr[0] = '\0';

    if (val < 0.0) {
        strcpy(outstr, "-\0");
        val = -val;
    }

    val += roundingFactor;

    strcat(outstr, itoa(int(val), temp, 10));

    if (precision > 0) {
        strcat(outstr, ".\0");
        unsigned long frac;
        unsigned long mult = 1;
        byte padding = precision - 1;
        while (precision--)
        mult *= 10;

        if (val >= 0)
        frac = (val - int(val)) * mult;
        else
        frac = (int(val) - val) * mult;
        unsigned long frac1 = frac;

        while (frac1 /= 10)
        padding--;

        while (padding--)
        strcat(outstr, "0\0");

        strcat(outstr, itoa(frac, temp, 10));
    }

    if ((widthp != 0) && (widthp >= strlen(outstr))) {
        byte J = 0;
        J = widthp - strlen(outstr);

        for (i = 0; i < J; i++) {
            temp[i] = ' ';
        }

        temp[i++] = '\0';
        strcat(temp, outstr);
        strcpy(outstr, temp);
    }

    return outstr;
}

char *floatToString2(char *outstr, float value, int places, int minwidth, bool rightjustify) {
    int digit;
    float tens = 0.1;
    int tenscount = 0;
    int i;
    float tempfloat = value;
    int c = 0;
    int charcount = 1;
    int extra = 0;
    float d = 0.5;
    if (value < 0)
    d *= -1.0;
    for (i = 0; i < places; i++)
    d /= 10.0;
    tempfloat += d;

    if (value < 0)
    tempfloat *= -1.0;
    while ((tens * 10.0) <= tempfloat) {
        tens *= 10.0;
        tenscount += 1;
    }

    if (tenscount > 0)
    charcount += tenscount;
    else
    charcount += 1;

    if (value < 0)
    charcount += 1;
    charcount += 1 + places;

    minwidth += 1;
    if (minwidth > charcount) {
        extra = minwidth - charcount;
        charcount = minwidth;
    }

    if (extra > 0 and rightjustify) {
        for (int i = 0; i < extra; i++) {
            outstr[c++] = ' ';
        }
    }

    if (value < 0)
    outstr[c++] = '-';

    if (tenscount == 0)
    outstr[c++] = '0';

    for (i = 0; i < tenscount; i++) {
        digit = (int)(tempfloat / tens);
        itoa(digit, &outstr[c++], 10);
        tempfloat = tempfloat - ((float)digit * tens);
        tens /= 10.0;
    }

    if (places > 0)
    outstr[c++] = '.';

    for (i = 0; i < places; i++) {
        tempfloat *= 10.0;
        digit = (int)tempfloat;
        itoa(digit, &outstr[c++], 10);
        tempfloat = tempfloat - (float)digit;
    }
    if (extra > 0 and not rightjustify) {
        for (int i = 0; i < extra; i++) {
            outstr[c++] = ' ';
        }
    }

    outstr[c++] = '\0';
    return outstr;
}


void setAllLightsWithColor(int color) {
    for (int i=0;i<30;i++) {
        pixels.setPixelColor(i, color);
    }
    pixels.show();
}

void setMeshTwoColors(int color1, int color2) {
    for (int i=0;i<30;i++) {
        if (i%2 == 0) {
            pixels.setPixelColor(i, color1);
        } else {
            pixels.setPixelColor(i, color2);
        }
    }
    pixels.show();
}
void setBrightness(int bright) {
    pixels.setBrightness(bright);
}
