package controller;

import model.CalendarModel;
import model.Event;
import model.SingleEvent;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Command to show the availability status of the calendar at a given time.
 */
public class ShowStatusCommand implements Command {

  private CalendarModel calendar;
  private LocalDateTime dateTime;

  /**
   * Constructs a ShowStatusCommand with the given calendar model and date-time.
   *
   * @param calendar the calendar model to check for events
   * @param dateTime the specific date and time to check status
   */
  public ShowStatusCommand(CalendarModel calendar, LocalDateTime dateTime) {
    this.calendar = calendar;
    this.dateTime = dateTime;
  }

  /**
   * Executes the command by checking if any event overlaps the specified date-time.
   *
   * @return "Busy" if an event is found at the given time, "Available" otherwise
   */
  public String execute() {
    boolean busy = false;
    List<Event> events = calendar.getAllEvents();
    for (Event event : events) {
      for (Event occurrence : event.getOccurrences()) {
        if (occurrence instanceof SingleEvent) {
          SingleEvent se = (SingleEvent) occurrence;
          if (se.getStartDateTime().isBefore(dateTime)
              && se.getEffectiveEndDateTime().isAfter(dateTime)) {
            busy = true;
            break;
          }
        }
      }
      if (busy) {
        break;
      }
    }
    return busy ? "Busy" : "Available";
  }
}
