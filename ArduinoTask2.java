package eecs1021;
import java.util.TimerTask;
import java.io.IOException;

import org.firmata4j.I2CDevice;
import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.ssd1306.SSD1306;

public class ArduinoTask extends TimerTask {
    private IODevice myArduinoBoard;
    private Pin myPump;
    private Pin mySensor;
    private long sampledSenValue;

    static final int PUMP = 7;
    static final int THESENSOR = 15;
    private long btnVal;
    private Pin myButton;
    static final int THEBUTTON = 6;
    SSD1306 theOledObject;
    //constructor
    public ArduinoTask(IODevice myArduinoBoard) {
        this.myArduinoBoard = myArduinoBoard;

        this.myPump = myArduinoBoard.getPin(PUMP);
        try {
            myPump.setMode(Pin.Mode.OUTPUT);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.mySensor = myArduinoBoard.getPin(THESENSOR);
        try {
            mySensor.setMode(Pin.Mode.ANALOG);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.myButton = myArduinoBoard.getPin(THEBUTTON);
        try {
            myButton.setMode(Pin.Mode.INPUT);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            I2CDevice i2cObject = myArduinoBoard.getI2CDevice((byte) 0x3C); // Use 0x3C for the Grove OLED
            this.theOledObject = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64); // 128x64 OLED SSD1515
            // Initialize the OLED (SSD1306) object
            theOledObject.init();

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public long getSenValue() {
        return sampledSenValue;
    }
    public SSD1306 getOled()
    {
        return theOledObject;
    }
    public long getButtonValue()
    {
        return btnVal;
    }
    //overide
    @Override
    public void run() {
        btnVal= myButton.getValue();
        if (myButton.getValue() == 1) {

        sampledSenValue = 700;
        }
        else{
        sampledSenValue = mySensor.getValue();
}

    }
}

