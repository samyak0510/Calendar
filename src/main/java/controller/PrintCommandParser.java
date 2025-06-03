package controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import model.ICalendarService;


public class PrintCommandParser implements ICommandParser {

  private ICalendarService calendar;
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");


  public PrintCommandParser(ICalendarService calendar) {
    this.calendar = calendar;
  }

  /**
   * Parses the provided tokens and creates the appropriate print command.
   *
   * @param tokens the array of command tokens
   * @return a Command instance for printing events
   */
  @Override
  public Command parse(String[] tokens) {
    try {
      int index = 1;
      if (tokens.length >= 4 && tokens[index].equalsIgnoreCase(
          "events") && tokens[index + 1].equalsIgnoreCase("on")) {
        String dateStr = tokens[index + 2];
        LocalDate date = LocalDate.parse(dateStr, DATE_FORMAT);
        return new PrintEventsCommand(calendar, date);
      } else if (tokens.length >= 6 && tokens[index].equalsIgnoreCase(
          "events") && tokens[index + 1].equalsIgnoreCase("from")) {
        String startDTStr = tokens[index + 2];
        String endDTStr = tokens[index + 4];
        return new PrintEventsRangeCommand(calendar, startDTStr, endDTStr);
      } else {
        return () -> "Invalid print command.";
      }
    } catch (DateTimeParseException e) {
      return () -> "Invalid date format. Expected 'YYYY-MM-DD'";
    } catch (Exception e) {
      return () -> "Error processing print command: " + e.getMessage();
    }
  }
}
