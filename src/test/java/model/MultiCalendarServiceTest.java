package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit Test Case
 */
public class MultiCalendarServiceTest {

  private ICalendarManager manager;
  private IMultiCalendarService multiService;

  @Before
  public void setUp() throws Exception {
    manager = new CalendarManager();
    multiService = new MultiCalendarService(manager);
  }

  @Test
  public void testCreateCalendarSuccess() throws Exception {
    boolean created = multiService.createCalendar("Work",
        "America/New_York");
    assertTrue("Calendar should be created successfully", created);
  }

  @Test
  public void testCreateCalendarDuplicate() throws Exception {
    multiService.createCalendar("Personal", "America/Los_Angeles");
    boolean duplicateCreated = multiService.createCalendar("Personal",
        "America/Los_Angeles");
    assertFalse("Creating a duplicate calendar should fail", duplicateCreated);
  }

  @Test
  public void testUseCalendar() throws Exception {
    multiService.createCalendar("Team", "Europe/London");
    boolean used = multiService.useCalendar("Team");
    assertTrue("Using an existing calendar should succeed", used);
  }

  @Test
  public void testEditCalendarName() throws Exception {
    multiService.createCalendar("OldName", "America/New_York");
    boolean edited = multiService.editCalendar("OldName", "name",
        "NewName");
    assertTrue("Editing calendar name should succeed", edited);
    multiService.createCalendar("Duplicate", "America/New_York");
    boolean duplicateEdit = multiService.editCalendar("NewName", "name",
        "Duplicate");
    assertFalse("Editing calendar name to an existing name should fail", duplicateEdit);
  }

  @Test
  public void testCreateCalendarActuallyAddsToManager() throws Exception {
    boolean result = multiService.createCalendar("TestCal", "Europe/Paris");
    assertTrue(result);

    assertNotNull(manager.getCalendar("TestCal"));
  }

  @Test
  public void testEditCalendarNameRemovesOldCalendar() throws Exception {
    multiService.createCalendar("Original", "UTC");
    multiService.editCalendar("Original", "name", "Renamed");

    assertNull(manager.getCalendar("Original"));
    assertNotNull(manager.getCalendar("Renamed"));
  }

  @Test
  public void testEditCalendarTimezoneMigratesEvents() throws Exception {

    multiService.createCalendar("TimezoneTest", "America/New_York");
    multiService.useCalendar("TimezoneTest");
    multiService.addSingleEvent("Meeting",
        LocalDateTime.of(2023, 1, 1, 9, 0),
        LocalDateTime.of(2023, 1, 1, 10, 0),
        "", "", true, true);

    boolean result = multiService.editCalendar("TimezoneTest", "timezone", "Europe/London");
    assertTrue(result);

    List<Event> events = manager.getCalendar("TimezoneTest").getCalendarService().getAllEvents();
    assertEquals(LocalDateTime.of(2023, 1, 1, 14, 0), events.get(0).getStartDateTime());
  }


  @Test
  public void testAddSingleEventActuallyCreatesEvent() throws Exception {
    multiService.createCalendar("EventTest", "UTC");
    multiService.useCalendar("EventTest");
    multiService.addSingleEvent("TestEvent",
        LocalDateTime.now(),
        LocalDateTime.now().plusHours(1),
        "", "", true, true);

    assertEquals(1, multiService.getAllEvents().size());
  }

  @Test
  public void testGetEventsOnEmptyCalendar() {
    assertNull(multiService.getEventsOn(LocalDate.now()));
  }

  @Test
  public void testGetAllEventsEmptyCalendar() {
    assertNull(multiService.getAllEvents());
  }

  @Test
  public void testIsBusyAtWithConflictingEvent() throws Exception {
    multiService.createCalendar("BusyTest", "UTC");
    multiService.useCalendar("BusyTest");
    LocalDateTime testTime = LocalDateTime.of(2023, 1, 1, 9, 30);
    multiService.addSingleEvent("Conflict",
        LocalDateTime.of(2023, 1, 1, 9, 0),
        LocalDateTime.of(2023, 1, 1, 10, 0),
        "", "", true, true);

    assertTrue(multiService.isBusyAt(testTime));
  }

  @Test
  public void testIsBusyAtWhenEmpty() {
    assertFalse(multiService.isBusyAt(LocalDateTime.now()));
  }
}
