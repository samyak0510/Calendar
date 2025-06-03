package model;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CalendarService implements ICalendarService {

  private ICalendarModel model;
  private Exporter exporter;
  private IEventPrinter printer;
  private IEditEventOperations editOps;

  public CalendarService(ICalendarModel model) {
    this.model = model;
    this.exporter = new CSVExporter();
    this.printer = new EventPrinter(model);
    this.editOps = new EditEventOperations();
  }

  @Override
  public void addSingleEvent(String subject, LocalDateTime start, LocalDateTime end,
      String description, String location, boolean isPublic, boolean autoDecline)
      throws InvalidDateException, EventConflictException {
    // Create the single event internally and add it via the model.
    AbstractEvent event = new SingleEvent(subject, start, end, description, location, isPublic);
    event.setAutoDecline(autoDecline);
    model.addEvent(event, autoDecline);
  }

  @Override
  public void addRecurringEvent(String subject, LocalDateTime start, LocalDateTime end,
      String description, String location, boolean isPublic,
      java.util.Set<java.time.DayOfWeek> recurrenceDays,
      int occurrenceCount, java.time.LocalDate recurrenceEndDate, boolean autoDecline)
      throws InvalidDateException, EventConflictException {

    AbstractEvent event = new RecurringEvent(subject, start, end, description, location, isPublic,
        recurrenceDays, occurrenceCount, recurrenceEndDate);
    event.setAutoDecline(autoDecline);
    model.addEvent(event, autoDecline);
  }

  @Override
  public List<Event> getEventsOn(LocalDate date) {
    return model.getEventsOn(date);
  }

  @Override
  public List<Event> getAllEvents() {
    return model.getAllEvents();
  }

  @Override
  public boolean isBusyAt(LocalDateTime dateTime) {
    return model.isBusyAt(dateTime);
  }


  @Override
  public void editEvent(String subject, LocalDateTime from, String property, String newValue,
      EditMode mode) throws Exception {

    editOps.editEvent(model, subject, from, property, newValue, mode);

  }


  @Override
  public String exporttoCSV(String format, String path) throws IOException {
    if (format.equalsIgnoreCase("csv")) {
      return exporter.export(model, path);
    } else {
      throw new UnsupportedOperationException("Export format not supported: " + format);
    }
  }

  @Override
  public String printEventsOn(LocalDate date) throws Exception {
    return printer.printEventsOn(date);
  }

  @Override
  public String printEventsRange(LocalDateTime start, LocalDateTime end) throws Exception {
    return printer.printEventsRange(start, end);
  }
}
