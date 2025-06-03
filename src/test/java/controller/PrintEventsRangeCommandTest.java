package controller;

import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import model.CalendarModel;
import model.CalendarService;
import model.ICalendarService;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit Test Case
 */

public class PrintEventsRangeCommandTest {

  private CalendarModel calendara;
  private ICalendarService calendar;


  @Before
  public void setUp() throws Exception {
    calendara = new CalendarModel();
    calendar = new CalendarService(calendara);
    LocalDateTime start = LocalDateTime.of(2025, 3, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 1, 10, 0);
    calendar.addSingleEvent("Meeting", start, end, "", "", true, false);
  }

  /**
   * Tests that the PrintEventsRangeCommand correctly prints events within the specified range.
   */
  @Test
  public void testPrintEventsRange() throws Exception {
    String startStr = "2025-03-01T00:00";
    String endStr = "2025-03-02T00:00";
    PrintEventsRangeCommand cmd = new PrintEventsRangeCommand(calendar, startStr, endStr);
    String output = cmd.execute();
    assertTrue(output.contains("Meeting"));
  }
}
