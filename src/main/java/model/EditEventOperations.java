package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditEventOperations implements IEditEventOperations {

  private Map<RecurringEvent, Map<LocalDateTime, SingleEvent>> recurringOverrides = new HashMap<>();

  @Override
  public void editEvent(ICalendarModel model,
      String subject,
      LocalDateTime from,
      String property,
      String newValue,
      ICalendarService.EditMode mode) throws Exception {

    boolean edited = false;
    List<Event> events = model.getAllEvents();
    List<Event> newRecurringEvents = new ArrayList<>();

    for (Event event : events) {
      if (!event.getSubject().equalsIgnoreCase(subject)) {
        continue;
      }

      if (!(event instanceof RecurringEvent)) {
        // Non-recurring event => only SINGLE mode.
        if (mode != ICalendarService.EditMode.SINGLE) {
          throw new UnsupportedOperationException(
              "For non-recurring events, only SINGLE mode is allowed.");
        }
        if (event.getStartDateTime().equals(from)) {
          updateEvent((AbstractEvent) event, property, newValue);
          edited = true;
        }
        continue;
      }

      // Recurring event
      RecurringEvent re = (RecurringEvent) event;
      switch (mode) {
        case ALL:
          updateEvent(re, property, newValue);
          edited = true;
          break;

        case SINGLE:
          // find the single occurrence at time == from
          List<SingleEvent> occs = re.generateOccurrences();
          boolean found = false;
          for (SingleEvent occ : occs) {
            if (occ.getStartDateTime().equals(from)) {
              SingleEvent overrideOcc = createOverride(occ, property, newValue);
              recurringOverrides
                  .computeIfAbsent(re, k -> new HashMap<>())
                  .put(occ.getStartDateTime(), overrideOcc);
              found = true;
              edited = true;
              break;
            }
          }
          if (!found) {
            throw new Exception("No occurrence found at the specified time for recurring event.");
          }
          break;

        case FROM:
          // We want to update all occurrences that start on/after 'from'.
          // 1) Generate occurrences, collect only the future occurrences on/after 'from'.
          List<SingleEvent> allOccs = re.generateOccurrences();
          List<SingleEvent> futureOccs = new ArrayList<>();
          for (SingleEvent occ : allOccs) {
            if (!occ.getStartDateTime().isBefore(from)) {
              futureOccs.add(occ);
            }
          }
          if (futureOccs.isEmpty()) {
            // Means there are no occurrences on/after 'from'; effectively nothing to update.
            break;
          }
          // 2) The "old" event covers all occurrences before 'from';
          //    so set its recurrenceEndDate to the day before the earliest future occurrence.
          LocalDate earliestFutureDay = futureOccs.get(0).getStartDateTime().toLocalDate();
          LocalDate dayBefore = earliestFutureDay.minusDays(1);
          re.setRecurrenceEndDate(dayBefore);

          // 3) Create a new recurring event for all future occurrences with the updated property.
          RecurringEvent newRe = createSplitRecurringEvent(re, futureOccs, property, newValue);
          newRecurringEvents.add(newRe);

          edited = true;
          break;
      }
    }

    // Add any newly created recurring events from FROM splitting
    for (Event newEvent : newRecurringEvents) {
      model.addEvent(newEvent,
          newEvent instanceof AbstractEvent && newEvent.isAutoDecline());
    }

    if (!edited) {
      throw new Exception("No matching event found to edit.");
    }
  }

  private void updateEvent(AbstractEvent event, String property, String newValue)
      throws Exception {
    if (property.equalsIgnoreCase("subject")) {
      event.setSubject(newValue);
    } else if (property.equalsIgnoreCase("description")) {
      event.setDescription(newValue);
    } else if (property.equalsIgnoreCase("location")) {
      event.setLocation(newValue);
    } else if (property.equalsIgnoreCase("public")) {
      event.setPublic(Boolean.parseBoolean(newValue));
    } else if (property.equalsIgnoreCase("autodecline")) {
      event.setAutoDecline(Boolean.parseBoolean(newValue));
    } else if (property.equalsIgnoreCase("start") || property.equalsIgnoreCase("startdatetime")) {
      LocalDateTime newStart = LocalDateTime.parse(newValue);
      if (event instanceof SingleEvent) {
        SingleEvent se = (SingleEvent) event;
        if (newStart.isAfter(se.getEffectiveEndDateTime())) {
          throw new IllegalArgumentException("Start time cannot be after end time.");
        }
        LocalDateTime oldStart = event.getStartDateTime();
        event.setStartDateTime(newStart);
        if (event.isAutoDecline() && isConflictWithOthers(event)) {
          event.setStartDateTime(oldStart);
          throw new IllegalArgumentException("Edit would cause a conflict.");
        }
      } else {
        throw new UnsupportedOperationException(
            "Editing start time for recurring events in ALL mode not allowed; use FROM mode.");
      }
    } else if (property.equalsIgnoreCase("end") || property.equalsIgnoreCase("enddatetime")) {
      LocalDateTime newEnd = LocalDateTime.parse(newValue);
      if (newEnd.isBefore(event.getStartDateTime())) {
        throw new IllegalArgumentException("End time cannot be before start time.");
      }
      if (event instanceof SingleEvent) {
        SingleEvent se = (SingleEvent) event;
        LocalDateTime oldEnd = se.getEffectiveEndDateTime();
        se.setEndDateTime(newEnd);
        if (event.isAutoDecline() && isConflictWithOthers(event)) {
          se.setEndDateTime(oldEnd);
          throw new IllegalArgumentException("Edit would cause a conflict.");
        }
      } else {
        throw new UnsupportedOperationException(
            "Editing end time for recurring events in ALL mode not allowed; use FROM mode.");
      }
    } else {
      throw new UnsupportedOperationException("Editing property not supported: " + property);
    }
  }


  private boolean isConflictWithOthers(AbstractEvent updatedEvent) {
    for (Event other : updatedEvent.getOccurrences()) {
      if (other == updatedEvent) {
        continue;
      }
      if (updatedEvent.conflictsWith(other) || other.conflictsWith(updatedEvent)) {
        return true;
      }
    }
    return false;
  }


  private RecurringEvent createSplitRecurringEvent(
      RecurringEvent oldRe,
      List<SingleEvent> futureOccs,
      String property,
      String newValue
  ) throws Exception {

    // earliest future occurrence
    LocalDateTime newStart = futureOccs.get(0).getStartDateTime();
    // We'll keep the same daily end-time offset
    LocalDateTime newEnd = LocalDateTime.of(newStart.toLocalDate(),
        oldRe.getEndDateTime().toLocalTime());
    // We keep same recurrenceDays from the old event, etc.
    // We set the recurrenceEndDate to the last day in futureOccs
    LocalDate lastDay = futureOccs.get(futureOccs.size() - 1).getStartDateTime().toLocalDate();

    // Build new recurring event
    RecurringEvent newRe = new RecurringEvent(oldRe.getSubject(),
        newStart, newEnd,
        oldRe.getDescription(),
        oldRe.getLocation(),
        oldRe.isPublic(),
        oldRe.getRecurrenceDays(),
        oldRe.getOccurrenceCount(),
        lastDay // until lastDay
    );

    // Copy autoDecline
    newRe.setAutoDecline(oldRe.isAutoDecline());

    // Now update the new event with the property
    updateEvent(newRe, property, newValue);
    return newRe;
  }

  // For recurring events in SINGLE mode: create an override SingleEvent.
  private SingleEvent createOverride(SingleEvent occ, String property, String newValue)
      throws Exception {
    SingleEvent overrideOcc = new SingleEvent(occ.getSubject(), occ.getStartDateTime(),
        occ.getEffectiveEndDateTime(), occ.getDescription(), occ.getLocation(), occ.isPublic());
    overrideOcc.setAutoDecline(occ.isAutoDecline());
    if (property.equalsIgnoreCase("subject")) {
      overrideOcc.setSubject(newValue);
    } else if (property.equalsIgnoreCase("description")) {
      overrideOcc.setDescription(newValue);
    } else if (property.equalsIgnoreCase("location")) {
      overrideOcc.setLocation(newValue);
    } else if (property.equalsIgnoreCase("public")) {
      overrideOcc.setPublic(Boolean.parseBoolean(newValue));
    } else if (property.equalsIgnoreCase("autodecline")) {
      overrideOcc.setAutoDecline(Boolean.parseBoolean(newValue));
    } else if (property.equalsIgnoreCase("start") || property.equalsIgnoreCase("startdatetime")) {
      LocalDateTime newStart = LocalDateTime.parse(newValue);
      if (newStart.isAfter(occ.getEffectiveEndDateTime())) {
        throw new IllegalArgumentException("Start time cannot be after end time.");
      }
      overrideOcc.setStartDateTime(newStart);
    } else if (property.equalsIgnoreCase("end") || property.equalsIgnoreCase("enddatetime")) {
      LocalDateTime newEnd = LocalDateTime.parse(newValue);
      if (newEnd.isBefore(occ.getStartDateTime())) {
        throw new IllegalArgumentException("End time cannot be before start time.");
      }
      overrideOcc.setEndDateTime(newEnd);
    } else {
      throw new UnsupportedOperationException("Editing property not supported: " + property);
    }
    return overrideOcc;
  }
}
