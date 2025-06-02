package controller;

import static org.junit.Assert.assertEquals;

import model.CalendarModel;
import model.EventConflictException;
import model.InvalidDateException;
import model.SingleEvent;
import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the ShowStatusCommand class.
 */
public class ShowStatusCommandTest {
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
   * Tests that the status is "Busy" when the check time falls within an event.
   */
  @Test
  public void testShowStatusBusy() {
    LocalDateTime checkTime = LocalDateTime.of(2025, 3, 1, 9, 30);
    ShowStatusCommand cmd = new ShowStatusCommand(calendar, checkTime);
    String result = cmd.execute();
    assertEquals("Busy", result);
  }

  /**
   * Tests that the status is "Available" when the check time does not fall within any event.
   */
  @Test
  public void testShowStatusAvailable() {
    LocalDateTime checkTime = LocalDateTime.of(2025, 3, 1, 10, 30);
    ShowStatusCommand cmd = new ShowStatusCommand(calendar, checkTime);
    String result = cmd.execute();
    assertEquals("Available", result);
  }
}
