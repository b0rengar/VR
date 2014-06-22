package event;

/**
 * Types of event a fire alarm system triggers.
 *
 * @author Sebastian Mundry
 * @author Benjamin Gorny
 */
public enum FireAlarmSystemEventTypes {
  /** A fire was detected. */
  ALARM("Brandalarm"),
  /** The system noticed an error. */
  MALFUNCTION("Störung"),
  /** The system is in maintenance mode. */
  MAINTENANCE("Wartung"),
  /** Switch the unit back to detecting hazards. */
  RESET("Melder zurücksetzen"),
  
  READY("Melder bereit");

  /** The human-readable name of the event. Currently mainly in german. */
  private String m_name = null;

  /**
   * Creates a new event type.
   *
   * @param name The human-readable name of the new event.
   */
  private FireAlarmSystemEventTypes(String name) {
    m_name = name;
  }

  /**
   * Returns the human-readable name of the event type.
   *
   * @return The human-readable name of the event type.
   */
  public String getName() {
    return m_name;
  }
}
