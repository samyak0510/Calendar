package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import model.CalendarModel;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the CalendarController class.
 */
public class CalendarControllerTest {

  private CalendarController controller;

  /**
   * Sets up the test fixture before each test.
   */
  @Before
  public void setUp() {
    CalendarModel calendar = new CalendarModel();
    controller = new CalendarController(calendar);
  }

  /**
   * Tests that a valid create command returns the expected creation message.
   */
  @Test
  public void testProcessCreateCommand()  {
    String command = "create event --autoDecline Meeting from 2025-03-01T09:00 to 2025-03-01T10:00";
    String result = controller.processCommand(command);
    assertEquals("Event created: Meeting", result);
  }

  /**
   * Tests that printing events on a given date returns an output containing the event's subject.
   */
  @Test
  public void testProcessPrintCommand()  {
    String createCommand = "create event Meeting from 2025-03-01T09:00 to 2025-03-01T10:00";
    controller.processCommand(createCommand);
    String printCommand = "print events on 2025-03-01";
    String result = controller.processCommand(printCommand);
    assertTrue(result.contains("Meeting"));
  }

  /**
   * Tests that an invalid command returns an appropriate error message.
   */
  @Test
  public void testProcessInvalidCommand()  {
    String command = "invalid command";
    String result = controller.processCommand(command);
    assertEquals("Unknown command.", result);
  }

  /**
   * Tests that the exit command returns the string "exit".
   */
  @Test
  public void testProcessExitCommand()  {
    String result = controller.processCommand("exit");
    assertEquals("exit", result);
  }

  /**
   * Tests that a command causing an event
   * conflict returns an error message when autoDecline is enabled.
   */
  @Test
  public void testProcessConflictCommand()  {
    String createCmd1 = "create event Meeting from 2025-03-01T09:00 to 2025-03-01T10:00";
    String result1 = controller.processCommand(createCmd1);
    assertEquals("Event created: Meeting", result1);

    String createCmd2 = "create event --autoDecline ConflictMeeting "
        + "from 2025-03-01T09:30 to 2025-03-01T10:30";
    String result2 = controller.processCommand(createCmd2);
    assertTrue(result2.startsWith("Error processing command:"));
  }

  /**
   * Tests that processing an empty command returns an empty string.
   */
  @Test
  public void testProcessCommandReturnsEmptyForNullCommand() {
    String result = controller.processCommand("");
    assertEquals("", result);
  }

  /**
   * Tests that printing events within a valid date-time
   * range returns an output containing the event's subject.
   */
  @Test
  public void testProcessPrintRangeCommand()  {
    String createCommand = "create event Meeting from 2025-03-01T09:00 to 2025-03-01T10:00";
    String createResult = controller.processCommand(createCommand);
    assertEquals("Event created: Meeting", createResult);

    String printRangeCommand = "print events from 2025-03-01T00:00 to 2025-03-02T00:00";
    String result = controller.processCommand(printRangeCommand);
    assertTrue(result.contains("Meeting"));
  }

  /**
   * Tests that an invalid date format in a print range command returns an error message.
   */
  @Test
  public void testProcessPrintRangeCommandError()  {
    String printRangeCommand = "print events from invalidDate to 2025-03-02T00:00";
    String result = controller.processCommand(printRangeCommand);
    assertTrue(result.startsWith("Error processing print command:"));
  }
}
