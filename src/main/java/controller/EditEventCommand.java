package controller;

import model.AbstractEvent;
import model.CalendarModel;
import model.Event;
import model.InvalidDateException;
import model.SingleEvent;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Edits existing events based on a specified property change.
 */
public class EditEventCommand implements Command {

  /**
   * ENUM for EditMode.
   */
  public enum EditMode { SINGLE, FROM, ALL }

  private CalendarModel calendar;
  private String originalSubject;
  private LocalDateTime start;
  private String property;
  private String newValue;
  private EditMode mode;
  private boolean autoDecline;

  /**
   * Constructs an EditEventCommand for single event mode.
   *
   * @param calendar    the calendar model
   * @param meeting     the subject of the event to edit
   * @param target      the target start time of the event
   * @param subject     the property name to change
   * @param teamMeeting the new value for the property
   * @param editMode    the edit mode to apply
   */
  public EditEventCommand(CalendarModel calendar, String meeting, LocalDateTime target,
      String subject, String teamMeeting, EditMode editMode) {
    this(calendar, meeting, target, subject, teamMeeting, editMode, false);
  }

  /**
   * Constructs an EditEventCommand with full parameters.
   *
   * @param calendar        the calendar model
   * @param originalSubject the subject of the event to edit
   * @param start           the start time used for matching the event
   * @param property        the property to update (e.g., "subject", "description")
   * @param newValue        the new value for the property
   * @param mode            the edit mode (SINGLE, FROM, or ALL)
   * @param autoDecline     flag to automatically decline conflicting updates
   */
  public EditEventCommand(CalendarModel calendar, String originalSubject, LocalDateTime start,
      String property, String newValue, EditMode mode, boolean autoDecline) {
    this.calendar = calendar;
    this.originalSubject = originalSubject;
    this.start = start;
    this.property = property;
    this.newValue = newValue;
    this.mode = mode;
    this.autoDecline = autoDecline;
  }

  /**
   * Constructs an EditEventCommand with default autoDecline set to false.
   *
   * @param calendar        the calendar model
   * @param originalSubject the subject of the event to edit
   * @param property        the property to update
   * @param newValue        the new value for the property
   * @param mode            the edit mode to apply
   */
  public EditEventCommand(CalendarModel calendar, String originalSubject, String property,
      String newValue, EditMode mode) {
    this(calendar, originalSubject, null, property, newValue, mode, false);
  }

  /**
   * Executes the edit command by updating the specified property for matching events.
   *
   * @return a message indicating the result of the edit operation
   * @throws InvalidDateException if a date parsing error occurs during the update
   */
  @Override
  public String execute() throws InvalidDateException {
    List<Event> events = calendar.getAllEvents();
    boolean edited = false;
    for (Event event : events) {
      if (!event.getSubject().equals(originalSubject)) {
        continue;
      }
      if (mode == EditMode.SINGLE) {
        if (event.getStartDateTime().equals(start)) {
          if (event instanceof AbstractEvent) {
            updateEvent((AbstractEvent) event);
            edited = true;
          }
        }
      } else if (mode == EditMode.FROM) {
        if (event.getStartDateTime().isEqual(start) || event.getStartDateTime().isAfter(start)) {
          if (event instanceof AbstractEvent) {
            updateEvent((AbstractEvent) event);
            edited = true;
          }
        }
      } else if (mode == EditMode.ALL) {
        if (event instanceof AbstractEvent) {
          updateEvent((AbstractEvent) event);
          edited = true;
        }
      }
    }
    if (edited) {
      return "Edited event(s) '" + originalSubject + "': " + property + " changed to " + newValue;
    } else {
      return "No matching event found to edit.";
    }
  }

  private void updateEvent(AbstractEvent event) {
    if (property.equalsIgnoreCase("subject")) {
      event.setSubject(newValue);
    } else if (property.equalsIgnoreCase("description")) {
      event.setDescription(newValue);
    } else if (property.equalsIgnoreCase("location")) {
      event.setLocation(newValue);
    } else if (property.equalsIgnoreCase("start") || property.equalsIgnoreCase("startdatetime")) {
      LocalDateTime newStart = LocalDateTime.parse(newValue);
      if (newStart.isAfter(((SingleEvent) event).getEffectiveEndDateTime())) {
        throw new IllegalArgumentException("Start time cannot be after end time.");
      }
      LocalDateTime oldStart = event.getStartDateTime();
      event.setStartDateTime(newStart);
      if (autoDecline && isConflictWithOthers(event)) {
        event.setStartDateTime(oldStart);
        throw new IllegalArgumentException("Edit would cause a conflict.");
      }
    } else if (property.equalsIgnoreCase("end") || property.equalsIgnoreCase("enddatetime")) {
      LocalDateTime newEnd = LocalDateTime.parse(newValue);
      if (newEnd.isBefore(event.getStartDateTime())) {
        throw new IllegalArgumentException("End time cannot be before start time.");
      }
      SingleEvent se = (SingleEvent) event;
      LocalDateTime oldEnd = se.getEffectiveEndDateTime();
      try {
        se.setEndDateTime(newEnd);
      } catch (InvalidDateException e) {
        throw new IllegalArgumentException(e.getMessage());
      }
      if (autoDecline && isConflictWithOthers(event)) {
        try {
          se.setEndDateTime(oldEnd);
        } catch (InvalidDateException e) {
          // This should not occur.
        }
        throw new IllegalArgumentException("Edit would cause a conflict.");
      }
    } else if (property.equalsIgnoreCase("public")) {
      boolean newPublic = Boolean.parseBoolean(newValue);
      event.setPublic(newPublic);
    }
  }

  private boolean isConflictWithOthers(AbstractEvent updatedEvent) {
    for (Event other : calendar.getAllEvents()) {
      if (other == updatedEvent) {
        continue;
      }
      if (updatedEvent.conflictsWith(other) || other.conflictsWith(updatedEvent)) {
        return true;
      }
    }
    return false;
  }
}
