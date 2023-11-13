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

import tme3.Controller;
import tme3.ControllerException;
import tme3.Event;
import tme3.Fixable;

import java.io.*;
import java.util.Date;
import java.util.Scanner;

//Added Implements Serializable Step3 Part3
public class GreenhouseControls extends Controller implements Serializable {

//All instance Variables

  private boolean light = false;
  private boolean water = false;
  private String thermostat = "Day";
  private String eventsFile = "examples1.txt";
  private boolean fans = false; //Added Step2 Part1
  private boolean windowok = true; //Added Step3 Part1, Default is true
  private boolean poweron = true; //Added step3 Part1, Default is true
  private int errorcode = 0; //Added Step3 Part3, Default is 0

//All Methods

  public int getError() { //Added for Step4 Part3
      return errorcode;
    }

    public Fixable getFixable(int errorcode) { //Added for Step4 Part3
        switch (errorcode) {
            case 1:
                return new FixWindow();
            case 2:
                return new PowerOn();
            default:
                return null;
        }
    }

//All Inner Classes

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

    public class FansOn extends Event { //Added Step2 Part1
        public FansOn(long delayTime) { super(delayTime); }
        public void action() {
            // Put hardware control code here to
            // physically turn on the fans.
            fans = true;
        }
        public String toString() { return "Fans are on"; }
    }

    public class FansOff extends Event { //Added Step2 Part1
        public FansOff(long delayTime) { super(delayTime); }
        public void action() {
            // Put hardware control code here to
            // physically turn off the fans.
            fans = false;
        }
        public String toString() { return "Fans are off"; }
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
  public class Bell extends Event { //Edited Step2 Part2
      private int count = 0;
      private int rings;

      public Bell(long delayTime, int rings) {
          super(delayTime);
          this.rings = rings;
      }

      public void action() {
          count++;
          if (count < rings) {
              addEvent(new Bell(2000, rings));
          }
      }
      public String toString() {
          return "Bell Bings!";
      }
  }

    public class WindowMalfunction extends Event { //Added Step3 Part1
        public WindowMalfunction(long delayTime) { super(delayTime); }
        public void action() throws ControllerException {
            // Put hardware control code here to
            // simulate a window malfunction.
            windowok = false;
            errorcode = 1;
            throw new ControllerException("Window malfunction!");
        }
        public String toString() { return "A Window Malfunction is Detected"; }
    }

    public class PowerOut extends Event { //Added Step3 Part1
        public PowerOut(long delayTime) { super(delayTime); }
        public void action() throws ControllerException {
            // Put hardware control code here to
            // simulate a power outage.
            poweron = false;
            errorcode = 2;
            throw new ControllerException("Power outage!");
        }
        public String toString() { return "A Power Outage is Detected"; }
    }

    public class PowerOn implements Fixable { //Added for Step4 Part2
        public void fix() {
            poweron = true;
            errorcode = 0;
        }

        public void log() {
            System.out.println("Power restored");
        }
    }

    public class FixWindow implements Fixable { //Added For Step4 Part2
        public void fix() {
            windowok = true;
            errorcode = 0;
        }

        public void log() {
            System.out.println("Window repaired");
        }
    }

    public class Restart extends Event { //Complete Edit Step2 Part3
        private String filename;

        public Restart(long delayTime, String filename) {
            super(delayTime);
            this.filename = filename;
        }

        public void action() {
            try {
                File file = new File(filename);
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    String eventName = parts[0].split("=")[1];
                    long delayTime = Long.parseLong(parts[1].split("=")[1]);

                    switch (eventName) {
                        case "ThermostatDay":
                            addEvent(new ThermostatDay(delayTime));
                            break;
                        case "ThermostatNight":
                            addEvent(new ThermostatNight(delayTime));
                            break;
                        case "Bell":
                            int rings = Integer.parseInt(parts[2].split("=")[1]);
                            addEvent(new Bell(delayTime, rings));
                            break;
                        case "WaterOn":
                            addEvent(new WaterOn(delayTime));
                            break;
                        case "WaterOff":
                            addEvent(new WaterOff(delayTime));
                            break;
                        case "LightOn":
                            addEvent(new LightOn(delayTime));
                            break;
                        case "LightOff":
                            addEvent(new LightOff(delayTime));
                            break;
                        case "FansOn":
                            addEvent(new FansOn(delayTime));
                            break;
                        case "FansOff":
                            addEvent(new FansOff(delayTime));
                            break;
                        case "Terminate":
                            addEvent(new Terminate(delayTime));
                            break;
                        case "WindowMalfunction": //Added Step3 Part1
                            addEvent(new WindowMalfunction(delayTime));
                            break;
                        case "PowerOut": //Added Step3 Part1
                            addEvent(new PowerOut(delayTime));
                            break;
                        default:
                            System.out.println("Unknown event: " + eventName);
                            break;
                    }
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + filename);
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


    @Override
    public void shutdown() { //Added Step3 Part2 and 3
        System.out.println("Emergency shutdown!");
        // Added Below for Step3 Part3
        try (PrintWriter out = new PrintWriter(new FileWriter("error_log.txt", true))) {
            out.println(new Date() + ": Emergency shutdown. Error code: " + errorcode);
            out.println(); //Adding a Blank space between error logs
        } catch (IOException e) {
            System.out.println("Error writing to log file: " + e);
        }
        // Serializing GreenhouseControls object
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("dump_out.txt"))) {
            out.writeObject(this);
        } catch (IOException e) {
            System.out.println("Error writing to dump file: " + e);
        }
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

            if (!(option.equals("-f")) && !(option.equals("-d"))) {
                System.out.println("Invalid option");
                printUsage();
            }

            if (option.equals("-f")) { //Edited Step4 Part5
                GreenhouseControls gc = new GreenhouseControls();
                gc.addEvent(gc.new Restart(0, filename));
                gc.run();
            } else if (option.equals("-d")) {
                Restore restore = new Restore(filename);
                restore.restore();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid number of parameters");
            printUsage();
        }
    }
} ///:~
