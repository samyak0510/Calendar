package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import model.CalendarModel;
import model.CalendarService;
import model.ICalendarService;
import org.junit.Before;
import org.junit.Test;

public class CalendarControllerTest {

  private CalendarController controller;

  @Before
  public void setUp() {
    CalendarModel model = new CalendarModel();
    ICalendarService service = new CalendarService(model);
    controller = new CalendarController(service);
  }

  @Test
  public void testProcessCreateCommand() {
    String command = "create event --autoDecline Meeting from 2025-03-01T09:00 to 2025-03-01T10:00";
    String result = controller.processCommand(command);
    assertEquals("Event created: Meeting", result);
  }

  @Test
  public void testProcessPrintCommand() {
    controller.processCommand("create event Meeting from 2025-03-01T09:00 to 2025-03-01T10:00");
    String result = controller.processCommand("print events on 2025-03-01");
    assertTrue(result.contains("Meeting"));
  }

  @Test
  public void testProcessInvalidCommand() {
    String result = controller.processCommand("invalid command");
    assertEquals("Unknown command.", result);
  }

  @Test
  public void testProcessExitCommand() {
    String result = controller.processCommand("exit");
    assertEquals("exit", result);
  }

  @Test
  public void testProcessConflictCommand() {
    String result1 = controller.processCommand(
        "create event Meeting from 2025-03-01T09:00 to 2025-03-01T10:00");
    assertEquals("Event created: Meeting", result1);
    String result2 = controller.processCommand(
        "create event --autoDecline ConflictMeeting from 2025-03-01T09:30 to 2025-03-01T10:30");
    assertTrue(result2.startsWith("Error processing command:"));
  }

  @Test
  public void testProcessCommandReturnsEmptyForNullCommand() {
    String result = controller.processCommand("");
    assertEquals("", result);
  }

  @Test
  public void testProcessPrintRangeCommand() {
    String createResult = controller.processCommand(
        "create event Meeting from 2025-03-01T09:00 to 2025-03-01T10:00");
    assertEquals("Event created: Meeting", createResult);
    String result = controller.processCommand(
        "print events from 2025-03-01T00:00 to 2025-03-02T00:00");
    assertTrue(result.contains("Meeting"));
  }

  @Test
  public void testProcessPrintRangeCommandError() {
    String result = controller.processCommand("print events from invalidDate to 2025-03-02T00:00");
    assertTrue(result.startsWith("Error processing print command:"));
  }

}