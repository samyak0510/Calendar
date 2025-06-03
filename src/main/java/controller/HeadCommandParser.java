package controller;

import model.ICalendarService;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses command line input and creates the corresponding Command objects.
 */
public class HeadCommandParser implements IHeadCommandParser {

  private Map<String, ICommandParser> parserMap;


  public HeadCommandParser(ICalendarService service) {
    parserMap = new HashMap<>();
    parserMap.put("create", new CreateCommandParser(service));
    parserMap.put("edit", new EditCommandParser(service));
    parserMap.put("print", new PrintCommandParser(service));
    parserMap.put("export", new ExportCommandParser(service));
    parserMap.put("show", new ShowStatusCommandParser(service));
  }

  /**
   * Parses the provided command line string into a Command.
   *
   * @param commandLine the input command string
   * @return a Command object corresponding to the input, or null if the input is empty
   */
  @Override
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
