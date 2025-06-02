package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the CSVExporter class.
 */
public class CSVExporterTest {

  private CalendarModel calendar;
  private final String testFile = "test_calendar.csv";


  @Before
  public void setUp() throws InvalidDateException, EventConflictException {
    calendar = new CalendarModel();
  }

  /**
   * Cleans up the test CSV file after each test.
   */
  @After
  public void tearDown() {
    File file = new File(testFile);
    if (file.exists()) {
      file.delete();
    }
  }


  @Test
  public void testExportCSVWithSingleEvent() throws IOException,
      InvalidDateException, EventConflictException {
    LocalDateTime start = LocalDateTime.of(2025, 3, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 1, 10, 0);
    SingleEvent event = new SingleEvent("Meeting", start, end,
        "desc", "Room 101", true);
    calendar.addEvent(event, false);

    String filePath = CSVExporter.exportToCSV(calendar, testFile);
    File csvFile = new File(filePath);
    assertTrue(csvFile.exists());

    BufferedReader reader = new BufferedReader(new FileReader(csvFile));
    String header = reader.readLine();
    assertEquals("Subject,Start Date,Start Time,"
        + "End Date,End Time,Description,Location,Public", header);
    String dataRow = reader.readLine();
    assertNotNull(dataRow);
    String[] parts = dataRow.split(",");
    assertEquals("Meeting", parts[0]);
    assertEquals(start.toLocalDate().toString(), parts[1]);
    assertEquals(start.toLocalTime().toString(), parts[2]);
    assertEquals(end.toLocalDate().toString(), parts[3]);
    assertEquals(end.toLocalTime().toString(), parts[4]);
    assertEquals("desc", parts[5]);
    assertEquals("Room 101", parts[6]);
    assertEquals("true", parts[7]);
    reader.close();
  }


  @Test
  public void testExportCSVWithRecurringEvent() throws IOException,
      InvalidDateException, EventConflictException {
    LocalDateTime start = LocalDateTime.of(2025, 3,
        3, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3,
        3, 9, 15);
    Set<DayOfWeek> days = new HashSet<>();
    days.add(DayOfWeek.MONDAY);
    RecurringEvent recurring = new RecurringEvent("Standup", start,
        end, "", "", true, days, 2, null);
    calendar.addEvent(recurring, false);

    String filePath = CSVExporter.exportToCSV(calendar, testFile);
    File csvFile = new File(filePath);
    assertTrue(csvFile.exists());

    BufferedReader reader = new BufferedReader(new FileReader(csvFile));
    String header = reader.readLine();
    assertEquals("Subject,Start Date,Start Time,"
        + "End Date,End Time,Description,Location,Public", header);
    String row1 = reader.readLine();
    String row2 = reader.readLine();
    assertNotNull(row1);
    assertNotNull(row2);
    assertTrue(reader.readLine() == null);
    reader.close();
  }


  @Test
  public void testExportCSVWithEmptyCalendar() throws IOException {
    String filePath = CSVExporter.exportToCSV(calendar, testFile);
    File csvFile = new File(filePath);
    assertTrue(csvFile.exists());

    BufferedReader reader = new BufferedReader(new FileReader(csvFile));
    String header = reader.readLine();
    assertEquals("Subject,Start Date,Start Time,"
        + "End Date,End Time,Description,Location,Public", header);
    String data = reader.readLine();
    assertTrue(data == null);
    reader.close();
  }


  @Test
  public void testReturnFilePath() throws IOException,
      InvalidDateException, EventConflictException {
    LocalDateTime start = LocalDateTime.of(2025,
        3, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025,
        3, 1, 10, 0);
    SingleEvent event = new SingleEvent("Meeting", start,
        end, "", "", true);
    calendar.addEvent(event, false);

    String filePath = CSVExporter.exportToCSV(calendar, testFile);
    assertNotNull(filePath);
    assertTrue(filePath.contains(testFile));
  }
}

