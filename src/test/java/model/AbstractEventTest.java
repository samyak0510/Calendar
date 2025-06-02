package model;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import org.junit.Test;

/**
 * Unit tests for the AbstractEvent functionality via SingleEvent.
 */
public class AbstractEventTest {

  @Test
  public void testGetDescription() throws InvalidDateException {
    LocalDateTime start = LocalDateTime.of(2025, 3, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 1, 10, 0);
    SingleEvent event = new SingleEvent("Meeting", start, end, "Test Description", "Room101", true);
    assertEquals("Test Description", event.getDescription());
  }
}
