package controller;

import model.ICalendarService;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Creates a recurring event in the calendar.
 */
public class CreateRecurringEventCommand implements Command {

  private ICalendarService calendarService;
  private boolean autoDecline;
  private String subject;
  private LocalDateTime start;
  private LocalDateTime end;
  private String description;
  private String location;
  private boolean isPublic;
  private Set<DayOfWeek> recurrenceDays;
  private int occurrenceCount = -1;
  private LocalDate recurrenceEndDate;


  public CreateRecurringEventCommand(ICalendarService service, boolean autoDecline, String subject,
      LocalDateTime start, LocalDateTime end, String description,
      String location, boolean isPublic,
      Set<DayOfWeek> recurrenceDays, int occurrenceCount,
      LocalDate recurrenceEndDate) {
    this.calendarService = service;
    this.autoDecline = autoDecline;
    this.subject = subject;
    this.start = start;
    this.end = end;
    this.description = description;
    this.location = location;
    this.isPublic = isPublic;
    this.recurrenceDays = recurrenceDays;
    this.occurrenceCount = occurrenceCount;
    this.recurrenceEndDate = recurrenceEndDate;
  }

  @Override
  public String execute() throws Exception {
    calendarService.addRecurringEvent(subject, start, end, description, location, isPublic,
        recurrenceDays, occurrenceCount, recurrenceEndDate, autoDecline);
    return "Recurring event created: " + subject;
  }
}
