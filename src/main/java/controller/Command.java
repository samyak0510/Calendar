package controller;

/**
 * Represents a command that can be executed.
 */
public interface Command {

  /**
   * Executes the command.
   *
   * @return the result of the command execution
   * @throws Exception if an error occurs during execution
   */
  String execute() throws Exception;
}
