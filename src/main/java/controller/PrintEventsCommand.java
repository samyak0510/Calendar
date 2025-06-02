package controller;

import model.CalendarModel;
import model.Event;
import java.time.LocalDate;
import java.util.List;

/**
 * Command to print events for a specific date.
 */
public class PrintEventsCommand implements Command {

  private CalendarModel calendar;
  private LocalDate date;

  /**
   * Constructs a PrintEventsCommand for the given date.
   *
   * @param calendar the calendar model containing the events
   * @param date     the date for which to print events
   */
  public PrintEventsCommand(CalendarModel calendar, LocalDate date) {
    this.calendar = calendar;
    this.date = date;
  }

  /**
   * Executes the command by retrieving and formatting events for the specified date.
   *
   * @return a formatted string of events on the given date
   */
  @Override
  public String execute() {
    List<Event> events = calendar.getEventsOn(date);
    StringBuilder sb = new StringBuilder();
    sb.append("Events on ").append(date).append(":\n");
    for (Event event : events) {
      sb.append("- ").append(event.getSubject())
              .append(" at ").append(event.getStartDateTime()).append("\n");
    }
    return sb.toString();
  }
}
