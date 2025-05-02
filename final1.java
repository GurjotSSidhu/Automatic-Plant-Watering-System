package eecs1021;

import java.util.Timer;
import edu.princeton.cs.introcs.StdDraw;
import org.firmata4j.firmata.*;
import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import java.io.IOException;
import org.firmata4j.ssd1306.SSD1306;
public class final1 {
    static final String USBPORT = "/dev/cu.usbserial-0001";
    static final int PUMP = 7;
    static final int THESENSOR = 15;
    static final int dryvalue = 700;
    static final int moisturethreshold = 630;
    static final int wetvalue = 570;
    public static void main(String[] args)
            throws IOException, InterruptedException
    {
        int sample = 1;
        System.out.println("Scheduled job, Arduino task, starting.");

        IODevice myArduinoBoard = new FirmataDevice(USBPORT); // using the name of a port

            myArduinoBoard.start(); // start comms with board;

            myArduinoBoard.ensureInitializationIsDone();
            //set up led and turn it on
        Pin myLed = myArduinoBoard.getPin(PUMP);
        myLed.setMode(Pin.Mode.OUTPUT);



        var myTask = new ArduinoTask(myArduinoBoard);
        new Timer().schedule(myTask,0,1000);

        graphing();


        //get a report from scheduled task
        while(true){
            //value to terminal

            System.out.println("The Sensor value is:" + myTask.getSenValue());
            long moisture = myTask.getSenValue();
            SSD1306 oled = myTask.getOled();
            long buttonVal = myTask.getButtonValue();
            oled.clear();

            if(moisture > dryvalue){
                try{
                    myLed.setValue(1);

                    oled.getCanvas().setCursor(0, 0);
                    oled.getCanvas().write("Pump on");
                    oled.getCanvas().setCursor(0, 10);
                    oled.getCanvas().write("Soil is dry");
                    oled.display();

                    //state = "ON";
                } catch (Exception ex){
                    System.out.println("Pump Error");
                }
            }
            else if(moisture >= moisturethreshold){
                try{
                    myLed.setValue(1);

                    oled.getCanvas().setCursor(0, 0);
                    oled.getCanvas().write("Pump on");
                    oled.getCanvas().setCursor(0, 10);
                    oled.getCanvas().write("Soil is damp");
                    oled.display();
                   // state = "ON";
                } catch (Exception ex){
                    System.out.println("Pump Error");
                }
            }
            // Turns off pump when soil is wet enough
            else{
                try{
                    myLed.setValue(0);

                    oled.getCanvas().setCursor(0, 0);
                    oled.getCanvas().write("Pump off");
                    oled.getCanvas().setCursor(0, 10);
                    oled.getCanvas().write("Soil is wet");
                    oled.display();

                    //state = "OFF";
                } catch (Exception ex){
                    System.out.println("Pump Error");
                }

            }

            long volt = (myTask.getSenValue()*5)/1023;
            if(volt>5)
            {
                volt = 5;
            }
            else if(volt<0)
            {
                volt = 0;
            }
            //value to graph. Grab data from method in the other class(on TimerTask)
            StdDraw.text((double)sample,(double)volt,"*");
            //pause
            Thread.sleep(500);//wait 500 ms before asking another value
            //increment the sample and warp at 100 samples
            if (sample<100)
            {
                sample ++;

            }
            else{
                sample = 1;//reset
                StdDraw.clear();
                graphing();
            }

        }

    }
    public static void graphing()
    {
        //set up the graph
        StdDraw.setXscale(-3,100);
        StdDraw.setYscale(0,6);
        //pen parameters
        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(StdDraw.BLUE);
        //draw axes
        StdDraw.line(0,0,0,5);
        StdDraw.line(0,0,100,0);

        //label
        StdDraw.text(50,0.09,"[x]");
        StdDraw.text(-3,2.5,"[y]");
        StdDraw.text(-3,0,"[0]");
        StdDraw.text(-3,5,"[5]");
        StdDraw.text(50,5,"[Moisture(Volts) vs Time(500ms)]");
    }
}