package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the CalendarModel class.
 */
public class CalendarModelTest {

  private CalendarModel calendar;

  /**
   * Initializes a new CalendarModel instance before each test.
   */
  @Before
  public void setUp() {
    calendar = new CalendarModel();
  }


  @Test
  public void testAddAndGetEvents() throws InvalidDateException, EventConflictException {
    LocalDateTime start1 = LocalDateTime.of(2025, 3, 1, 9, 0);
    LocalDateTime end1 = LocalDateTime.of(2025, 3, 1, 10, 0);
    SingleEvent event1 = new SingleEvent("Meeting", start1, end1, "", "", true);
    calendar.addEvent(event1, false);

    LocalDate eventsDate = LocalDate.of(2025, 3, 1);
    List<Event> eventsOnDate = calendar.getEventsOn(eventsDate);
    assertEquals(1, eventsOnDate.size());
  }


  @Test(expected = EventConflictException.class)
  public void testAutoDeclineConflict() throws InvalidDateException, EventConflictException {
    LocalDateTime start1 = LocalDateTime.of(2025, 3, 1, 9, 0);
    LocalDateTime end1 = LocalDateTime.of(2025, 3, 1, 10, 0);
    SingleEvent event1 = new SingleEvent("Meeting", start1, end1, "", "", true);
    calendar.addEvent(event1, false);

    LocalDateTime start2 = LocalDateTime.of(2025, 3, 1, 9, 30);
    LocalDateTime end2 = LocalDateTime.of(2025, 3, 1, 10, 30);
    SingleEvent event2 = new SingleEvent("Conflict", start2, end2, "", "", true);
    calendar.addEvent(event2, true);
  }


  @Test
  public void testGetAllEvents() throws InvalidDateException, EventConflictException {
    LocalDateTime start1 = LocalDateTime.of(2025, 3, 1, 9, 0);
    LocalDateTime end1 = LocalDateTime.of(2025, 3, 1, 10, 0);
    SingleEvent event1 = new SingleEvent("Meeting", start1, end1, "", "", true);
    calendar.addEvent(event1, false);
    assertFalse(calendar.getAllEvents().isEmpty());
  }


  @Test
  public void testGetEventsOnForRecurringEvent()
      throws InvalidDateException, EventConflictException {
    LocalDateTime start = LocalDateTime.of(2025, 3, 3, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 3, 9, 15);
    Set<DayOfWeek> recurrenceDays = new HashSet<>();
    recurrenceDays.add(DayOfWeek.MONDAY);
    RecurringEvent recurring = new RecurringEvent("Standup", start,
        end, "Daily meeting", "Room101", true, recurrenceDays, 2, null);
    calendar.addEvent(recurring, false);

    LocalDate date = LocalDate.of(2025, 3, 3);
    List<Event> eventsOnDate = calendar.getEventsOn(date);
    assertFalse(eventsOnDate.isEmpty());
    boolean found = eventsOnDate.stream().anyMatch(e -> e.getSubject().equals("Standup"));
    assertTrue(found);
  }
}
