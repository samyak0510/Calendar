package controller;

import model.CalendarModel;
import model.ICalendarService;
import model.SingleEvent;
import java.time.LocalDateTime;

/**
 * Creates a single event in the calendar.
 */
public class CreateEventCommand implements Command {

  private ICalendarService service;
  private boolean autoDecline;
  private String subject;
  private LocalDateTime start;
  private LocalDateTime end;
  private String description;
  private String location;
  private boolean isPublic;


  public CreateEventCommand(ICalendarService service, boolean autoDecline, String subject,
      LocalDateTime start, LocalDateTime end, String description,
      String location, boolean isPublic) {
    this.service = service;
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
    service.addSingleEvent(subject, start, end, description, location, isPublic,autoDecline);
    return "Event created: " + subject;
  }
}
