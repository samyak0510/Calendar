package controller;

import model.CalendarModel;

/**
 * Controls the interaction between the calendar model and command parsing.
 */
public class CalendarController {

  private CommandParser parser;

  /**
   * Creates a new CalendarController.
   *
   * @param calendar the calendar model instance
   */
  public CalendarController(CalendarModel calendar) {
    this.parser = new CommandParser(calendar);
  }

  /**
   * Processes a given command line string by parsing and executing it.
   *
   * @param commandLine the command string to process
   * @return the result of executing the command, or an error message if one occurs
   */
  public String processCommand(String commandLine) {
    Command command = parser.parse(commandLine);
    if (command == null) {
      return "";
    }
    try {
      return command.execute();
    } catch (Exception e) {
      return "Error processing command: " + e.getMessage();
    }
  }
}
