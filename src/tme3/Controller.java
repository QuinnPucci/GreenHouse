//: innerclasses/controller/Controller.java
// The reusable framework for control systems.
// From 'Thinking in Java, 4th ed.' (c) Bruce Eckel 2005
// www.BruceEckel.com. See copyright notice in CopyRight.txt.

/***********************************************************************
 * Adapated for COMP308 Java for Programmer, 
 *		SCIS, Athabasca University
 *
 * Assignment: TME3
 * @author: Steve Leung
 * @date  : Oct 21, 2006
 *
 */



package tme3;
import java.util.*;

// CONTROLLER MANAGER A LIST OF EVENTS

public class Controller {
  // A class from java.util to hold Event objects:
  private List<Event> eventList = new ArrayList<Event>();
  public void addEvent(Event c) { eventList.add(c); }

  public void run() {
    while(eventList.size() > 0)
      // Make a copy so you're not modifying the list
      // while you're selecting the elements in it:
      for(Event e : new ArrayList<Event>(eventList))
        if(e.ready()) {
          System.out.println(e); // this prints the events to string
          try { // now that action throws an exception, the call in run has to be in a try catch block.
              e.action(); // this calls the action
          } catch (ControllerException ex) {
              System.err.println("Emergency Shutdown Initiated"); // THERE IS SUPPOSED TO BE A METHOD HERE FOR EMERGENCY SHUTDOWN
              this.shutdown();
          }

          eventList.remove(e); // then removes it from the list
        }
  }

  // create shutdown method
  public void shutdown() {

  }
} ///:~
