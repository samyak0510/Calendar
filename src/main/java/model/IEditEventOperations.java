package model;

import java.time.LocalDateTime;

public interface IEditEventOperations {

  void editEvent(ICalendarModel model,
      String subject,
      LocalDateTime from,
      String property,
      String newValue,
      ICalendarService.EditMode mode) throws Exception;
}
