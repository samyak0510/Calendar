package controller;

import static org.junit.Assert.assertTrue;

import model.CalendarModel;
import model.EventConflictException;
import model.InvalidDateException;
import model.SingleEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the PrintEventsCommand class.
 */
public class PrintEventsCommandTest {
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
   * Tests that the PrintEventsCommand correctly prints events for a given date.
   */
  @Test
  public void testPrintEvents() {
    LocalDate date = LocalDate.of(2025, 3, 1);
    PrintEventsCommand cmd = new PrintEventsCommand(calendar, date);
    String output = cmd.execute();
    assertTrue(output.contains("Meeting"));
    assertTrue(output.contains("2025-03-01T09:00"));
  }
}
