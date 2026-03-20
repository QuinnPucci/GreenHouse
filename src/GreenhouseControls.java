//: innerclasses/GreenhouseControls.java
// This produces a specific application of the
// control system, all in a single class. Inner
// classes allow you to encapsulate different
// functionality for each type of event.
// From 'Thinking in Java, 4th ed.' (c) Bruce Eckel 2005
// www.BruceEckel.com. See copyright notice in CopyRight.txt.

/***********************************************************************
 * Adapated for COMP308 Java for Programmer, 
 *		SCIS, Athabasca University
 *
 * Assignment: TME3
 * @author: Steve Leung
 * @date  : Oct 21, 2005
 *
 */

import java.io.*;
import java.util.Calendar;
import tme3.*;


// BASICALLY THE MAIN APPLICATION OF AN EVENT DRIVEN SCHEDULING SYSTEM

public class GreenhouseControls extends Controller {

    // task one, fans
    private boolean fans = false;
    // ------

  private boolean light = false;
  private boolean water = false;

  private String thermostat = "Day";
  private String eventsFile = "examples1.txt";

    // FAN METHODS
    public class FansOn extends Event{
        public FansOn(long delayTime) {
            super(delayTime); // Inherit delayTime from event class.
        }
        public void action(){
            fans = true;
        }
        public String toString() {return "Fans are on";}
    }
    public class FansOff extends Event{
        public FansOff(long delayTime){
            super(delayTime);
        }

        public void action() {
            fans = false;
        }

        public String toString() {return "Fans are off";}
    }
    // ---------------------------------------------

  public class LightOn extends Event {
    public LightOn(long delayTime) { super(delayTime); }
    public void action() {
      // Put hardware control code here to
      // physically turn on the light.
      light = true;
    }
    public String toString() { return "Light is on"; }
  }
  public class LightOff extends Event {
    public LightOff(long delayTime) { super(delayTime); }
    public void action() {
      // Put hardware control code here to
      // physically turn off the light.
      light = false;
    }
    public String toString() { return "Light is off"; }
  }
  public class WaterOn extends Event {
    public WaterOn(long delayTime) { super(delayTime); }
    public void action() {
      // Put hardware control code here.
      water = true;
    }
    public String toString() {
      return "Greenhouse water is on";
    }
  }
  public class WaterOff extends Event {
    public WaterOff(long delayTime) { super(delayTime); }
    public void action() {
      // Put hardware control code here.
      water = false;
    }
    public String toString() {
      return "Greenhouse water is off";
    }
  }
  public class ThermostatNight extends Event {
    public ThermostatNight(long delayTime) {
      super(delayTime);
    }
    public void action() {
      // Put hardware control code here.
      thermostat = "Night";
    }
    public String toString() {
      return "Thermostat on night setting";
    }
  }
  public class ThermostatDay extends Event {
    public ThermostatDay(long delayTime) {
      super(delayTime);
    }
    public void action() {
      // Put hardware control code here.
      thermostat = "Day";
    }
    public String toString() {
      return "Thermostat on day setting";
    }
  }
  // An example of an action() that inserts a
  // new one of itself into the event list:

    // BELL modifications

    // notes.
    // ive set up a remaining rings variable that is decremented every time bell is rung.
    // NOW I need: another bell event is added... every 2000ms .... IF remainingRings is > 0

  public class Bell extends Event {

    public int remainingRings;

    public Bell(long delayTime, int remainingRings) {
        super(delayTime);
        this.remainingRings = remainingRings;
    }
    public void action() {
        remainingRings--; // everytime action is called on bell subtract the remaining rings
    }
    public String toString() { return "Bing!"; }
  }

  public class Restart extends Event {
    public Restart(long delayTime, String filename) {
      super(delayTime);
      eventsFile = filename; // here is the input file variable
    }

    public void action() {
	addEvent(new ThermostatNight(0));
	addEvent(new LightOn(2000));
	addEvent(new WaterOff(8000));
	addEvent(new ThermostatDay(10000));
	addEvent(new Bell(9000, 5)); // ADDED IN REMAINING RINGS IN THE HARDCODED DATA FOR TESTING.
	addEvent(new WaterOn(6000));
	addEvent(new LightOff(4000));
	addEvent(new Terminate(12000));
    }

    public String toString() {
      return "Restarting system";
    }
  }

  public class Terminate extends Event {
    public Terminate(long delayTime) { super(delayTime); }
    public void action() { System.exit(0); }
    public String toString() { return "Terminating";  }
  }


  public static void printUsage() {
    System.out.println("Correct format: ");
    System.out.println("  java GreenhouseControls -f <filename>, or");
    System.out.println("  java GreenhouseControls -d dump.out");
  }

//---------------------------------------------------------
    public static void main(String[] args) {
	try {
	    String option = args[0];
	    String filename = args[1];

	    if ( !(option.equals("-f")) && !(option.equals("-d")) ) {
		System.out.println("Invalid option");
		printUsage();
	    }

	    GreenhouseControls gc = new GreenhouseControls();

	    if (option.equals("-f"))  {
		gc.addEvent(gc.new Restart(0,filename)); // this is how we start it with the input
	    }

	    gc.run();
	}
	catch (ArrayIndexOutOfBoundsException e) {
	    System.out.println("Invalid number of parameters");
	    printUsage();
	}
    }

} ///:~
