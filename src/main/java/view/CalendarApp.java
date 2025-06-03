package view;

import controller.CalendarController;
import controller.ICalendarController;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import model.CalendarModel;
import model.CalendarService;
import model.ICalendarModel;
import model.ICalendarService;

/**
 * The main application class for the Calendar App.
 * Supports both interactive and headless modes.
 */
public class CalendarApp {

  /**
   * The main method that will start Calendar App.
   *
   * @param args command line arguments; expected format: --mode
   *             interactive|headless [commandFile]
   */
  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("Usage: java View.CalendarApp --mode interactive|headless");
      return;
    }
    String mode = args[1].toLowerCase();

    ICalendarModel calendarModel = new CalendarModel();
    // Instantiate the service, which internally uses CSVExporter.
    ICalendarService service = new CalendarService(calendarModel);

    ICalendarController controller = new CalendarController(service);

    if (mode.equals("interactive")) {
      runInteractive(controller);
    } else if (mode.equals("headless")) {
      if (args.length < 3) {
        System.out.println("Headless mode requires a command file.");
        return;
      }
      runHeadless(controller, args[2]);
    } else {
      System.out.println("Unknown mode: " + args[1]);
    }
  }

  /**
   * Runs the Calendar App in interactive mode, accepting commands from standard input.
   *
   * @param controller the CalendarController instance to process commands
   */
  private static void runInteractive(ICalendarController controller) {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Interactive Calendar App. Type 'exit' to quit.");
    while (true) {
      System.out.print("> ");
      String line = scanner.nextLine();
      String result = controller.processCommand(line);
      if (result.equals("exit")) {
        break;
      }
      System.out.println(result);
    }
    scanner.close();
  }

  /**
   * Runs the Calendar App in headless mode by reading commands from a file.
   *
   * @param controller the CalendarController instance to process commands
   * @param fileName   the name of the command file
   */
  private static void runHeadless(ICalendarController controller, String fileName) {
    try (Scanner scanner = new Scanner(new File(fileName))) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String result = controller.processCommand(line);
        if (result.equals("exit")) {
          break;
        }
        System.out.println(result);
      }
    } catch (FileNotFoundException e) {
      System.out.println("Command file not found: " + fileName);
    }
  }
}
