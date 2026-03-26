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
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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

  public class Bell extends Event {

    public int remainingRings;

    public Bell(long delayTime, int remainingRings) {
        super(delayTime);
        this.remainingRings = remainingRings;
    }
    public void action() {
        remainingRings--; // everytime action is called on bell subtract the remaining rings

        // here is how we handle the every 2 seconds
        if (remainingRings > 0) {
            // if there is any remaining rings left, add a new bell event with a delay time of 2s
            addEvent(new Bell(2000, remainingRings));
        }
    }
    public String toString() { return "Bing!"; }
  }

  public class Restart extends Event {
        String inputEvents; // make it an instance variable so I can use it in action
    public Restart(long delayTime, String filename) {
        super(delayTime);
        this.inputEvents = filename;
    }

    public void action() {

        // this is the scanner for opening the file and using the while loops to process it
        try {
            File in = new File(inputEvents);
            Scanner scan = new Scanner(in);

            // I relied on this alot for the regex stuff https://www.w3schools.com/java/java_regex.asp
            // define the patter im trying to match
            Pattern pattern = Pattern.compile("Event=(\\w+),time=(\\d+)(?:,rings=(\\d+))?");

            //loop through each line of the file
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                // use regex pattern to extract the right data from each line and use to create an event

                Matcher match = pattern.matcher(line);

                // use a conditional for each type of event, and add event accordingly
                // Ref: https://docs.oracle.com/javase/8/docs/api/java/util/regex/Matcher.html
                if (match.matches()) {
                    String eventName = match.group(1); // event name
                    long time = Long.parseLong(match.group(2)); // delay time

                    // if it matches, plug in the variables and create the event
                    if(eventName.equals("Bell")) {
                        int rings = Integer.parseInt(match.group(3));
                        addEvent(new Bell(time, rings));
                    } else if (eventName.equals("LightOn")) {
                        addEvent(new LightOn(time));
                    } else if (eventName.equals("LightOff")) {
                        addEvent(new LightOff(time));
                    } else if (eventName.equals("WaterOn")) {
                        addEvent(new WaterOn(time));
                    } else if (eventName.equals("WaterOff")) {
                        addEvent(new WaterOff(time));
                    } else if (eventName.equals("ThermostatNight")) {
                        addEvent(new ThermostatNight(time));
                    } else if (eventName.equals("ThermostatDay")) {
                        addEvent(new ThermostatDay(time));
                    } else if (eventName.equals("Terminate")) {
                        addEvent(new Terminate(time));
                    }
                }

            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
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
