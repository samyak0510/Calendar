package view;

import controller.ExtendedCalendarController;
import controller.ICalendarController;
import model.CalendarManager;
import model.ICalendarManager;
import model.IMultiCalendarService;
import model.MultiCalendarService;

/**
 * The main application class for the Calendar App. Supports both interactive and headless execution
 * modes.
 */
public class CalendarApp {

  /**
   * Entry point for the Calendar application.
   *
   * @param args command line arguments: --mode [interactive|headless] [optional command file]
   */
  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("Usage: java View.CalendarApp --mode interactive|headless");
      return;
    }

    ICalendarManager calendarManager = new CalendarManager();
    IMultiCalendarService service = new MultiCalendarService(calendarManager);
    ICalendarController controller = new ExtendedCalendarController(service);
    CalendarView calendarView = new CalendarView(controller, args);

    calendarView.start();
  }
}

