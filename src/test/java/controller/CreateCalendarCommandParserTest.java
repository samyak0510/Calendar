package controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import model.CalendarManager;
import model.MultiCalendarService;
import org.junit.Test;

public class CreateCalendarCommandParserTest {

  @Test
  public void testValidParse() {
    CreateCalendarCommandParser parser =
        new CreateCalendarCommandParser(new MultiCalendarService(new CalendarManager()));
    String[] input = {"create", "calendar", "--name", "Work", "--timezone", "Asia/Kolkata"};

    Object result = parser.parse(input);
    assertTrue(result instanceof CreateCalendarCommand);
  }

  @Test
  public void testMissingTimezoneFlagButHasValue() {
    CreateCalendarCommandParser parser =
        new CreateCalendarCommandParser(new MultiCalendarService(new CalendarManager()));
    String[] input = {"create", "calendar", "--name", "MyCal", "Asia/Kolkata"};

    Object result = parser.parse(input);
    assertFalse(result instanceof CreateCalendarCommand);
  }
}

