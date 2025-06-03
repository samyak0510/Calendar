package controller;

import java.time.LocalDate;
import model.ICalendarService;

/**
 * Command to print events for a specific date.
 */
public class PrintEventsCommand implements Command {

  private ICalendarService calendarService;
  private LocalDate date;


  public PrintEventsCommand(ICalendarService service, LocalDate date) {
    this.calendarService = service;
    this.date = date;
  }

  /**
   * Executes the command by retrieving and formatting events for the specified date.
   *
   * @return a formatted string of events on the given date
   */
  @Override
  public String execute() throws Exception {

    return calendarService.printEventsOn(date);
  }
}
