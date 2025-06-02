package controller;

import model.CalendarModel;
import model.SingleEvent;
import java.time.LocalDateTime;

/**
 * Creates a single event in the calendar.
 */
public class CreateEventCommand implements Command {
  private CalendarModel calendar;
  private boolean autoDecline;
  private String subject;
  private LocalDateTime start;
  private LocalDateTime end;
  private String description;
  private String location;
  private boolean isPublic;

  /**
   * Constructs a CreateEventCommand with the specified details.
   *
   * @param calendar    the calendar model where the event will be added
   * @param autoDecline flag to automatically decline conflicting events
   * @param subject     the subject of the event
   * @param start       the start date and time of the event
   * @param end         the end date and time of the event
   * @param description the description of the event
   * @param location    the location of the event
   * @param isPublic    whether the event is public or private
   */
  public CreateEventCommand(CalendarModel calendar, boolean autoDecline, String subject,
                            LocalDateTime start, LocalDateTime end, String description,
                            String location, boolean isPublic) {
    this.calendar = calendar;
    this.autoDecline = autoDecline;
    this.subject = subject;
    this.start = start;
    this.end = end;
    this.description = description;
    this.location = location;
    this.isPublic = isPublic;
  }

  /**
   * Executes the event creation by adding a new single event to the calendar.
   *
   * @return a message indicating the event was created
   * @throws Exception if an error occurs during event creation
   */
  @Override
  public String execute() throws Exception {
    SingleEvent event = new SingleEvent(subject, start, end, description, location, isPublic);
    calendar.addEvent(event, autoDecline);
    return "Event created: " + subject;
  }
}
