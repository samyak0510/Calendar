package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ICalendarModel {


  void addEvent(Event event, boolean autoDecline) throws EventConflictException;

  List<Event> getEventsOn(LocalDate date);

  List<Event> getAllEvents();

  boolean isBusyAt(LocalDateTime dateTime);


}
