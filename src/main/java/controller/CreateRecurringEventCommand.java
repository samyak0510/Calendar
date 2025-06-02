package controller;

import model.CalendarModel;
import model.EventConflictException;
import model.InvalidDateException;
import model.RecurringEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Creates a recurring event in the calendar.
 */
public class CreateRecurringEventCommand implements Command {

  private CalendarModel calendar;
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

  /**
   * Constructs a CreateRecurringEventCommand with the given parameters.
   *
   * @param calendar          the calendar model to be used
   * @param autoDecline       flag to automatically decline conflicting events
   * @param subject           the subject of the recurring event
   * @param start             the start date and time of the event series
   * @param end               the end date and time of the event series
   * @param description       the event description
   * @param location          the event location
   * @param isPublic          whether the event is public
   * @param recurrenceDays    the days on which the event recurs
   * @param occurrenceCount   number of occurrences (-1 if not specified)
   * @param recurrenceEndDate the end date of the recurrence series
   */
  public CreateRecurringEventCommand(CalendarModel calendar, boolean autoDecline, String subject,
                                     LocalDateTime start, LocalDateTime end, String description,
                                     String location, boolean isPublic,
                                     Set<DayOfWeek> recurrenceDays, int occurrenceCount,
                                     LocalDate recurrenceEndDate) {
    this.calendar = calendar;
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

  /**
   * Executes the recurring event creation and adds it to the calendar.
   *
   * @return a confirmation message for the created recurring event
   * @throws InvalidDateException   if the provided dates are invalid
   * @throws EventConflictException if there is a conflict with an existing event
   */
  @Override
  public String execute() throws InvalidDateException, EventConflictException {
    RecurringEvent event = new RecurringEvent(subject, start, end, description, location, isPublic,
            recurrenceDays, occurrenceCount, recurrenceEndDate);
    calendar.addEvent(event, autoDecline);
    return "Recurring event created: " + subject;
  }
}
