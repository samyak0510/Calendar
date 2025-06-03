package controller;


import model.ICalendarService;

/**
 * Parses export commands and creates the corresponding export command.
 */
public class ExportCommandParser implements ICommandParser {
  private ICalendarService calendarService;


  public ExportCommandParser(ICalendarService calendarService ) {
    this.calendarService = calendarService;
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
        return new ExportCalendarCommand(calendarService, fileName);
      } else {
        return () -> "Invalid export command.";
      }
    } catch (Exception e) {
      return () -> "Error processing export command: " + e.getMessage();
    }
  }
}
