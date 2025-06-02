package controller;

import model.CalendarModel;

/**
 * Parses export commands and creates the corresponding export command.
 */
public class ExportCommandParser implements ICommandParser {
  private CalendarModel calendar;

  /**
   * Constructs an ExportCommandParser with the specified calendar model.
   *
   * @param calendar the calendar model to be used for exporting
   */
  public ExportCommandParser(CalendarModel calendar) {
    this.calendar = calendar;
  }

  /**
   * Parsing the provided tokens to create an export command.
   *
   * @param tokens the command tokens
   * @return a Command representing the export operation
   */
  @Override
  public Command parse(String[] tokens) {
    try {
      if (tokens.length >= 3 && tokens[1].equalsIgnoreCase("cal")) {
        String fileName = tokens[2];
        return new ExportCalendarCommand(calendar, fileName);
      } else {
        return () -> "Invalid export command.";
      }
    } catch (Exception e) {
      return () -> "Error processing export command: " + e.getMessage();
    }
  }
}
