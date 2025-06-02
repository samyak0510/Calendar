package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import controller.EditEventCommand.EditMode;
import model.CalendarModel;
import model.EventConflictException;
import model.InvalidDateException;
import model.SingleEvent;
import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the EditEventCommand class.
 */
public class EditEventCommandTest {
  private CalendarModel calendar;


  @Before
  public void setUp() throws InvalidDateException, EventConflictException {
    calendar = new CalendarModel();
    LocalDateTime start = LocalDateTime.of(2025, 3, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 1, 10, 0);
    SingleEvent event = new SingleEvent("Meeting", start, end, "", "", true);
    calendar.addEvent(event, false);
  }


  @Test
  public void testEditSingleEvent() throws InvalidDateException {
    LocalDateTime target = LocalDateTime.of(2025, 3, 1, 9, 0);
    EditEventCommand cmd = new EditEventCommand(calendar, "Meeting",
        target, "subject", "TeamMeeting", EditMode.SINGLE);
    String result = cmd.execute();
    assertTrue(result.contains("Edited event(s)"));
    assertEquals("TeamMeeting", calendar.getAllEvents().get(0).getSubject());
  }


  @Test
  public void testEditEventNotFound() throws InvalidDateException {
    LocalDateTime target = LocalDateTime.of(2025, 3, 1, 11, 0);
    EditEventCommand cmd = new EditEventCommand(calendar, "Meeting",
        target, "subject", "TeamMeeting", EditMode.SINGLE);
    String result = cmd.execute();
    assertEquals("No matching event found to edit.", result);
  }


  @Test
  public void testEditAllEventsUsingOverload() throws Exception {
    LocalDateTime start1 = LocalDateTime.of(2025, 3, 1, 9, 0);
    LocalDateTime end1 = LocalDateTime.of(2025, 3, 1, 10, 0);
    SingleEvent event1 = new SingleEvent("Meeting", start1, end1, "", "", true);
    calendar.addEvent(event1, false);

    LocalDateTime start2 = LocalDateTime.of(2025, 3, 2, 14, 0);
    LocalDateTime end2 = LocalDateTime.of(2025, 3, 2, 15, 0);
    SingleEvent event2 = new SingleEvent("Meeting", start2, end2, "", "", true);
    calendar.addEvent(event2, false);

    EditEventCommand editCmd = new EditEventCommand(calendar,
        "Meeting", "subject", "TeamMeeting", EditMode.ALL);
    String result = editCmd.execute();
    assertTrue(result.contains("Edited event(s)"));

    boolean foundEvent1 = calendar.getAllEvents().stream()
            .anyMatch(e -> e.getStartDateTime().equals(start1)
                && e.getSubject().equals("TeamMeeting"));
    boolean foundEvent2 = calendar.getAllEvents().stream()
            .anyMatch(e -> e.getStartDateTime().equals(start2)
                && e.getSubject().equals("TeamMeeting"));
    assertTrue(foundEvent1);
    assertTrue(foundEvent2);
  }

  @Test
  public void testEditLocationForSingleEvent() throws Exception {
    LocalDateTime target = LocalDateTime.of(2025, 3, 1, 9, 0);
    EditEventCommand cmd = new EditEventCommand(calendar, "Meeting",
        target, "location", "Room202", EditMode.SINGLE);
    String result = cmd.execute();
    assertTrue(result.contains("Edited event(s)"));
    assertEquals("Room202", calendar.getAllEvents().get(0).getLocation());
  }


  @Test
  public void testEditEndTimeForSingleEvent() throws Exception {
    LocalDateTime start = LocalDateTime.of(2025, 3, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 1, 10, 0);
    SingleEvent event = new SingleEvent("Meeting", start, end, "", "", true);
    calendar.addEvent(event, false);

    EditEventCommand cmd = new EditEventCommand(calendar, "Meeting",
        start, "end", "2025-03-01T11:00", EditMode.SINGLE);
    String result = cmd.execute();
    assertTrue(result.contains("Edited event(s)"));

    SingleEvent updatedEvent = (SingleEvent) calendar.getAllEvents().get(0);
    assertEquals(LocalDateTime.of(2025, 3, 1, 11, 0), updatedEvent.getEffectiveEndDateTime());
  }

  /**
   * Tests that editing the start time to a value after the current end time fails.
   */
  @Test
  public void testEditStartTimeAfterEndTimeShouldFail() {
    LocalDateTime target = LocalDateTime.of(2025, 3, 1, 9, 0);
    EditEventCommand cmd = new EditEventCommand(calendar,
        "Meeting", target, "start", "2025-03-01T13:00", EditMode.SINGLE);
    try {
      cmd.execute();
    } catch (IllegalArgumentException | InvalidDateException e) {
      assertEquals("Start time cannot be after end time.", e.getMessage());
    }
  }

  /**
   * Tests that editing the end time to a value before the current start time fails.
   */
  @Test
  public void testEditEndTimeBeforeStartTimeShouldFail() {
    LocalDateTime target = LocalDateTime.of(2025, 3, 1, 9, 0);
    EditEventCommand cmd = new EditEventCommand(calendar,
        "Meeting", target, "end", "2025-03-01T08:00", EditMode.SINGLE);
    try {
      cmd.execute();
    } catch (IllegalArgumentException | InvalidDateException e) {
      assertEquals("End time cannot be before start time.", e.getMessage());
    }
  }


  @Test
  public void testEditFromModeNormal() throws Exception {
    LocalDateTime start1 = LocalDateTime.of(2025, 3, 1, 9, 0);
    LocalDateTime end1 = LocalDateTime.of(2025, 3, 1, 10, 0);
    SingleEvent event1 = new SingleEvent("Meeting", start1, end1, "", "", true);
    calendar.addEvent(event1, false);

    LocalDateTime start2 = LocalDateTime.of(2025, 3, 1, 11, 0);
    LocalDateTime end2 = LocalDateTime.of(2025, 3, 1, 12, 0);
    SingleEvent event2 = new SingleEvent("Meeting", start2, end2, "", "", true);
    calendar.addEvent(event2, false);

    LocalDateTime targetTime = LocalDateTime.of(2025, 3, 1, 10, 0);
    EditEventCommand cmd = new EditEventCommand(calendar, "Meeting",
        targetTime, "start", "2025-03-01T10:30", EditMode.FROM);
    String result = cmd.execute();
    assertTrue(result.contains("Edited event(s)"));

    boolean event1Unchanged = calendar.getAllEvents().stream()
            .anyMatch(e -> e.getStartDateTime().equals(start1));
    boolean event2Changed = calendar.getAllEvents().stream()
            .anyMatch(e -> e.getStartDateTime().equals(LocalDateTime.of(2025, 3, 1, 10, 30)));
    assertTrue(event1Unchanged);
    assertTrue(event2Changed);
  }

  @Test
  public void testEditTimeConflictPrevention() throws Exception {
    LocalDateTime startA = LocalDateTime.of(2025, 3, 1, 9, 0);
    LocalDateTime endA = LocalDateTime.of(2025, 3, 1, 10, 0);
    SingleEvent eventA = new SingleEvent("Meeting", startA, endA, "", "", true);
    calendar.addEvent(eventA, false);

    LocalDateTime startB = LocalDateTime.of(2025, 3, 1, 11, 0);
    LocalDateTime endB = LocalDateTime.of(2025, 3, 1, 12, 0);
    SingleEvent eventB = new SingleEvent("Meeting", startB, endB, "", "", true);
    calendar.addEvent(eventB, false);

    EditEventCommand cmd = new EditEventCommand(calendar, "Meeting",
        startB, "start", "2025-03-01T09:30", EditMode.SINGLE, true);
    try {
      cmd.execute();
    } catch (IllegalArgumentException e) {
      assertEquals("Edit would cause a conflict.", e.getMessage());
    }
  }

  @Test
  public void testEditPublicFlagForSingleEvent() throws Exception {
    LocalDateTime start = LocalDateTime.of(2025, 3, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 1, 10, 0);
    SingleEvent event = new SingleEvent("Meeting", start, end, "", "", true);
    calendar.addEvent(event, false);

    EditEventCommand cmd = new EditEventCommand(calendar, "Meeting",
        start, "public", "false", EditMode.SINGLE);
    String result = cmd.execute();
    assertTrue(result.contains("Edited event(s)"));
    boolean isPublic = calendar.getAllEvents().get(0).isPublic();
    assertFalse(isPublic);
  }
}
