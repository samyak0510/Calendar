package controller;

import model.ICalendarService;

import java.time.LocalDateTime;

/**
 * Command to print events within a specified date-time range.
 */
public class PrintEventsRangeCommand implements Command {

  private ICalendarService calendarService;
  private LocalDateTime start;
  private LocalDateTime end;


  public PrintEventsRangeCommand(ICalendarService calendarService, String startDTStr, String endDTStr) {
    try {
      this.calendarService = calendarService;
      this.start = CommandParserStatic.parseDateTimeStatic(startDTStr);
      this.end = CommandParserStatic.parseDateTimeStatic(endDTStr);
    } catch (model.InvalidDateException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Executes the command by retrieving events within the specified range and formatting them.
   *
   * @return a formatted string of events within the given range
   */
  @Override
  public String execute() throws Exception {

    return calendarService.printEventsRange(start,end);
  }
}
