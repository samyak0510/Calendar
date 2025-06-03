package controller;

import model.ICalendarService;

/**
 * Controls the interaction between the calendar model and command parsing.
 */
public class CalendarController implements ICalendarController {

  private ICalendarService service;
  private IHeadCommandParser parser;


  public CalendarController(ICalendarService service) {
    this.service = service;
    this.parser = new HeadCommandParser(this.service);
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
