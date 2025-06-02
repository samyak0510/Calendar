package controller;

import model.CalendarModel;
import model.Event;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Command to print events within a specified date-time range.
 */
public class PrintEventsRangeCommand implements Command {

  private CalendarModel calendar;
  private LocalDateTime start;
  private LocalDateTime end;

  /**
   * Constructs a PrintEventsRangeCommand using the provided start and end date-time strings.
   *
   * @param calendar   the calendar model to use
   * @param startDTStr the start date-time string
   * @param endDTStr   the end date-time string
   */
  public PrintEventsRangeCommand(CalendarModel calendar, String startDTStr, String endDTStr) {
    try {
      this.calendar = calendar;
      this.start = CommandParserStatic.parseDateTimeStatic(startDTStr);
      this.end = CommandParserStatic.parseDateTimeStatic(endDTStr);
    } catch (model.InvalidDateException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Executes the command by retrieving events within the specified range and formatting them.
   *
   * @return a formatted string of events within the given range
   */
  @Override
  public String execute() {
    StringBuilder sb = new StringBuilder();
    sb.append("Events from ").append(start).append(" to ").append(end).append(":\n");
    List<Event> events = calendar.getAllEvents();
    for (Event event : events) {
      for (Event occurrence : event.getOccurrences()) {
        LocalDateTime occStart = occurrence.getStartDateTime();
        if ((occStart.equals(start) || occStart.isAfter(start)) && occStart.isBefore(end)) {
          sb.append("- ").append(occurrence.getSubject())
                  .append(" at ").append(occStart).append("\n");
        }
      }
    }
    return sb.toString();
  }
}
