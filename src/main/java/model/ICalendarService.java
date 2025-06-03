package model;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ICalendarService {

  enum EditMode {SINGLE, FROM, ALL}


  void addSingleEvent(String subject, LocalDateTime start, LocalDateTime end,
      String description, String location, boolean isPublic, boolean autoDecline)
      throws Exception;

  void addRecurringEvent(String subject, LocalDateTime start, LocalDateTime end,
      String description, String location, boolean isPublic,
      java.util.Set<java.time.DayOfWeek> recurrenceDays,
      int occurrenceCount, java.time.LocalDate recurrenceEndDate, boolean autoDecline)
      throws Exception;

  List<Event> getEventsOn(LocalDate date);

  List<Event> getAllEvents();

  boolean isBusyAt(LocalDateTime dateTime);

  void editEvent(String subject, LocalDateTime from, String property, String newValue,
      EditMode mode) throws Exception;

  String exporttoCSV(String format, String path) throws Exception;

  String printEventsOn(LocalDate date) throws Exception;

  String printEventsRange(LocalDateTime start, LocalDateTime end) throws Exception;

}
