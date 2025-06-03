package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface IEventPrinter {
  String printEventsOn(LocalDate date) throws Exception;
  String printEventsRange(LocalDateTime start, LocalDateTime end) throws Exception;

}
