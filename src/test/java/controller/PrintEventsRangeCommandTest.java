package controller;

import static org.junit.Assert.assertTrue;

import model.CalendarModel;
import model.EventConflictException;
import model.InvalidDateException;
import model.SingleEvent;
import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the PrintEventsRangeCommand class.
 */
public class PrintEventsRangeCommandTest {

  private CalendarModel calendar;


  @Before
  public void setUp() throws InvalidDateException, EventConflictException {
    calendar = new CalendarModel();
    LocalDateTime start = LocalDateTime.of(2025, 3, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 1, 10, 0);
    SingleEvent event = new SingleEvent("Meeting", start, end, "", "", true);
    calendar.addEvent(event, false);
  }

  /**
   * Tests that the PrintEventsRangeCommand correctly prints events within the specified range.
   */
  @Test
  public void testPrintEventsRange() {
    String startStr = "2025-03-01T00:00";
    String endStr = "2025-03-02T00:00";
    PrintEventsRangeCommand cmd = new PrintEventsRangeCommand(calendar, startStr, endStr);
    String output = cmd.execute();
    assertTrue(output.contains("Meeting"));
  }
}
