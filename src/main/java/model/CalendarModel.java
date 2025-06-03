package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the calendar model that stores events and provides methods to add and query events.
 */
public class CalendarModel implements ICalendarModel {

  private List<Event> events;


  /**
   * Constructs a new CalendarModel with an empty list of events.
   */
  public CalendarModel() {
    this.events = new ArrayList<>();
  }

  /**
   * Adds an event to the calendar. If a conflict is found and autoDecline is true, an exception is
   * thrown.
   *
   * @param event       the event to add
   * @param autoDecline if true, conflicts cause the event to be declined
   * @throws EventConflictException if the event conflicts with an existing event
   */
  @Override
  public void addEvent(Event event, boolean autoDecline) throws EventConflictException {
    for (Event existingEvent : events) {
      if (existingEvent.conflictsWith(event)) {
        if (event.isAutoDecline()) {
          throw new EventConflictException("Event '" + event.getSubject() +
              "' conflicts with existing event '" + existingEvent.getSubject() + "'.");
        }

      }
    }
    events.add(event);
  }


  @Override
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
    for (Event occurrence : event.getOccurrences()) {
      if (occurrence.getStartDateTime().toLocalDate().equals(date)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns all events in the calendar.
   *
   * @return a list of all events
   */
  @Override
  public List<Event> getAllEvents() {
    return new ArrayList<>(events);
  }

  @Override
  public boolean isBusyAt(LocalDateTime dateTime) {
    for (Event event : events) {
      for (Event occurrence : event.getOccurrences()) {
        if (occurrence instanceof SingleEvent) {
          SingleEvent se = (SingleEvent) occurrence;
          if (se.getStartDateTime().isBefore(dateTime) && se.getEffectiveEndDateTime()
              .isAfter(dateTime)) {
            return true;
          }
        }
      }
    }
    return false;
  }



}

