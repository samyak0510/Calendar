package controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import model.CalendarManager;
import model.IMultiCalendarService;
import model.MultiCalendarService;
import org.junit.Test;

/**
 * JUnit Test Case
 */

public class EditCalendarCommandParserTest {

  @Test
  public void testValidParse() {
    EditCalendarCommandParser parser =
        new EditCalendarCommandParser(new MultiCalendarService(new CalendarManager()));
    String[] input = {"edit", "calendar", "--name", "OldName", "--property", "name", "NewName"};

    Object cmd = parser.parse(input);
    assertTrue(cmd instanceof EditCalendarCommand);
  }

  @Test
  public void testInvalidParse() {
    IMultiCalendarService service = new MultiCalendarService(new CalendarManager());
    EditCalendarCommandParser parser = new EditCalendarCommandParser(service);
    String[] input = {"edit", "calendar", "OldCal"};
    Object result = parser.parse(input);
    assertFalse(result instanceof EditCalendarCommand);
  }
}

