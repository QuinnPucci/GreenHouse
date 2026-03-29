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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import static java.lang.Integer.parseInt;

import tme3.*;




// BASICALLY THE MAIN APPLICATION OF AN EVENT DRIVEN SCHEDULING SYSTEM

public class GreenhouseControls extends Controller implements Serializable {

    // task one, fans
    private boolean fans = false;
    // ------

    // step 3 instance variables
    private boolean windowok = true;
    private boolean poweron = true;
    private int errorcode;

  private boolean light = false;
  private boolean water = false;

  private String thermostat = "Day";
  private String eventsFile;

  // override controllers shutdown method
  // functionality here so I can access error code
  @Override
  public void shutdown(){

      // declare and init the strings going in the logs
      String reason = "";
      String seperator = "----------------------------";
      // used this for time -> https://www.w3schools.com/java/java_date.asp
      LocalTime now = LocalTime.now();
      String timeLog = "Time: " + now.toString();

      // ERROR.LOG LOGIC
      if (errorcode == 1){
          reason = "Error Code 1: Window Malfunction has caused emergency shutdown procedure.";
      } else if (errorcode == 2){
          reason = "Error Code 2: Power Outage has caused emergency shutdown procedure.";
      }
      // using try-with-resources so it automatically closes the writer when done (no need for finally)
      try (PrintWriter out = new PrintWriter(
              new BufferedWriter(new FileWriter("error.log",true)))) { // true so it appends to error log instead of overwrite
          // shutdown message
          out.println(reason);
          // time stamp
          out.println(timeLog);
          // clean the log by using a seperator line
          out.println(seperator);

      } catch (IOException e) {
          System.err.println("IO Error");
          throw new RuntimeException(e);
          // throw unchecked exception so the program fails and terminates
      }

      // DUMP.OUT LOGIC
      try (ObjectOutputStream obOut = new ObjectOutputStream(new FileOutputStream("dump.out"))) {
          obOut.writeObject(this);
      }
      catch (FileNotFoundException e) {
          System.err.println("File not Found");
          throw new RuntimeException(e);
      }
      catch (IOException e){
          System.err.println("IO Error");
          throw new RuntimeException(e);
      }

      // console print and terminate program
      System.out.println(reason);
      System.out.println(timeLog);
      System.exit(0);

  }

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

  // -------------- PROBLEMS CLASSES (Step 3) --------------------

  public class WindowMalfunction extends Event {
      public WindowMalfunction(long delayTime) {
          super(delayTime);
      }
      // throws controller exception, sets windowok to false, and error code to 1
      public void action() throws ControllerException{
          windowok = false;
          errorcode = 1;
          throw new ControllerException();
      }
      public String toString() { return "Window Malfunction"; }
  }

  public class PowerOut extends Event {
      public PowerOut(long delayTime) {
          super(delayTime);
      }
      // throws controller exception, sets poweron to false, and error code to 2
      public void action() throws ControllerException{
          poweron = false;
          errorcode = 2;
          throw new ControllerException();
      }
      public String toString() { return "Power Out";}
  }
  // -------------- PROBLEMS END -------------------------

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

                    // if it matches an events name
                    if(eventName.equals("Bell")) {

                        // Bell needed another conditional to handle the example files that do not
                        // supply a number of rings, such as examples1
                        // check if the third regex group (for rings) is null
                        if (match.group(3) != null) {
                            int rings = parseInt(match.group(3)); // if not null, parse it
                            addEvent(new Bell(time, rings)); // plug in the variables and create the event
                        } else if (match.group(3) == null) {
                            int rings = 1; // if it is null, set rings to 1
                            addEvent(new Bell(time, rings));
                        }

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
                    } else if (eventName.equals("WindowMalfunction")) {
                        addEvent(new WindowMalfunction(time));
                    } else if (eventName.equals("PowerOut")) {
                        addEvent(new PowerOut(time));
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
