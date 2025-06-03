package controller;

import java.io.IOException;
import model.ICalendarService;


/**
 * Exports the calendar data to a CSV file.
 */
public class ExportCalendarCommand implements Command {
  private ICalendarService calendarService;
  private String fileName;


  public ExportCalendarCommand(ICalendarService calendarService, String fileName) {
    this.calendarService = calendarService;
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
      String path = calendarService.exporttoCSV("csv", fileName);
      return "Calendar exported to: " + path;
    } catch (IOException e) {
      return "Export failed: " + e.getMessage();
    }
  }
}
