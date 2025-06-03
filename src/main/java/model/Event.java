package model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface defining the behavior of an event.
 */
public interface Event {
  /**
   * Returns the subject of the event.
   *
   * @return the event subject
   */
  String getSubject();

  /**
   * Returns the start date and time of the event.
   *
   * @return the start date and time
   */
  LocalDateTime getStartDateTime();

  /**
   * Returns the description of the event.
   *
   * @return the event description
   */
  String getDescription();

  /**
   * Returns the location of the event.
   *
   * @return the event location
   */
  String getLocation();

  /**
   * Indicates whether the event is public.
   *
   * @return true if the event is public, false otherwise
   */
  boolean isPublic();

  /**
   * Determines if this event conflicts with another event.
   *
   * @param other the other event to check against
   * @return true if there is a conflict, false otherwise
   */
  boolean conflictsWith(Event other);

  /**
   * Returns a list of event occurrences. For a single event, this will contain only itself.
   *
   * @return a list of occurrences
   */
  List<Event> getOccurrences();

  boolean isAutoDecline();
}

