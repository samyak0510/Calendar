package controller;

import static org.junit.Assert.assertEquals;

import model.CalendarModel;
import model.InvalidDateException;
import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the CreateEventCommand class.
 */
public class CreateEventCommandTest {
  private CalendarModel calendar;

  /**
   * Sets up the test fixture before each test.
   */
  @Before
  public void setUp() {
    calendar = new CalendarModel();
  }


  @Test
  public void testExecuteCreateEvent() throws Exception {
    LocalDateTime start = LocalDateTime.of(2025, 3, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 1, 10, 0);
    CreateEventCommand cmd = new CreateEventCommand(calendar, false,
        "Meeting", start, end, "desc", "room", true);
    String result = cmd.execute();
    assertEquals("Event created: Meeting", result);
    assertEquals(1, calendar.getAllEvents().size());
  }


  @Test(expected = InvalidDateException.class)
  public void testCreateEventInvalidDate() throws Exception {
    LocalDateTime start = LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 1, 9, 0);
    new CreateEventCommand(calendar, false, "Meeting", start, end, "", "", true).execute();
  }
}
