package controller;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import model.CalendarModel;
import model.SingleEvent;
import java.io.File;
import java.time.LocalDateTime;
import org.junit.Test;

/**
 * Unit tests for the ExportCalendarCommand class.
 */
public class ExportCalendarCommandTest {


  @Test
  public void testExportCalendarReturnMessage() throws Exception {
    CalendarModel calendar = new CalendarModel();
    LocalDateTime start = LocalDateTime.of(2025, 3, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 1, 10, 0);
    SingleEvent event = new SingleEvent("Meeting", start, end, "desc", "room", true);
    calendar.addEvent(event, false);

    String fileName = "test_export_message.csv";
    ExportCalendarCommand exportCmd = new ExportCalendarCommand(calendar, fileName);
    String result = exportCmd.execute();

    assertNotEquals("", result);
    assertTrue(result.startsWith("Calendar exported to:"));

    File exportedFile = new File(fileName);
    assertTrue(exportedFile.exists());
  }


  @Test
  public void testExportCalendarIOException() throws Exception {
    CalendarModel calendar = new CalendarModel();
    String invalidFileName = "/nonexistent_folder/test_export.csv";
    ExportCalendarCommand exportCmd = new ExportCalendarCommand(calendar, invalidFileName);
    String result = exportCmd.execute();
    assertTrue(result.startsWith("Export failed:"));
  }
}
