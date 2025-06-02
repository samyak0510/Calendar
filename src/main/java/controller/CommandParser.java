package controller;

import model.CalendarModel;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses command line input and creates the corresponding Command objects.
 */
public class CommandParser {

  private Map<String, ICommandParser> parserMap;

  /**
   * Constructs a CommandParser with the specified calendar model.
   *
   * @param calendar the calendar model to be used for command parsing
   */
  public CommandParser(CalendarModel calendar) {
    parserMap = new HashMap<>();
    parserMap.put("create", new CreateCommandParser(calendar));
    parserMap.put("edit", new EditCommandParser(calendar));
    parserMap.put("print", new PrintCommandParser(calendar));
    parserMap.put("export", new ExportCommandParser(calendar));
    parserMap.put("show", new ShowStatusCommandParser(calendar));
  }

  /**
   * Parses the provided command line string into a Command.
   *
   * @param commandLine the input command string
   * @return a Command object corresponding to the input, or null if the input is empty
   */
  public Command parse(String commandLine) {
    if (commandLine == null || commandLine.trim().isEmpty()) {
      return null;
    }

    String[] tokens = commandLine.trim().split("\\s+");
    String cmdType = tokens[0].toLowerCase();
    if (cmdType.equals("exit")) {
      return () -> "exit";
    }
    ICommandParser parser = parserMap.get(cmdType);
    if (parser != null) {
      return parser.parse(tokens);
    } else {
      return () -> "Unknown command.";
    }
  }
}
