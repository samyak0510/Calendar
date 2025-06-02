package controller;

import static org.junit.Assert.assertEquals;

import model.InvalidDateException;
import java.time.LocalDateTime;
import org.junit.Test;

/**
 * Unit tests for the CommandParserStatic class.
 */
public class CommandParserStaticTest {

  /**
   * Tests that parseDateTimeStatic correctly parses a valid date-time string.
   *
   * @throws InvalidDateException if the input string is invalid
   */
  @Test
  public void testParseDateTimeStatic() throws InvalidDateException {
    String input = "2025-03-01T09:00";
    LocalDateTime dateTime = CommandParserStatic.parseDateTimeStatic(input);
    assertEquals(2025, dateTime.getYear());
    assertEquals(3, dateTime.getMonthValue());
    assertEquals(1, dateTime.getDayOfMonth());
    assertEquals(9, dateTime.getHour());
    assertEquals(0, dateTime.getMinute());
  }
}
