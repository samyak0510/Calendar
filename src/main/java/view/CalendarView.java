package view;

import controller.ICalendarController;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Handles user interaction in both interactive and headless modes.
 */
public class CalendarView implements ICalendarView {

  String[] args;
  private final ICalendarController controller;

  /**
   * Constructs a CalendarView with a controller and command-line arguments.
   *
   * @param controller the calendar controller
   * @param args       the command-line arguments
   */
  public CalendarView(ICalendarController controller, String[] args) {
    this.controller = controller;
    this.args = args;
  }

  /**
   * Starts the application in interactive or headless mode based on the input arguments.
   */
  @Override
  public void start() {
    String mode = args[1].toLowerCase();
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

  private void runInteractive(ICalendarController controller) {
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

  private void runHeadless(ICalendarController controller, String fileName) {
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
