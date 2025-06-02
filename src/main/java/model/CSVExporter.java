package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

/**
 * A utility class for exporting calendar events to a CSV file.
 */
public class CSVExporter {

  /**
   * Exports the events from the given calendar to a CSV file with the specified file name.
   *
   * @param calendar the calendar model containing the events
   * @param fileName the name of the CSV file to create
   * @return the file path of the created CSV file
   * @throws IOException if an I/O error occurs during writing
   */
  public static String exportToCSV(CalendarModel calendar, String fileName) throws IOException {
    String filePath = Paths.get(System.getProperty("user.dir"), fileName).toString();
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
      writer.write("Subject,Start Date,Start Time,End Date,End Time,Description,Location,Public");
      writer.newLine();

      List<Event> events = calendar.getAllEvents();

      for (Event event : events) {
        if (event instanceof SingleEvent) {
          SingleEvent se = (SingleEvent) event;
          writeSingleEventRow(se, writer);
        } else if (event instanceof RecurringEvent) {
          RecurringEvent re = (RecurringEvent) event;
          for (SingleEvent occurrence : re.generateOccurrences()) {
            writeSingleEventRow(occurrence, writer);
          }
        }
      }
    }
    return filePath;
  }

  /**
   * Writes a single event row to the CSV file.
   *
   * @param se     the SingleEvent instance to write
   * @param writer the BufferedWriter for the CSV file
   * @throws IOException if writing fails
   */
  private static void writeSingleEventRow(SingleEvent se,
      BufferedWriter writer) throws IOException {
    StringBuilder row = new StringBuilder();
    LocalDateTime start = se.getStartDateTime();
    LocalDateTime end = se.getEffectiveEndDateTime();
    row.append(se.getSubject()).append(",")
        .append(start.toLocalDate()).append(",")
        .append(start.toLocalTime()).append(",")
        .append(end.toLocalDate()).append(",")
        .append(end.toLocalTime()).append(",")
        .append(se.getDescription()).append(",")
        .append(se.getLocation()).append(",")
        .append(se.isPublic());
    writer.write(row.toString());
    writer.newLine();
  }
}

