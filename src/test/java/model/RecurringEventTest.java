package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;

/**
 * Unit tests for the RecurringEvent class.
 */
public class RecurringEventTest {


  @Test
  public void testGenerateOccurrencesFixedCount() throws InvalidDateException {
    LocalDateTime start = LocalDateTime.of(2025, 3, 3, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 3, 9, 15);
    Set<DayOfWeek> days = new HashSet<>();
    days.add(DayOfWeek.MONDAY);
    RecurringEvent re = new RecurringEvent("Standup", start, end,
        "", "", true, days, 3, null);
    List<SingleEvent> occurrences = re.generateOccurrences();
    assertEquals(3, occurrences.size());
    for (int i = 0; i < 3; i++) {
      assertEquals(DayOfWeek.MONDAY, occurrences.get(i).getStartDateTime().getDayOfWeek());
    }
  }


  @Test
  public void testGenerateOccurrencesUntilDate() throws InvalidDateException {
    LocalDateTime start = LocalDateTime.of(2025, 3, 3, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 3, 9, 15);
    Set<DayOfWeek> days = new HashSet<>();
    days.add(DayOfWeek.MONDAY);
    LocalDate until = LocalDate.of(2025, 3, 17);
    RecurringEvent re = new RecurringEvent("WeeklyMeeting", start, end,
        "", "", true, days, -1, until);
    List<SingleEvent> occurrences = re.generateOccurrences();
    assertEquals(3, occurrences.size());
  }


  @Test(expected = InvalidDateException.class)
  public void testInvalidRecurringEventDifferentDay() throws InvalidDateException {
    LocalDateTime start = LocalDateTime.of(2025, 3, 3, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 4, 9, 15);
    Set<DayOfWeek> days = new HashSet<>();
    days.add(DayOfWeek.MONDAY);
    new RecurringEvent("Invalid", start, end, "", "", true,
        days, 3, null);
  }


  @Test
  public void testRecurringEventConflict() throws InvalidDateException {
    LocalDateTime startRecurring = LocalDateTime.of(2025, 3, 3, 9, 0);
    LocalDateTime endRecurring = LocalDateTime.of(2025, 3, 3, 9, 15);
    Set<DayOfWeek> days = new HashSet<>();
    days.add(DayOfWeek.MONDAY);
    RecurringEvent recurring = new RecurringEvent("Standup", startRecurring,
        endRecurring, "", "", true, days, 3, null);

    LocalDateTime startSingle = LocalDateTime.of(2025, 3, 3, 9, 5);
    LocalDateTime endSingle = LocalDateTime.of(2025, 3, 3, 9, 10);
    SingleEvent single = new SingleEvent("Overlap",
        startSingle, endSingle, "", "", true);

    assertTrue(recurring.conflictsWith(single));
  }


  @Test
  public void testConflictsWithNoConflict() throws InvalidDateException {
    LocalDateTime startRecurring = LocalDateTime.of(2025, 3, 3, 9, 0);
    LocalDateTime endRecurring = LocalDateTime.of(2025, 3, 3, 9, 15);
    Set<DayOfWeek> days = new HashSet<>();
    days.add(DayOfWeek.MONDAY);
    RecurringEvent recurring = new RecurringEvent("Standup", startRecurring,
        endRecurring, "Daily meeting", "Room1", true,
        days, 2, null);

    LocalDateTime singleStart = LocalDateTime.of(2025, 3, 3, 10, 0);
    LocalDateTime singleEnd = LocalDateTime.of(2025, 3, 3, 10, 30);
    try {
      SingleEvent single = new SingleEvent("OtherEvent", singleStart,
          singleEnd, "", "", true);
      assertFalse(recurring.conflictsWith(single));
    } catch (InvalidDateException e) {
      fail("No exception expected: " + e.getMessage());
    }
  }


  @Test
  public void testGetOccurrencesReturnsExpectedList() throws InvalidDateException {
    LocalDateTime start = LocalDateTime.of(2025, 3, 3, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 3, 9, 15);
    Set<DayOfWeek> days = new HashSet<>();
    days.add(DayOfWeek.MONDAY);
    RecurringEvent recurring = new RecurringEvent("Standup", start, end,
        "Daily meeting", "Room1", true, days, 3, null);
    List<?> occs = recurring.getOccurrences();
    assertNotNull(occs);
    assertEquals(3, occs.size());
  }


  @Test
  public void testGenerateOccurrencesWithRecurrenceEndDate() throws InvalidDateException {
    LocalDateTime start = LocalDateTime.of(2025, 3, 3, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 3, 9, 15);
    Set<DayOfWeek> days = new HashSet<>();
    days.add(DayOfWeek.MONDAY);
    LocalDate recurrenceEndDate = LocalDate.of(2025, 3, 10);
    RecurringEvent recurring = new RecurringEvent("Standup", start, end,
        "Daily meeting", "Room1", true, days, -1, recurrenceEndDate);
    List<SingleEvent> occurrences = recurring.generateOccurrences();
    assertEquals("Expected 2 occurrences", 2, occurrences.size());
    assertEquals(recurrenceEndDate, occurrences.get(1).getStartDateTime().toLocalDate());
  }
}
