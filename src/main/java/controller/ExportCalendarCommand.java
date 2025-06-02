package controller;

import model.CalendarModel;
import java.io.IOException;
import model.CSVExporter;

/**
 * Exports the calendar data to a CSV file.
 */
public class ExportCalendarCommand implements Command {
  private CalendarModel calendar;
  private String fileName;

  /**
   * Constructs an ExportCalendarCommand.
   *
   * @param calendar the calendar model to export
   * @param fileName the name of the CSV file to create
   */
  public ExportCalendarCommand(CalendarModel calendar, String fileName) {
    this.calendar = calendar;
    this.fileName = fileName;
  }

  /**
   * Executes the export operation.
   *
   * @return a message indicating the CSV export path or an error message if the export fails
   * @throws Exception if an error occurs during export
   */
  @Override
  public String execute() throws Exception {
    try {
      String path = CSVExporter.exportToCSV(calendar, fileName);
      return "Calendar exported to: " + path;
    } catch (IOException e) {
      return "Export failed: " + e.getMessage();
    }
  }
}
