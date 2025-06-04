package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Extends calendar service to support multiple calendars and event copying.
 */
public interface IMultiCalendarService extends ICalendarService {

  boolean createCalendar(String name, String timezone) throws Exception;

  boolean editCalendar(String calendarName, String property, String newValue) throws Exception;

  boolean useCalendar(String calendarName) throws Exception;

  String copyEvent(String eventName, LocalDateTime sourceStart,
                   String targetCalendarName, LocalDateTime targetStart) throws Exception;

  String copyEventsOn(LocalDate sourceDate, String targetCalendarName,
                      LocalDate targetDate) throws Exception;

  String copyEventsBetween(LocalDate sourceStartDate, LocalDate sourceEndDate,
                           String targetCalendarName, LocalDate targetStartDate) throws Exception;
}

