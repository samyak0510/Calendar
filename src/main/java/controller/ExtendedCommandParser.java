package controller;

import java.util.HashMap;
import java.util.Map;
import model.IMultiCalendarService;

/**
 * An extended parser that handles additional calendar commands and builds on a base parser and adds
 * support for multi-calendar operations.
 */
public class ExtendedCommandParser implements IHeadCommandParser {

  private Map<String, ICommandParser> parserMap;
  private IMultiCalendarService multiCalendarService;
  private IHeadCommandParser baseParser;

  /**
   * Constructs an extended parser with a multi-calendar service. Initializes parsers for new
   * commands like create, edit, and copy operations.
   *
   * @param multiCalendarService The service managing multiple calendars
   */
  public ExtendedCommandParser(IMultiCalendarService multiCalendarService) {
    this.multiCalendarService = multiCalendarService;
    this.baseParser = new HeadCommandParser(multiCalendarService);
    parserMap = new HashMap<>();
    parserMap.put("create calendar", new CreateCalendarCommandParser(this.multiCalendarService));
    parserMap.put("edit calendar", new EditCalendarCommandParser(this.multiCalendarService));
    parserMap.put("use calendar", new UseCalendarCommandParser(this.multiCalendarService));
    parserMap.put("copy event", new CopyEventCommandParser(this.multiCalendarService));
    parserMap.put("copy events on", new CopyEventsOnCommandParser(this.multiCalendarService));
    parserMap.put("copy events between",
        new CopyEventsBetweenCommandParser(this.multiCalendarService));
  }

  /**
   * Parses a command line into a Command object.
   *
   * @param commandLine The input command string to parse
   * @return A Command object, or null if the input is empty
   */
  @Override
  public Command parse(String commandLine) {
    if (commandLine == null || commandLine.trim().isEmpty()) {
      return null;
    }
    String[] tokens = commandLine.trim().split("\\s+");
    if (tokens.length >= 3) {
      String possibleKey3 = tokens[0].toLowerCase() + " " + tokens[1].toLowerCase()
          + " " + tokens[2].toLowerCase();
      if (parserMap.containsKey(possibleKey3)) {
        return parserMap.get(possibleKey3).parse(tokens);
      }
    }
    if (tokens.length >= 2) {
      String possibleKey = tokens[0].toLowerCase() + " " + tokens[1].toLowerCase();
      if (parserMap.containsKey(possibleKey)) {
        return parserMap.get(possibleKey).parse(tokens);
      }
    }
    return baseParser.parse(commandLine);
  }
}