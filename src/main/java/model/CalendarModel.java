package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the calendar model that stores events and provides methods to add and query events.
 */
public class CalendarModel {

  private List<Event> events;

  /**
   * Constructs a new CalendarModel with an empty list of events.
   */
  public CalendarModel() {
    events = new ArrayList<>();
  }

  /**
   * Adds an event to the calendar. If a conflict is found and autoDecline is true,
   * an exception is thrown.
   *
   * @param event       the event to add
   * @param autoDecline if true, conflicts cause the event to be declined
   * @throws EventConflictException if the event conflicts with an existing event
   */
  public void addEvent(Event event, boolean autoDecline) throws EventConflictException {
    for (Event existingEvent : events) {
      if (existingEvent.conflictsWith(event)) {
        if (autoDecline) {
          throw new EventConflictException("Event '" + event.getSubject() +
                  "' conflicts with existing event '" + existingEvent.getSubject() + "'.");
        }
      }
    }
    events.add(event);
  }

  /**
   * Retrieves all events that occur on the specified date.
   *
   * @param date the date to check
   * @return a list of events occurring on the specified date
   */
  public List<Event> getEventsOn(LocalDate date) {
    List<Event> result = new ArrayList<>();
    for (Event event : events) {
      if (occursOnDate(event, date)) {
        result.add(event);
      }
    }
    return result;
  }

  /**
   * Checks if an event occurs on a given date.
   *
   * @param event the event to check
   * @param date  the date to verify
   * @return true if the event occurs on the specified date, false otherwise
   */
  private boolean occursOnDate(Event event, LocalDate date) {
    if (event instanceof SingleEvent) {
      SingleEvent se = (SingleEvent) event;
      LocalDate start = se.getStartDateTime().toLocalDate();
      LocalDate end = se.getEffectiveEndDateTime().toLocalDate();
      return !date.isBefore(start) && !date.isAfter(end);
    } else if (event instanceof RecurringEvent) {
      RecurringEvent re = (RecurringEvent) event;
      for (SingleEvent occurrence : re.generateOccurrences()) {
        if (occurrence.getStartDateTime().toLocalDate().equals(date)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Returns all events in the calendar.
   *
   * @return a list of all events
   */
  public List<Event> getAllEvents() {
    return new ArrayList<>(events);
  }
}

