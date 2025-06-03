package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class EventPrinter implements IEventPrinter
{

  private ICalendarModel model;

  public EventPrinter(ICalendarModel model) {
    this.model = model;
  }

  @Override
  public String printEventsOn(LocalDate date) throws Exception {
    List<Event> events = model.getEventsOn(date);
    StringBuilder sb = new StringBuilder();
    sb.append("Events on ").append(date).append(":\n");
    for (Event event : events) {
      sb.append("- ").append(event.getSubject())
          .append(" at ");

      String location = event.getLocation();
      if (location != null && !location.trim().isEmpty()) {
        sb.append(location).append(" ");
      }

      sb.append(event.getStartDateTime())
          .append("\n");
    }
    return sb.toString();
  }

  @Override
  public String printEventsRange(LocalDateTime start, LocalDateTime end) throws Exception {
    StringBuilder sb = new StringBuilder();
    sb.append("Events from ").append(start).append(" to ").append(end).append(":\n");
    List<Event> events = model.getAllEvents();
    for (Event event : events) {
      for (Event occurrence : event.getOccurrences()) {
        LocalDateTime occStart = occurrence.getStartDateTime();
        if ((occStart.equals(start) || occStart.isAfter(start)) && occStart.isBefore(end)) {
          sb.append("- ").append(occurrence.getSubject()).append(" at ");
          String location = occurrence.getLocation();
          if (location != null && !location.trim().isEmpty()) {
            sb.append(location).append(" ");
          }
          sb.append(occStart).append("\n");
        }
      }
    }
    return sb.toString();
  }
}
