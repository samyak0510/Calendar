package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import model.CalendarModel;
import org.junit.Test;

/**
 * Unit tests for the CommandParser class.
 */
public class CommandParserTest {

  /**
   * Tests that an unknown command returns "Unknown command." when executed.
   *
   * @throws Exception if an error occurs during command execution
   */
  @Test
  public void testUnknownCommand() throws Exception {
    CalendarModel calendar = new CalendarModel();
    CommandParser parser = new CommandParser(calendar);
    Command cmd = parser.parse("eprnig command");
    String result = cmd.execute();
    assertEquals("Unknown command.", result);
  }

  /**
   * Tests that an error during parsing is handled correctly
   * when processing a create command with an invalid date.
   *
   * @throws Exception if an error occurs during command execution
   */
  @Test
  public void testErrorProcessingInParse() throws Exception {
    CalendarModel calendar = new CalendarModel();
    CommandParser parser = new CommandParser(calendar);
    Command cmd = parser.parse("create event Meeting from invalidDate to 2025-03-01T10:00");
    String result = cmd.execute();
    assertTrue(result.startsWith("Error processing create command:"));
  }

  /**
   * Tests that an input containing only whitespace returns null.
   */
  @Test
  public void testEmptyInputReturnsNull() {
    CalendarModel calendar = new CalendarModel();
    CommandParser parser = new CommandParser(calendar);
    Command cmd = parser.parse("    ");
    assertNull("Expected null for empty (whitespace-only) input", cmd);
  }

  /**
   * Tests that a null input returns null.
   */
  @Test
  public void testNullInputReturnsNull() {
    CalendarModel calendar = new CalendarModel();
    CommandParser parser = new CommandParser(calendar);
    Command cmd = parser.parse(null);
    assertNull(cmd);
  }
}
