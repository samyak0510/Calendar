package controller;

import model.AbstractEvent;
import model.CalendarModel;
import model.Event;
import model.ICalendarService;
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

  private ICalendarService service;
  private EditMode mode;
  private String originalSubject;
  private LocalDateTime start;
  private String property;
  private String newValue;
  private boolean autoDecline;


  public EditEventCommand(ICalendarService service, String meeting, LocalDateTime target,
      String subject, String teamMeeting, EditMode editMode) {
    this(service, meeting, target, subject, teamMeeting, editMode, false);
  }


  public EditEventCommand(ICalendarService service, String originalSubject, LocalDateTime start,
      String property, String newValue, EditMode mode, boolean autoDecline) {
    this.service = service;
    this.originalSubject = originalSubject;
    this.start = start;
    this.property = property;
    this.newValue = newValue;
    this.mode = mode;
    this.autoDecline = autoDecline;
  }

  public EditEventCommand(ICalendarService service, String originalSubject, String property,
      String newValue, EditMode mode) {
    this(service, originalSubject, null, property, newValue, mode, false);
  }


  @Override
  public String execute() throws Exception {
    service.editEvent(originalSubject, start, property, newValue, ICalendarService.EditMode.valueOf(mode.name()));
    return "Edited event(s) '" + originalSubject + "': " + property + " changed to " + newValue;
  }


}
