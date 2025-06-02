package controller;

import model.CalendarModel;
import java.time.LocalDateTime;

/**
 * Parses commands to display the current status (availability) of the calendar.
 */
public class ShowStatusCommandParser implements ICommandParser {

  private CalendarModel calendar;

  /**
   * Constructs a ShowStatusCommandParser with the specified calendar model.
   *
   * @param calendar the calendar model to check status on
   */
  public ShowStatusCommandParser(CalendarModel calendar) {
    this.calendar = calendar;
  }

  /**
   * Parses the provided tokens to create a ShowStatusCommand.
   *
   * @param tokens the command tokens
   * @return a Command representing the status check operation
   */
  @Override
  public Command parse(String[] tokens) {
    try {
      if (tokens.length >= 4 && tokens[1].equalsIgnoreCase("status")
          && tokens[2].equalsIgnoreCase("on")) {
        String dateTimeStr = tokens[3];
        LocalDateTime dateTime = CommandParserStatic.parseDateTimeStatic(dateTimeStr);
        return new ShowStatusCommand(calendar, dateTime);
      } else {
        return () -> "Invalid show command.";
      }
    } catch (Exception e) {
      return () -> "Error processing show command: " + e.getMessage();
    }
  }
}
