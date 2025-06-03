package controller;

import model.ICalendarService;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class CreateCommandParser implements ICommandParser {

  private ICalendarService service;
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

  public CreateCommandParser(ICalendarService service) {
    this.service = service;
  }

  @Override
  public Command parse(String[] tokens) {
    try {
      boolean autoDecline = false;
      int index = 1;
      if (!tokens[index].equalsIgnoreCase("event")) {
        return () -> "Invalid create command: missing 'event' keyword.";
      }
      index++;
      if (tokens[index].equalsIgnoreCase("--autoDecline")) {
        autoDecline = true;
        index++;
      }
      String subject = tokens[index++];
      if (tokens[index].equalsIgnoreCase("from")) {
        index++;
        String startDTStr = tokens[index++];
        LocalDateTime start = CommandParserStatic.parseDateTimeStatic(startDTStr);
        if (!tokens[index].equalsIgnoreCase("to")) {
          return () -> "Expected 'to' after start time.";
        }
        index++;
        String endDTStr = tokens[index++];
        LocalDateTime end = CommandParserStatic.parseDateTimeStatic(endDTStr);
        if (index < tokens.length && tokens[index].equalsIgnoreCase("repeats")) {
          return getRecurringCommand(tokens, autoDecline, index, subject, start, end);
        } else {
          boolean isPublic = isABoolean(tokens, index);
          return new CreateEventCommand(service, autoDecline, subject,
              start, end, "", "", isPublic);
        }
      } else if (tokens[index].equalsIgnoreCase("on")) {
        index++;
        String dateStr = tokens[index++];
        LocalDateTime start;
        LocalDateTime end;
        if (dateStr.contains("T")) {
          // If a date-time string is provided for an all-day event, ignore the time part.
          LocalDateTime dt = LocalDateTime.parse(dateStr, DATE_TIME_FORMAT);
          start = dt.toLocalDate().atStartOfDay();
          end = dt.toLocalDate().atTime(23, 59);
        } else {
          LocalDate date = LocalDate.parse(dateStr, DATE_FORMAT);
          start = date.atStartOfDay();
          end = date.atTime(23, 59);
        }
        if (index < tokens.length && tokens[index].equalsIgnoreCase("repeats")) {
          return getRecurringCommand(tokens, autoDecline, index, subject, start, end);
        } else {
          boolean isPublic = isABoolean(tokens, index);
          return new CreateEventCommand(service, autoDecline, subject,
              start, end, "", "", isPublic);
        }
      } else {
        return () -> "Invalid create command format.";
      }
    } catch (Exception e) {
      return () -> "Error processing create command: " + e.getMessage();
    }
  }

  private static boolean isABoolean(String[] tokens, int index) {
    boolean isPublic = true;
    if (index < tokens.length) {
      String pubToken = tokens[index];
      if (pubToken.equalsIgnoreCase("public")) {
        isPublic = true;
      } else if (pubToken.equalsIgnoreCase("private")) {
        isPublic = false;
      }
      index++;
    }
    return isPublic;
  }

  private Command getRecurringCommand(String[] tokens, boolean autoDecline, int index, String subject,
      LocalDateTime start, LocalDateTime end) {
    index++; // skip "repeats"
    String weekdaysStr = tokens[index++];
    Set<DayOfWeek> recurrenceDays = parseWeekdays(weekdaysStr);
    Integer occCount = null;
    LocalDate recurrenceEndDate = null;
    if (index < tokens.length) {
      if (tokens[index].equalsIgnoreCase("for")) {
        index++;
        occCount = Integer.parseInt(tokens[index++]);
        if (index < tokens.length && tokens[index].equalsIgnoreCase("times")) {
          index++;
        } else {
          return () -> "Expected 'times' after occurrence count.";
        }
      } else if (tokens[index].equalsIgnoreCase("until")) {
        index++;
        String untilStr = tokens[index++];
        // Try to parse as date-time first; if that fails, parse as date.
        LocalDateTime untilDT;
        try {
          untilDT = LocalDateTime.parse(untilStr, DATE_TIME_FORMAT);
        } catch (Exception e) {
          untilDT = LocalDate.parse(untilStr, DATE_FORMAT).atStartOfDay();
        }
        recurrenceEndDate = untilDT.toLocalDate();
      }
    }
    boolean isPublic = isABoolean(tokens, index);
    return new CreateRecurringEventCommand(service, autoDecline, subject, start, end, "", "",
        isPublic, recurrenceDays, occCount == null ? -1 : occCount, recurrenceEndDate);
  }

  private Set<DayOfWeek> parseWeekdays(String weekdaysStr) {
    Set<DayOfWeek> days = new HashSet<>();
    for (char ch : weekdaysStr.toCharArray()) {
      switch (Character.toUpperCase(ch)) {
        case 'M':
          days.add(DayOfWeek.MONDAY);
          break;
        case 'T':
          days.add(DayOfWeek.TUESDAY);
          break;
        case 'W':
          days.add(DayOfWeek.WEDNESDAY);
          break;
        case 'R':
          days.add(DayOfWeek.THURSDAY);
          break;
        case 'F':
          days.add(DayOfWeek.FRIDAY);
          break;
        case 'S':
          days.add(DayOfWeek.SATURDAY);
          break;
        case 'U':
          days.add(DayOfWeek.SUNDAY);
          break;
        default:
          throw new IllegalArgumentException("Invalid weekday character: " + ch);
      }
    }
    return days;
  }
}
